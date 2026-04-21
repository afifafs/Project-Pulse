package team.projectpulse.ram.controller;

import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
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

    @PostMapping
    public ResponseEntity<SectionDetailResponse> createSection(@RequestBody SectionRequest request) {
        SectionDetailResponse createdSection = sectionService.createSection(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdSection.getId())
                .toUri();

        return ResponseEntity.created(location).body(createdSection);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SectionDetailResponse> getSectionDetails(@PathVariable Long id) {
        return ResponseEntity.ok(sectionService.getSectionDetails(id));
    }
}
