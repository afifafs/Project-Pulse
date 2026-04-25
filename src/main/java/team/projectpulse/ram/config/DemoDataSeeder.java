package team.projectpulse.ram.config;

import java.time.LocalDate;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import team.projectpulse.ram.model.Criterion;
import team.projectpulse.ram.model.Instructor;
import team.projectpulse.ram.model.Rubric;
import team.projectpulse.ram.model.Section;
import team.projectpulse.ram.model.SectionWeek;
import team.projectpulse.ram.model.Student;
import team.projectpulse.ram.model.Team;
import team.projectpulse.ram.repository.InstructorRepository;
import team.projectpulse.ram.repository.RubricRepository;
import team.projectpulse.ram.repository.SectionRepository;
import team.projectpulse.ram.repository.StudentRepository;
import team.projectpulse.ram.repository.TeamRepository;

@Configuration
public class DemoDataSeeder {

    @Bean
    CommandLineRunner seedDemoData(
            RubricRepository rubricRepository,
            SectionRepository sectionRepository,
            TeamRepository teamRepository,
            StudentRepository studentRepository,
            InstructorRepository instructorRepository
    ) {
        return args -> {
            if (!sectionRepository.findAll().isEmpty()) {
                return;
            }

            Rubric rubric = new Rubric();
            rubric.setName("Senior Design Peer Evaluation Rubric");
            rubric.setDescription("Shared rubric for team contribution and communication.");

            Criterion contribution = new Criterion();
            contribution.setName("Contribution");
            contribution.setDescription("Completes assigned work and contributes regularly.");
            contribution.setMaxScore(10);
            rubric.addCriterion(contribution);

            Criterion communication = new Criterion();
            communication.setName("Communication");
            communication.setDescription("Communicates clearly with teammates and instructors.");
            communication.setMaxScore(10);
            rubric.addCriterion(communication);

            Criterion reliability = new Criterion();
            reliability.setName("Reliability");
            reliability.setDescription("Shows up prepared and meets commitments.");
            reliability.setMaxScore(10);
            rubric.addCriterion(reliability);

            Rubric savedRubric = rubricRepository.save(rubric);

            Section section = new Section();
            section.setName("Senior Design Team Alpha");
            section.setCourseCode("CS-490");
            section.setStartDate(LocalDate.now().minusWeeks(6));
            section.setEndDate(LocalDate.now().plusWeeks(8));
            section.setInstructors(List.of("Professor Harper"));
            section.setRubric(savedRubric);
            section.getWeeks().add(new SectionWeek(1, LocalDate.now().minusWeeks(5), LocalDate.now().minusWeeks(5).plusDays(6), true, section));
            section.getWeeks().add(new SectionWeek(2, LocalDate.now().minusWeeks(4), LocalDate.now().minusWeeks(4).plusDays(6), true, section));
            section.getWeeks().add(new SectionWeek(3, LocalDate.now().minusWeeks(3), LocalDate.now().minusWeeks(3).plusDays(6), true, section));
            section.getWeeks().add(new SectionWeek(4, LocalDate.now().minusWeeks(2), LocalDate.now().minusWeeks(2).plusDays(6), true, section));
            section.getWeeks().add(new SectionWeek(5, LocalDate.now().minusWeeks(1), LocalDate.now().minusWeeks(1).plusDays(6), true, section));
            Section savedSection = sectionRepository.save(section);

            Team team = new Team();
            team.setName("Pulse Builders");
            team.setDescription("Capstone team focused on the peer evaluation platform.");
            team.setWebsite("https://example.com/pulse-builders");
            team.setSection(savedSection);
            Team savedTeam = teamRepository.save(team);

            Student ava = buildStudent("Ava", "Nguyen", "ava.nguyen@example.com", "ava.nguyen", savedSection, savedTeam, true);
            Student marco = buildStudent("Marco", "Ruiz", "marco.ruiz@example.com", "marco.ruiz", savedSection, savedTeam, true);
            Student lena = buildStudent("Lena", "Patel", "lena.patel@example.com", "lena.patel", savedSection, savedTeam, true);
            Student unassigned = buildStudent("Noah", "Brooks", "noah.brooks@example.com", "noah.brooks", savedSection, null, false);
            studentRepository.saveAll(List.of(ava, marco, lena, unassigned));

            Instructor instructor = new Instructor();
            instructor.setFirstName("Professor");
            instructor.setLastName("Harper");
            instructor.setEmail("prof.harper@example.com");
            instructor.setUsername("prof.harper");
            instructor.setPassword("password123");
            instructor.setActive(Boolean.FALSE);
            instructor.setSection(savedSection);
            instructorRepository.save(instructor);
        };
    }

    private Student buildStudent(
            String firstName,
            String lastName,
            String email,
            String username,
            Section section,
            Team team,
            boolean active
    ) {
        Student student = new Student();
        student.setFirstName(firstName);
        student.setLastName(lastName);
        student.setEmail(email);
        student.setUsername(username);
        student.setPassword("password123");
        student.setSection(section);
        student.setTeam(team);
        student.setActive(active);
        return student;
    }
}
