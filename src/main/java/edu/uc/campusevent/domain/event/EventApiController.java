package edu.uc.campusevent.domain.event;

import edu.uc.campusevent.shared.dto.EventDetailResponse;
import edu.uc.campusevent.shared.dto.EventSummaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventApiController {

    private final EventService eventService;
    private final EventCalendarService calendarService;

    @GetMapping
    public Page<EventSummaryResponse> listEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String tag) {

        size = Math.min(size, 50);
        Pageable pageable = PageRequest.of(page, size, Sort.by("startTime").ascending());
        Page<Event> events;

        if (q != null && !q.isBlank()) {
            events = eventService.search(q, pageable);
        } else if (category != null && !category.isBlank()) {
            events = eventService.getByCategory(category, pageable);
        } else if (tag != null && !tag.isBlank()) {
            events = eventService.getByTag(tag, pageable);
        } else {
            events = eventService.getPublishedEvents(pageable);
        }
        return events.map(EventSummaryResponse::from);
    }

    @GetMapping("/{id}")
    public EventDetailResponse getEvent(@PathVariable UUID id) {
        return EventDetailResponse.from(eventService.findById(id));
    }

    @GetMapping(value = "/{id}/calendar.ics", produces = "text/calendar")
    public ResponseEntity<byte[]> downloadCalendar(@PathVariable UUID id) {
        Event event = eventService.findById(id);
        String ics = calendarService.generateEventIcs(event);
        String filename = calendarService.calendarFilename(id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(new MediaType("text", "calendar", StandardCharsets.UTF_8))
                .body(ics.getBytes(StandardCharsets.UTF_8));
    }
}
