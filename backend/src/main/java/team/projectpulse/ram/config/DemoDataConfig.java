package team.projectpulse.ram.config;

import java.time.LocalDate;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import team.projectpulse.ram.model.Criterion;
import team.projectpulse.ram.model.Rubric;
import team.projectpulse.ram.model.Section;
import team.projectpulse.ram.model.Student;
import team.projectpulse.ram.model.Team;
import team.projectpulse.ram.repository.RubricRepository;
import team.projectpulse.ram.repository.SectionRepository;

@Configuration
public class DemoDataConfig {

    @Bean
    CommandLineRunner seedDemoData(SectionRepository sectionRepository, RubricRepository rubricRepository) {
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
            uxSection.setInstructors(List.of("Dr. Maya Chen", "Prof. Jonah Ellis"));
            uxSection.setRubric(sprintRubric);

            Team northStar = team("North Star", uxSection);
            Team signalLab = team("Signal Lab", uxSection);

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
            capstoneSection.setInstructors(List.of("Prof. Elena Brooks"));
            capstoneSection.setRubric(capstoneRubric);

            Team aurora = team("Aurora", capstoneSection);
            Team relay = team("Relay", capstoneSection);

            Student sara = student("Sara", "Kim", "sara.kim@example.edu", capstoneSection, aurora);
            Student noah = student("Noah", "Reed", "noah.reed@example.edu", capstoneSection, aurora);
            Student tessa = student("Tessa", "Owens", "tessa.owens@example.edu", capstoneSection, relay);
            Student julian = student("Julian", "Price", "julian.price@example.edu", capstoneSection, relay);

            aurora.setStudents(List.of(sara, noah));
            relay.setStudents(List.of(tessa, julian));
            capstoneSection.setTeams(List.of(aurora, relay));
            capstoneSection.setStudents(List.of(sara, noah, tessa, julian));

            sectionRepository.saveAll(List.of(uxSection, capstoneSection));
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

    private Team team(String name, Section section) {
        Team team = new Team();
        team.setName(name);
        team.setSection(section);
        return team;
    }

    private Student student(String firstName, String lastName, String email, Section section, Team team) {
        Student student = new Student();
        student.setFirstName(firstName);
        student.setLastName(lastName);
        student.setEmail(email);
        student.setSection(section);
        student.setTeam(team);
        return student;
    }
}
