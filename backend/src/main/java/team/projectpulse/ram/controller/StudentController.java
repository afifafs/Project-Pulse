package team.projectpulse.ram.controller;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team.projectpulse.ram.dto.InviteStudentsRequest;
import team.projectpulse.ram.dto.InviteStudentsResponse;
import team.projectpulse.ram.dto.ProfileResponse;
import team.projectpulse.ram.dto.ProfileUpdateRequest;
import team.projectpulse.ram.dto.ResetPasswordRequest;
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
        int invitationsSent = studentService.inviteStudents(request.getEmails());
        return ResponseEntity.ok(new InviteStudentsResponse(invitationsSent));
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
}
