package team.projectpulse.ram.controller;

import java.net.URI;
import org.springframework.http.ResponseEntity;
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
