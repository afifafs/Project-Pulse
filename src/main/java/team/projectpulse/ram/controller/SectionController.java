package team.projectpulse.ram.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import team.projectpulse.ram.dto.SectionDetailResponse;
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
}
