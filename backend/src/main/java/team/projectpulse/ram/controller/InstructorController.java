package team.projectpulse.ram.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import team.projectpulse.ram.dto.AccountSetupRequest;
import team.projectpulse.ram.dto.InstructorResponse;
import team.projectpulse.ram.dto.InviteInstructorsRequest;
import team.projectpulse.ram.dto.InviteInstructorsResponse;
import team.projectpulse.ram.service.InstructorService;

@RestController
@RequestMapping("/instructors")
public class InstructorController {

    private final InstructorService instructorService;

    public InstructorController(InstructorService instructorService) {
        this.instructorService = instructorService;
    }

    @PostMapping("/invite")
    public ResponseEntity<InviteInstructorsResponse> inviteInstructors(@RequestBody InviteInstructorsRequest request) {
        return ResponseEntity.ok(new InviteInstructorsResponse(instructorService.inviteInstructors(request)));
    }

    @GetMapping
    public ResponseEntity<List<InstructorResponse>> findInstructors(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String sectionName
    ) {
        return ResponseEntity.ok(instructorService.findInstructors(name, email, sectionName));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InstructorResponse> getInstructor(@PathVariable Long id) {
        return ResponseEntity.ok(instructorService.getInstructor(id));
    }

    @PostMapping("/{id}/setup-account")
    public ResponseEntity<InstructorResponse> setupInstructorAccount(
            @PathVariable Long id,
            @RequestBody AccountSetupRequest request
    ) {
        return ResponseEntity.ok(instructorService.setupAccount(id, request));
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<InstructorResponse> deactivate(@PathVariable Long id) {
        return ResponseEntity.ok(instructorService.deactivate(id));
    }

    @PutMapping("/{id}/reactivate")
    public ResponseEntity<InstructorResponse> reactivate(@PathVariable Long id) {
        return ResponseEntity.ok(instructorService.reactivate(id));
    }
}
