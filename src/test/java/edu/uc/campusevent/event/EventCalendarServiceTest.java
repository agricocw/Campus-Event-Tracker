package edu.uc.campusevent.event;

import edu.uc.campusevent.domain.event.Event;
import edu.uc.campusevent.domain.event.EventCalendarService;
import edu.uc.campusevent.domain.event.EventStatus;
import edu.uc.campusevent.domain.user.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EventCalendarServiceTest {

    private final EventCalendarService service = new EventCalendarService();

    @Test
    void generateEventIcs_givenEvent_whenCalled_thenProducesVCalendarContent() {
        UUID eventId = UUID.randomUUID();
        Event event = Event.builder()
                .id(eventId)
                .title("Spring Expo")
                .description("Talks and demos")
                .category("Academic")
                .location("Engineering Hall")
                .startTime(LocalDateTime.of(2026, 4, 20, 10, 0))
                .endTime(LocalDateTime.of(2026, 4, 20, 12, 0))
                .updatedAt(LocalDateTime.of(2026, 4, 10, 9, 0))
                .status(EventStatus.PUBLISHED)
                .organizer(User.builder().id(UUID.randomUUID()).firstName("Test").lastName("Organizer").build())
                .build();

        String ics = service.generateEventIcs(event);

        assertThat(ics).contains("BEGIN:VCALENDAR");
        assertThat(ics).contains("BEGIN:VEVENT");
        assertThat(ics).contains("SUMMARY:Spring Expo");
        assertThat(ics).contains("LOCATION:Engineering Hall");
        assertThat(ics).contains("END:VCALENDAR");
    }

    @Test
    void calendarFilename_givenEventId_whenCalled_thenReturnsAttachmentName() {
        UUID id = UUID.randomUUID();
        assertThat(service.calendarFilename(id)).isEqualTo("event-" + id + ".ics");
    }
}
