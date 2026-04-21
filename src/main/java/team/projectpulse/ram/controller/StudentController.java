package team.projectpulse.ram.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team.projectpulse.ram.dto.InviteStudentsRequest;
import team.projectpulse.ram.dto.InviteStudentsResponse;
import team.projectpulse.ram.service.StudentService;

@RestController
@RequestMapping("/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @PostMapping("/invite")
    public ResponseEntity<InviteStudentsResponse> inviteStudents(@RequestBody InviteStudentsRequest request) {
        return ResponseEntity.ok(studentService.inviteStudents(request));
    }
}
