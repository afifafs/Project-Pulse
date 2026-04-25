package team.projectpulse.ram.controller;

import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import team.projectpulse.ram.dto.InstructorAccountRequest;
import team.projectpulse.ram.dto.InstructorResponse;
import team.projectpulse.ram.service.InstructorService;

@RestController
@RequestMapping("/instructors")
public class InstructorController {

    private final InstructorService instructorService;

    public InstructorController(InstructorService instructorService) {
        this.instructorService = instructorService;
    }

    @GetMapping
    public ResponseEntity<List<InstructorResponse>> getInstructors() {
        return ResponseEntity.ok(instructorService.getInstructors());
    }

    @PostMapping("/setup")
    public ResponseEntity<InstructorResponse> setupInstructorAccount(@RequestBody InstructorAccountRequest request) {
        InstructorResponse response = instructorService.setupInstructorAccount(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.getId())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @PatchMapping("/{id}/reactivate")
    public ResponseEntity<InstructorResponse> reactivateInstructor(@PathVariable Long id) {
        return ResponseEntity.ok(instructorService.reactivateInstructor(id));
    }
}
