package team.projectpulse.ram.config;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import team.projectpulse.ram.service.SectionService;

@Configuration
public class SectionWeekBootstrapConfig {

    private final SectionService sectionService;

    public SectionWeekBootstrapConfig(SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void synchronizeSectionWeeks() {
        sectionService.synchronizeWeeksForAllSections();
    }
}
