package team.projectpulse.ram.controller;

import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import team.projectpulse.ram.dto.InviteStudentsRequest;
import team.projectpulse.ram.dto.InviteStudentsResponse;
import team.projectpulse.ram.dto.StudentAccountRequest;
import team.projectpulse.ram.dto.StudentAccountResponse;
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

    @GetMapping("/accounts")
    public ResponseEntity<List<StudentAccountResponse>> getStudentAccounts() {
        return ResponseEntity.ok(studentService.getStudentAccounts());
    }

    @GetMapping("/accounts/{id}")
    public ResponseEntity<StudentAccountResponse> getStudentAccount(@PathVariable Long id) {
        return ResponseEntity.ok(studentService.getStudentAccount(id));
    }

    @PostMapping("/accounts/setup")
    public ResponseEntity<StudentAccountResponse> setupStudentAccount(@RequestBody StudentAccountRequest request) {
        StudentAccountResponse response = studentService.setupStudentAccount(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.getId())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/accounts/{id}")
    public ResponseEntity<StudentAccountResponse> updateStudentAccount(
            @PathVariable Long id,
            @RequestBody StudentAccountRequest request
    ) {
        return ResponseEntity.ok(studentService.updateStudentAccount(id, request));
    }
}
