package team.projectpulse.ram.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.projectpulse.ram.dto.EvaluationHistoryResponse;
import team.projectpulse.ram.dto.EvaluationWeekSummaryResponse;
import team.projectpulse.ram.dto.PeerEvaluationBatchRequest;
import team.projectpulse.ram.dto.PeerEvaluationScoreRequest;
import team.projectpulse.ram.dto.PeerEvaluationSubmissionRequest;
import team.projectpulse.ram.exception.InvalidStudentRequestException;
import team.projectpulse.ram.exception.ResourceNotFoundException;
import team.projectpulse.ram.model.Criterion;
import team.projectpulse.ram.model.PeerEvaluation;
import team.projectpulse.ram.model.PeerEvaluationScore;
import team.projectpulse.ram.model.Rubric;
import team.projectpulse.ram.model.Section;
import team.projectpulse.ram.model.Student;
import team.projectpulse.ram.repository.CriterionRepository;
import team.projectpulse.ram.repository.PeerEvaluationRepository;
import team.projectpulse.ram.repository.SectionRepository;
import team.projectpulse.ram.repository.StudentRepository;

@Service
public class EvaluationService {

    private final PeerEvaluationRepository peerEvaluationRepository;
    private final StudentRepository studentRepository;
    private final SectionRepository sectionRepository;
    private final CriterionRepository criterionRepository;

    public EvaluationService(
            PeerEvaluationRepository peerEvaluationRepository,
            StudentRepository studentRepository,
            SectionRepository sectionRepository,
            CriterionRepository criterionRepository
    ) {
        this.peerEvaluationRepository = peerEvaluationRepository;
        this.studentRepository = studentRepository;
        this.sectionRepository = sectionRepository;
        this.criterionRepository = criterionRepository;
    }

    @Transactional(readOnly = true)
    public EvaluationHistoryResponse getEvaluationHistory(
            Long studentId,
            Long sectionId,
            LocalDate startWeek,
            LocalDate endWeek
    ) {
        Student student = studentRepository.findByIdWithDetails(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));
        Section section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Section not found with id: " + sectionId));
        validateWeekRange(section, startWeek, endWeek);
        ensureStudentInSection(student, section);

        List<PeerEvaluation> evaluations = peerEvaluationRepository.findAllForRevieweeBetweenWeeks(
                studentId,
                sectionId,
                startWeek,
                endWeek
        );

        List<Criterion> criteria = rubricCriteria(section.getRubric());
        Map<LocalDate, List<PeerEvaluation>> grouped = new LinkedHashMap<>();
        for (PeerEvaluation evaluation : evaluations) {
            grouped.computeIfAbsent(evaluation.getWeekStart(), ignored -> new ArrayList<>()).add(evaluation);
        }

        List<EvaluationWeekSummaryResponse> rows = grouped.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> summarizeWeek(entry.getKey(), entry.getValue(), criteria))
                .toList();

        return new EvaluationHistoryResponse(rows);
    }

    @Transactional
    public int savePeerEvaluations(Long reviewerId, Long sectionId, LocalDate weekStart, PeerEvaluationBatchRequest request) {
        Student reviewer = studentRepository.findByIdWithDetails(reviewerId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + reviewerId));
        Section section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Section not found with id: " + sectionId));
        validateWeekRange(section, weekStart, weekStart);
        ensureStudentInSection(reviewer, section);

        if (request == null || request.getSubmissions() == null || request.getSubmissions().isEmpty()) {
            throw new InvalidStudentRequestException("At least one evaluation submission is required.");
        }

        List<Criterion> criteria = rubricCriteria(section.getRubric());
        Map<Long, Criterion> criteriaById = criteria.stream().collect(
                java.util.stream.Collectors.toMap(Criterion::getId, criterion -> criterion)
        );

        for (PeerEvaluationSubmissionRequest submission : request.getSubmissions()) {
            validateSubmission(submission, reviewerId);

            Student reviewee = studentRepository.findByIdWithDetails(submission.getRevieweeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + submission.getRevieweeId()));
            ensureStudentInSection(reviewee, section);

            PeerEvaluation evaluation = peerEvaluationRepository.findExistingSubmission(
                            reviewerId,
                            reviewee.getId(),
                            sectionId,
                            weekStart
                    )
                    .orElseGet(PeerEvaluation::new);

            evaluation.setWeekStart(weekStart);
            evaluation.setSection(section);
            evaluation.setReviewer(reviewer);
            evaluation.setReviewee(reviewee);
            evaluation.setPublicComment(Optional.ofNullable(submission.getPublicComment()).orElse("").trim());
            evaluation.getScores().clear();

            if (submission.getScores().size() != criteria.size()) {
                throw new InvalidStudentRequestException("Each submission must include a score for every criterion.");
            }

            for (PeerEvaluationScoreRequest scoreRequest : submission.getScores()) {
                Criterion criterion = criteriaById.get(scoreRequest.getCriterionId());
                if (criterion == null) {
                    throw new InvalidStudentRequestException("Criterion does not belong to the selected section rubric.");
                }
                if (scoreRequest.getScore() == null || scoreRequest.getScore() < 0 || scoreRequest.getScore() > criterion.getMaxScore()) {
                    throw new InvalidStudentRequestException(
                            "Score for " + criterion.getName() + " must be between 0 and " + criterion.getMaxScore() + "."
                    );
                }

                PeerEvaluationScore score = new PeerEvaluationScore();
                score.setCriterion(criterion);
                score.setScore(scoreRequest.getScore());
                evaluation.addScore(score);
            }

            peerEvaluationRepository.save(evaluation);
        }

        return request.getSubmissions().size();
    }

    private EvaluationWeekSummaryResponse summarizeWeek(
            LocalDate weekStart,
            List<PeerEvaluation> evaluations,
            List<Criterion> criteria
    ) {
        double totalAverage = evaluations.stream()
                .mapToDouble(evaluation -> evaluation.getScores().stream()
                        .mapToDouble(PeerEvaluationScore::getScore)
                        .sum())
                .average()
                .orElse(0.0);

        List<Double> criterionScores = criteria.stream()
                .map(criterion -> evaluations.stream()
                        .flatMap(evaluation -> evaluation.getScores().stream())
                        .filter(score -> score.getCriterion().getId().equals(criterion.getId()))
                        .mapToInt(PeerEvaluationScore::getScore)
                        .average()
                        .orElse(0.0))
                .map(value -> Math.round(value * 100.0) / 100.0)
                .toList();

        return new EvaluationWeekSummaryResponse(
                weekStart,
                weekStart.plusDays(6),
                Math.round(totalAverage * 100.0) / 100.0,
                criterionScores
        );
    }

    private void validateSubmission(PeerEvaluationSubmissionRequest submission, Long reviewerId) {
        if (submission == null || submission.getRevieweeId() == null) {
            throw new InvalidStudentRequestException("Each submission must include a revieweeId.");
        }
        if (submission.getRevieweeId().equals(reviewerId)) {
            throw new InvalidStudentRequestException("Students cannot evaluate themselves.");
        }
        if (submission.getScores() == null || submission.getScores().isEmpty()) {
            throw new InvalidStudentRequestException("Each submission must include scores.");
        }
    }

    private List<Criterion> rubricCriteria(Rubric rubric) {
        if (rubric == null) {
            throw new InvalidStudentRequestException("Section does not have a rubric.");
        }

        return rubric.getCriteria().stream()
                .sorted(Comparator.comparing(Criterion::getId))
                .toList();
    }

    private void validateWeekRange(Section section, LocalDate startWeek, LocalDate endWeek) {
        if (startWeek == null || endWeek == null) {
            throw new InvalidStudentRequestException("Week range is required.");
        }
        if (section.getStartDate() == null || section.getEndDate() == null) {
            throw new InvalidStudentRequestException("Section week range is not configured.");
        }
        if (endWeek.isBefore(startWeek)) {
            throw new InvalidStudentRequestException("End week cannot be before start week.");
        }
        if (startWeek.isBefore(section.getStartDate()) || endWeek.isAfter(section.getEndDate())) {
            throw new InvalidStudentRequestException("Requested weeks are outside the section range.");
        }
    }

    private void ensureStudentInSection(Student student, Section section) {
        if (student.getSection() == null || !student.getSection().getId().equals(section.getId())) {
            throw new InvalidStudentRequestException("Student does not belong to the selected section.");
        }
    }
}
