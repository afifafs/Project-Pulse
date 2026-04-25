package team.projectpulse.ram.service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.projectpulse.ram.dto.PeerEvaluationRatingRequest;
import team.projectpulse.ram.dto.PeerEvaluationReportResponse;
import team.projectpulse.ram.dto.PeerEvaluationRequest;
import team.projectpulse.ram.dto.PeerEvaluationResponse;
import team.projectpulse.ram.dto.SectionPeerEvaluationReportResponse;
import team.projectpulse.ram.dto.SectionPeerEvaluationStudentSummary;
import team.projectpulse.ram.exception.InvalidPeerEvaluationRequestException;
import team.projectpulse.ram.exception.ResourceNotFoundException;
import team.projectpulse.ram.model.Criterion;
import team.projectpulse.ram.model.Instructor;
import team.projectpulse.ram.model.PeerEvaluation;
import team.projectpulse.ram.model.PeerEvaluationRating;
import team.projectpulse.ram.model.Section;
import team.projectpulse.ram.model.SectionWeek;
import team.projectpulse.ram.model.Student;
import team.projectpulse.ram.repository.InstructorRepository;
import team.projectpulse.ram.repository.PeerEvaluationRepository;
import team.projectpulse.ram.repository.StudentRepository;

@Service
public class PeerEvaluationService {

    private final PeerEvaluationRepository peerEvaluationRepository;
    private final StudentRepository studentRepository;
    private final InstructorRepository instructorRepository;

    public PeerEvaluationService(
            PeerEvaluationRepository peerEvaluationRepository,
            StudentRepository studentRepository,
            InstructorRepository instructorRepository
    ) {
        this.peerEvaluationRepository = peerEvaluationRepository;
        this.studentRepository = studentRepository;
        this.instructorRepository = instructorRepository;
    }

    @Transactional
    public PeerEvaluationResponse submitEvaluation(PeerEvaluationRequest request) {
        validateEvaluationRequest(request);

        Student evaluator = studentRepository.findById(request.getEvaluatorId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + request.getEvaluatorId()));
        Student evaluatee = studentRepository.findById(request.getEvaluateeId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + request.getEvaluateeId()));

        if (evaluator.getId().equals(evaluatee.getId())) {
            throw new InvalidPeerEvaluationRequestException("Students cannot evaluate themselves.");
        }

        if (evaluator.getTeam() == null || evaluatee.getTeam() == null
                || !evaluator.getTeam().getId().equals(evaluatee.getTeam().getId())) {
            throw new InvalidPeerEvaluationRequestException("Students can evaluate only teammates.");
        }

        Section section = evaluatee.getSection();
        if (section == null || section.getRubric() == null || section.getRubric().getCriteria().isEmpty()) {
            throw new InvalidPeerEvaluationRequestException("The evaluatee's section must have a rubric.");
        }

        int weekNumber = request.getWeekNumber() == null ? resolvePreviousWeekNumber(section) : request.getWeekNumber();
        if (weekNumber <= 0) {
            throw new InvalidPeerEvaluationRequestException("weekNumber must be greater than 0.");
        }

        Map<Long, Criterion> criteriaById = section.getRubric().getCriteria().stream()
                .collect(java.util.stream.Collectors.toMap(Criterion::getId, criterion -> criterion));

        PeerEvaluation evaluation = new PeerEvaluation();
        evaluation.setEvaluator(evaluator);
        evaluation.setEvaluatee(evaluatee);
        evaluation.setWeekNumber(weekNumber);
        evaluation.setPublicComment(trimToNull(request.getPublicComment()));
        evaluation.setPrivateComment(trimToNull(request.getPrivateComment()));
        evaluation.setSubmittedAt(LocalDateTime.now());

        double totalScore = 0.0;
        for (PeerEvaluationRatingRequest ratingRequest : request.getRatings()) {
            Criterion criterion = criteriaById.get(ratingRequest.getCriterionId());
            if (criterion == null) {
                throw new InvalidPeerEvaluationRequestException(
                        "Criterion not found on section rubric: " + ratingRequest.getCriterionId()
                );
            }

            if (ratingRequest.getScore() == null || ratingRequest.getScore() < 0 || ratingRequest.getScore() > criterion.getMaxScore()) {
                throw new InvalidPeerEvaluationRequestException(
                        "Score must be between 0 and " + criterion.getMaxScore() + " for criterion " + criterion.getName()
                );
            }

            PeerEvaluationRating rating = new PeerEvaluationRating();
            rating.setCriterionId(criterion.getId());
            rating.setCriterionName(criterion.getName());
            rating.setScore(ratingRequest.getScore());
            rating.setMaxScore(criterion.getMaxScore().doubleValue());
            rating.setPeerEvaluation(evaluation);
            evaluation.getRatings().add(rating);
            totalScore += ratingRequest.getScore();
        }

        evaluation.setTotalScore(totalScore);
        return PeerEvaluationResponse.fromEntity(peerEvaluationRepository.save(evaluation), true);
    }

    @Transactional(readOnly = true)
    public PeerEvaluationReportResponse getStudentPeerEvaluationReport(Long studentId, Integer weekNumber, boolean includePrivateComments) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));

        List<PeerEvaluation> evaluations = weekNumber == null
                ? peerEvaluationRepository.findByEvaluateeIdOrderByWeekNumberDescIdDesc(studentId)
                : peerEvaluationRepository.findByEvaluateeIdAndWeekNumberOrderByIdDesc(studentId, weekNumber);

        double averageScore = evaluations.stream()
                .map(PeerEvaluation::getTotalScore)
                .filter(score -> score != null)
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);

        return new PeerEvaluationReportResponse(
                student.getId(),
                student.getFirstName() + " " + student.getLastName(),
                weekNumber,
                evaluations.size(),
                averageScore,
                evaluations.stream()
                        .map(evaluation -> PeerEvaluationResponse.fromEntity(evaluation, includePrivateComments))
                        .toList()
        );
    }

    @Transactional(readOnly = true)
    public SectionPeerEvaluationReportResponse getSectionPeerEvaluationReport(Long instructorId, Long sectionId, Integer weekNumber) {
        Instructor instructor = instructorRepository.findById(instructorId)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found with id: " + instructorId));

        if (instructor.getSection() == null || !instructor.getSection().getId().equals(sectionId)) {
            throw new InvalidPeerEvaluationRequestException("Instructor does not have access to this section.");
        }

        List<PeerEvaluation> evaluations = peerEvaluationRepository.findSectionEvaluations(sectionId, weekNumber);
        Map<Long, List<PeerEvaluation>> evaluationsByStudent = evaluations.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        evaluation -> evaluation.getEvaluatee().getId(),
                        LinkedHashMap::new,
                        java.util.stream.Collectors.toList()
                ));

        List<SectionPeerEvaluationStudentSummary> studentSummaries = evaluationsByStudent.entrySet().stream()
                .map(entry -> {
                    Student evaluatee = entry.getValue().get(0);
                    double averageScore = entry.getValue().stream()
                            .map(PeerEvaluation::getTotalScore)
                            .filter(score -> score != null)
                            .mapToDouble(Double::doubleValue)
                            .average()
                            .orElse(0.0);

                    return new SectionPeerEvaluationStudentSummary(
                            evaluatee.getId(),
                            evaluatee.getFirstName() + " " + evaluatee.getLastName(),
                            entry.getValue().size(),
                            averageScore
                    );
                })
                .sorted(Comparator.comparing(SectionPeerEvaluationStudentSummary::getStudentName, String.CASE_INSENSITIVE_ORDER))
                .toList();

        double averageScore = evaluations.stream()
                .map(PeerEvaluation::getTotalScore)
                .filter(score -> score != null)
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);

        String sectionName = instructor.getSection() == null ? null : instructor.getSection().getName();
        return new SectionPeerEvaluationReportResponse(
                sectionId,
                sectionName,
                weekNumber,
                evaluations.size(),
                averageScore,
                studentSummaries
        );
    }

    @Transactional(readOnly = true)
    public PeerEvaluationReportResponse getInstructorStudentReport(Long instructorId, Long studentId, Integer weekNumber) {
        Instructor instructor = instructorRepository.findById(instructorId)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found with id: " + instructorId));
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));

        if (instructor.getSection() == null || student.getSection() == null
                || !instructor.getSection().getId().equals(student.getSection().getId())) {
            throw new InvalidPeerEvaluationRequestException("Instructor does not have access to this student.");
        }

        return getStudentPeerEvaluationReport(studentId, weekNumber, true);
    }

    private void validateEvaluationRequest(PeerEvaluationRequest request) {
        if (request == null) {
            throw new InvalidPeerEvaluationRequestException("Peer evaluation request is required.");
        }

        if (request.getEvaluatorId() == null) {
            throw new InvalidPeerEvaluationRequestException("evaluatorId is required.");
        }

        if (request.getEvaluateeId() == null) {
            throw new InvalidPeerEvaluationRequestException("evaluateeId is required.");
        }

        if (request.getRatings() == null || request.getRatings().isEmpty()) {
            throw new InvalidPeerEvaluationRequestException("At least one rating is required.");
        }
    }

    private int resolvePreviousWeekNumber(Section section) {
        List<SectionWeek> activeWeeks = section.getWeeks().stream()
                .filter(week -> Boolean.TRUE.equals(week.getActive()))
                .sorted(Comparator.comparing(SectionWeek::getWeekNumber))
                .toList();

        if (activeWeeks.isEmpty()) {
            return 1;
        }

        if (activeWeeks.size() == 1) {
            return activeWeeks.get(0).getWeekNumber();
        }

        return activeWeeks.get(activeWeeks.size() - 2).getWeekNumber();
    }

    private String trimToNull(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        return value.trim();
    }
}
