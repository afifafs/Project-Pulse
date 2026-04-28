package team.projectpulse.ram.controller;

import java.util.Map;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import team.projectpulse.ram.dto.AccountSetupRequest;
import team.projectpulse.ram.dto.InviteStudentsRequest;
import team.projectpulse.ram.dto.InviteStudentsResponse;
import team.projectpulse.ram.dto.ProfileResponse;
import team.projectpulse.ram.dto.ProfileUpdateRequest;
import team.projectpulse.ram.dto.ResetPasswordRequest;
import team.projectpulse.ram.dto.StudentDetailResponse;
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
        int invitationsSent = studentService.inviteStudents(request);
        return ResponseEntity.ok(new InviteStudentsResponse(invitationsSent));
    }

    @GetMapping
    public ResponseEntity<List<StudentDetailResponse>> findStudents(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String sectionName,
            @RequestParam(required = false) String teamName
    ) {
        return ResponseEntity.ok(studentService.findStudents(name, email, sectionName, teamName));
    }

    @GetMapping("/{studentId}")
    public ResponseEntity<StudentDetailResponse> getStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(studentService.getStudentById(studentId));
    }

    @GetMapping("/{studentId}/profile")
    public ResponseEntity<ProfileResponse> getProfile(@PathVariable Long studentId) {
        return ResponseEntity.ok(studentService.getProfile(studentId));
    }

    @PutMapping("/{studentId}/profile")
    public ResponseEntity<ProfileResponse> updateProfile(
            @PathVariable Long studentId,
            @RequestBody ProfileUpdateRequest request
    ) {
        return ResponseEntity.ok(studentService.updateProfile(studentId, request));
    }

    @PostMapping("/{studentId}/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(
            @PathVariable Long studentId,
            @RequestBody ResetPasswordRequest request
    ) {
        studentService.resetPassword(studentId, request);
        return ResponseEntity.ok(Map.of("message", "Password updated."));
    }

    @PostMapping("/{studentId}/setup-account")
    public ResponseEntity<ProfileResponse> setupStudentAccount(
            @PathVariable Long studentId,
            @RequestBody AccountSetupRequest request
    ) {
        return ResponseEntity.ok(studentService.setupStudentAccount(studentId, request));
    }

    @DeleteMapping("/{studentId}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long studentId) {
        studentService.deleteStudent(studentId);
        return ResponseEntity.noContent().build();
    }
}
