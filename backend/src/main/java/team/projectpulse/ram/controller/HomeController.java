package team.projectpulse.ram.controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HomeController {

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> home() {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("application", "Project Pulse backend");
        response.put("status", "running");
        response.put("message", "Use the frontend app or the API endpoints below.");
        response.put("endpoints", List.of(
                "/sections",
                "/sections/1",
                "/rubrics",
                "/students/{id}/profile",
                "/students/{id}/activities",
                "/students/{id}/evaluations"
        ));

        return ResponseEntity.ok(response);
    }
}
