package team.projectpulse.ram.controller;

import java.util.List;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import team.projectpulse.ram.dto.ActiveWeeksRequest;
import team.projectpulse.ram.dto.SectionDetailResponse;
import team.projectpulse.ram.dto.SectionRequest;
import team.projectpulse.ram.dto.SectionResponse;
import team.projectpulse.ram.service.SectionService;

@RestController
@RequestMapping("/sections")
public class SectionController {

    private final SectionService sectionService;

    public SectionController(SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @GetMapping
    public ResponseEntity<List<SectionResponse>> findSections(@RequestParam(required = false) String name) {
        List<SectionResponse> sections = sectionService.findSections(name).stream()
                .map(SectionResponse::fromEntity)
                .toList();

        return ResponseEntity.ok(sections);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SectionDetailResponse> getSectionDetails(@PathVariable Long id) {
        return ResponseEntity.ok(sectionService.getSectionDetails(id));
    }

    @PostMapping
    public ResponseEntity<SectionDetailResponse> createSection(@RequestBody SectionRequest request) {
        SectionDetailResponse created = sectionService.createSection(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SectionDetailResponse> updateSection(@PathVariable Long id, @RequestBody SectionRequest request) {
        return ResponseEntity.ok(sectionService.updateSection(id, request));
    }

    @PutMapping("/{id}/active-weeks")
    public ResponseEntity<SectionDetailResponse> updateActiveWeeks(
            @PathVariable Long id,
            @RequestBody ActiveWeeksRequest request
    ) {
        return ResponseEntity.ok(sectionService.updateActiveWeeks(id, request));
    }
}
