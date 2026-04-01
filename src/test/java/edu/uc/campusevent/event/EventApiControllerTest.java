package edu.uc.campusevent.event;

import edu.uc.campusevent.domain.event.Event;
import edu.uc.campusevent.domain.event.EventApiController;
import edu.uc.campusevent.domain.event.EventCalendarService;
import edu.uc.campusevent.domain.event.EventService;
import edu.uc.campusevent.domain.event.EventStatus;
import edu.uc.campusevent.domain.user.User;
import edu.uc.campusevent.shared.dto.EventDetailResponse;
import edu.uc.campusevent.shared.dto.EventSummaryResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventApiControllerTest {

    @Mock
    EventService eventService;
    @Mock
    EventCalendarService calendarService;

    private EventApiController controller;
    private Event event;

    @BeforeEach
    void setUp() {
        controller = new EventApiController(eventService, calendarService);
        event = Event.builder()
                .id(UUID.randomUUID())
                .title("Career Fair")
                .description("Meet employers")
                .category("Career")
                .location("Student Center")
                .startTime(LocalDateTime.now().plusDays(2))
                .endTime(LocalDateTime.now().plusDays(2).plusHours(2))
                .status(EventStatus.PUBLISHED)
                .organizer(User.builder().id(UUID.randomUUID()).firstName("Rudi").lastName("Vogel").build())
                .build();
    }

    @Test
    void listEvents_givenSearchQuery_whenRequested_thenReturnsMappedPage() {
        when(eventService.search(eq("career"), any())).thenReturn(new PageImpl<>(List.of(event)));
        Page<EventSummaryResponse> response = controller.listEvents(0, 10, null, "career", null);
        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getContent().getFirst().title()).isEqualTo("Career Fair");
    }

    @Test
    void getEvent_givenEventId_whenRequested_thenReturnsDetailDto() {
        when(eventService.findById(event.getId())).thenReturn(event);
        EventDetailResponse response = controller.getEvent(event.getId());
        assertThat(response.id()).isEqualTo(event.getId());
        assertThat(response.organizerName()).isEqualTo("Rudi Vogel");
    }

    @Test
    void downloadCalendar_givenEventId_whenRequested_thenReturnsIcsAttachment() {
        when(eventService.findById(event.getId())).thenReturn(event);
        when(calendarService.generateEventIcs(event)).thenReturn("BEGIN:VCALENDAR\r\nEND:VCALENDAR\r\n");
        when(calendarService.calendarFilename(event.getId())).thenReturn("event-test.ics");

        ResponseEntity<byte[]> response = controller.downloadCalendar(event.getId());

        assertThat(response.getHeaders().getContentType().toString()).startsWith("text/calendar");
        assertThat(response.getHeaders().getFirst("Content-Disposition")).contains("event-test.ics");
        assertThat(new String(response.getBody())).contains("BEGIN:VCALENDAR");
        verify(calendarService).generateEventIcs(event);
    }
}
