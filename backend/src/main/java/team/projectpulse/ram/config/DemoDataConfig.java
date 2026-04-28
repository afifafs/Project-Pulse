package team.projectpulse.ram.config;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import team.projectpulse.ram.model.ActivityEntry;
import team.projectpulse.ram.model.Criterion;
import team.projectpulse.ram.model.Instructor;
import team.projectpulse.ram.model.PeerEvaluation;
import team.projectpulse.ram.model.PeerEvaluationScore;
import team.projectpulse.ram.model.Rubric;
import team.projectpulse.ram.model.Section;
import team.projectpulse.ram.model.Student;
import team.projectpulse.ram.model.Team;
import team.projectpulse.ram.repository.ActivityEntryRepository;
import team.projectpulse.ram.repository.InstructorRepository;
import team.projectpulse.ram.repository.PeerEvaluationRepository;
import team.projectpulse.ram.repository.RubricRepository;
import team.projectpulse.ram.repository.SectionRepository;

@Configuration
public class DemoDataConfig {

    @Bean
    CommandLineRunner seedDemoData(
            SectionRepository sectionRepository,
            RubricRepository rubricRepository,
            InstructorRepository instructorRepository,
            ActivityEntryRepository activityEntryRepository,
            PeerEvaluationRepository peerEvaluationRepository
    ) {
        return args -> {
            if (sectionRepository.count() > 0 || rubricRepository.count() > 0) {
                return;
            }

            Rubric sprintRubric = createRubric(
                    "Sprint Review Rubric",
                    "Balanced scoring for collaboration, delivery, and communication.",
                    List.of(
                            criterion("Ownership", "Takes initiative and follows through on assigned work.", 5),
                            criterion("Collaboration", "Supports teammates and contributes to a healthy team rhythm.", 5),
                            criterion("Quality", "Ships work that is tested, polished, and maintainable.", 5),
                            criterion("Communication", "Keeps the team aligned with clear updates and feedback.", 5)
                    )
            );
            rubricRepository.save(sprintRubric);

            Rubric capstoneRubric = createRubric(
                    "Capstone Milestone Rubric",
                    "Used for milestone check-ins across product, engineering, and presentation readiness.",
                    List.of(
                            criterion("Product Thinking", "Understands user needs and makes thoughtful trade-offs.", 10),
                            criterion("Execution", "Delivers working software and closes the loop on feedback.", 10),
                            criterion("Reliability", "Keeps the system stable and responds well to issues.", 10)
                    )
            );
            rubricRepository.save(capstoneRubric);

            Section uxSection = new Section();
            uxSection.setName("UX Research Studio");
            uxSection.setCourseCode("DSGN-410");
            uxSection.setStartDate(LocalDate.of(2026, 1, 12));
            uxSection.setEndDate(LocalDate.of(2026, 5, 8));
            uxSection.setRubric(sprintRubric);

            Instructor maya = instructorRepository.save(instructor("Dr.", "Maya Chen", "maya.chen@example.edu"));
            Instructor jonah = instructorRepository.save(instructor("Prof.", "Jonah Ellis", "jonah.ellis@example.edu"));
            uxSection.setInstructors(new LinkedHashSet<>(List.of(maya, jonah)));

            Team northStar = team("North Star", "Accessibility-first research squad.", "https://northstar.example.edu", uxSection);
            Team signalLab = team("Signal Lab", "Prototype and validation team.", "https://signallab.example.edu", uxSection);
            northStar.setInstructors(new LinkedHashSet<>(List.of(maya)));
            signalLab.setInstructors(new LinkedHashSet<>(List.of(jonah)));

            Student anika = student("Anika", "Patel", "anika.patel@example.edu", uxSection, northStar);
            Student drew = student("Drew", "Morales", "drew.morales@example.edu", uxSection, northStar);
            Student lila = student("Lila", "Nguyen", "lila.nguyen@example.edu", uxSection, signalLab);
            Student marco = student("Marco", "Silva", "marco.silva@example.edu", uxSection, signalLab);
            Student june = student("June", "Bennett", "june.bennett@example.edu", uxSection, null);

            northStar.setStudents(List.of(anika, drew));
            signalLab.setStudents(List.of(lila, marco));
            uxSection.setTeams(List.of(northStar, signalLab));
            uxSection.setStudents(List.of(anika, drew, lila, marco, june));

            Section capstoneSection = new Section();
            capstoneSection.setName("Product Engineering Capstone");
            capstoneSection.setCourseCode("CS-498");
            capstoneSection.setStartDate(LocalDate.of(2026, 1, 12));
            capstoneSection.setEndDate(LocalDate.of(2026, 5, 8));
            capstoneSection.setRubric(capstoneRubric);

            Instructor elena = instructorRepository.save(instructor("Prof.", "Elena Brooks", "elena.brooks@example.edu"));
            capstoneSection.setInstructors(new LinkedHashSet<>(List.of(elena)));

            Team aurora = team("Aurora", "Platform and release engineering team.", "https://aurora.example.edu", capstoneSection);
            Team relay = team("Relay", "Workflow automation team.", "https://relay.example.edu", capstoneSection);
            aurora.setInstructors(new LinkedHashSet<>(List.of(elena)));

            Student sara = student("Sara", "Kim", "sara.kim@example.edu", capstoneSection, aurora);
            Student noah = student("Noah", "Reed", "noah.reed@example.edu", capstoneSection, aurora);
            Student tessa = student("Tessa", "Owens", "tessa.owens@example.edu", capstoneSection, relay);
            Student julian = student("Julian", "Price", "julian.price@example.edu", capstoneSection, relay);

            aurora.setStudents(List.of(sara, noah));
            relay.setStudents(List.of(tessa, julian));
            capstoneSection.setTeams(List.of(aurora, relay));
            capstoneSection.setStudents(List.of(sara, noah, tessa, julian));

            sectionRepository.saveAll(List.of(uxSection, capstoneSection));

            activityEntryRepository.saveAll(List.of(
                    activity(uxSection, anika, LocalDate.of(2026, 3, 16), "DESIGN", "Interview synthesis",
                            "Synthesized user interview notes into a prioritized insight matrix.", 3, 3, "COMPLETED"),
                    activity(uxSection, anika, LocalDate.of(2026, 3, 16), "DOCUMENTATION", "Sprint retrospective",
                            "Documented wins, blockers, and carry-over items for the next sprint.", 2, 2, "IN REVIEW"),
                    activity(uxSection, drew, LocalDate.of(2026, 3, 16), "RESEARCH", "Prototype review",
                            "Reviewed test findings and captured recommendations for the next prototype iteration.", 2, 2, "COMPLETED"),
                    activity(uxSection, lila, LocalDate.of(2026, 3, 16), "DEVELOPMENT", "Usability testing support",
                            "Prepared prototype flow and observed two moderated usability sessions.", 3, 3, "IN REVIEW"),
                    activity(uxSection, marco, LocalDate.of(2026, 3, 16), "MISCELLANEOUS", "Planning sync",
                            "Coordinated deliverables, owners, and timeline updates with the teaching team.", 2, 2, "COMPLETED"),
                    activity(capstoneSection, sara, LocalDate.of(2026, 3, 16), "DEVELOPMENT", "Release prep",
                            "Stabilized deployment scripts and validated staging smoke tests.", 4, 4, "COMPLETED"),
                    activity(capstoneSection, noah, LocalDate.of(2026, 3, 16), "DOCUMENTATION", "Runbook update",
                            "Updated the incident runbook with screenshots and rollback notes.", 2, 2, "COMPLETED"),
                    activity(capstoneSection, tessa, LocalDate.of(2026, 3, 16), "RESEARCH", "Feedback review",
                            "Compiled stakeholder feedback and proposed backlog adjustments.", 2, 1, "IN REVIEW"),
                    activity(capstoneSection, julian, LocalDate.of(2026, 3, 16), "DESIGN", "Board cleanup",
                            "Cleaned sprint board labels and aligned task owners for milestone week.", 1, 1, "COMPLETED")
            ));

            peerEvaluationRepository.saveAll(List.of(
                    evaluation(uxSection, LocalDate.of(2026, 3, 23), anika, drew,
                            "Consistently pushed decisions forward and kept the team unblocked.",
                            List.of(score(sprintRubric.getCriteria().get(0), 5), score(sprintRubric.getCriteria().get(1), 5),
                                    score(sprintRubric.getCriteria().get(2), 4), score(sprintRubric.getCriteria().get(3), 5))),
                    evaluation(uxSection, LocalDate.of(2026, 3, 23), lila, drew,
                            "Great communicator and very dependable on shared artifacts.",
                            List.of(score(sprintRubric.getCriteria().get(0), 4), score(sprintRubric.getCriteria().get(1), 5),
                                    score(sprintRubric.getCriteria().get(2), 4), score(sprintRubric.getCriteria().get(3), 5))),
                    evaluation(uxSection, LocalDate.of(2026, 3, 30), anika, drew,
                            "Handled feedback well and delivered polished updates.",
                            List.of(score(sprintRubric.getCriteria().get(0), 5), score(sprintRubric.getCriteria().get(1), 4),
                                    score(sprintRubric.getCriteria().get(2), 5), score(sprintRubric.getCriteria().get(3), 5))),
                    evaluation(capstoneSection, LocalDate.of(2026, 3, 23), sara, noah,
                            "Reliable release partner who kept the team moving.",
                            List.of(score(capstoneRubric.getCriteria().get(0), 9), score(capstoneRubric.getCriteria().get(1), 10),
                                    score(capstoneRubric.getCriteria().get(2), 9))),
                    evaluation(capstoneSection, LocalDate.of(2026, 3, 23), julian, noah,
                            "Strong follow-through and fast response to bugs.",
                            List.of(score(capstoneRubric.getCriteria().get(0), 8), score(capstoneRubric.getCriteria().get(1), 9),
                                    score(capstoneRubric.getCriteria().get(2), 9)))
            ));
        };
    }

    private Rubric createRubric(String name, String description, List<Criterion> criteria) {
        Rubric rubric = new Rubric();
        rubric.setName(name);
        rubric.setDescription(description);

        for (Criterion criterion : criteria) {
            rubric.addCriterion(criterion);
        }

        return rubric;
    }

    private Criterion criterion(String name, String description, int maxScore) {
        Criterion criterion = new Criterion();
        criterion.setName(name);
        criterion.setDescription(description);
        criterion.setMaxScore(maxScore);
        return criterion;
    }

    private Team team(String name, String description, String website, Section section) {
        Team team = new Team();
        team.setName(name);
        team.setDescription(description);
        team.setWebsite(website);
        team.setSection(section);
        return team;
    }

    private Student student(String firstName, String lastName, String email, Section section, Team team) {
        Student student = new Student();
        student.setFirstName(firstName);
        student.setLastName(lastName);
        student.setEmail(email);
        student.setPassword("student123");
        student.setSection(section);
        student.setTeam(team);
        return student;
    }

    private Instructor instructor(String firstName, String lastName, String email) {
        Instructor instructor = new Instructor();
        instructor.setFirstName(firstName);
        instructor.setLastName(lastName);
        instructor.setEmail(email);
        instructor.setPassword("instructor123");
        instructor.setActive(true);
        return instructor;
    }

    private ActivityEntry activity(
            Section section,
            Student student,
            LocalDate weekStart,
            String category,
            String activity,
            String description,
            int plannedHours,
            int actualHours,
            String status
    ) {
        ActivityEntry entry = new ActivityEntry();
        entry.setSection(section);
        entry.setStudent(student);
        entry.setWeekStart(weekStart);
        entry.setCategory(category);
        entry.setActivity(activity);
        entry.setDescription(description);
        entry.setPlannedHours(plannedHours);
        entry.setActualHours(actualHours);
        entry.setStatus(status);
        return entry;
    }

    private PeerEvaluation evaluation(
            Section section,
            LocalDate weekStart,
            Student reviewer,
            Student reviewee,
            String comment,
            List<PeerEvaluationScore> scores
    ) {
        PeerEvaluation evaluation = new PeerEvaluation();
        evaluation.setSection(section);
        evaluation.setWeekStart(weekStart);
        evaluation.setReviewer(reviewer);
        evaluation.setReviewee(reviewee);
        evaluation.setPublicComment(comment);
        for (PeerEvaluationScore score : scores) {
            evaluation.addScore(score);
        }
        return evaluation;
    }

    private PeerEvaluationScore score(Criterion criterion, int value) {
        PeerEvaluationScore score = new PeerEvaluationScore();
        score.setCriterion(criterion);
        score.setScore(value);
        return score;
    }
}
