package team.projectpulse.ram.controller;

import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import team.projectpulse.ram.dto.RubricRequest;
import team.projectpulse.ram.model.Rubric;
import team.projectpulse.ram.service.RubricService;

@RestController
@RequestMapping("/rubrics")
public class RubricController {

    private final RubricService rubricService;

    public RubricController(RubricService rubricService) {
        this.rubricService = rubricService;
    }

    @GetMapping
    public ResponseEntity<List<Rubric>> getRubrics() {
        return ResponseEntity.ok(rubricService.getAllRubrics());
    }

    @PostMapping
    public ResponseEntity<Rubric> createRubric(@RequestBody RubricRequest request) {
        Rubric createdRubric = rubricService.createRubric(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdRubric.getId())
                .toUri();

        return ResponseEntity.created(location).body(createdRubric);
    }
}
