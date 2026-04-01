package edu.uc.campusevent.shared.dto;

import edu.uc.campusevent.domain.event.Event;
import edu.uc.campusevent.domain.event.EventStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record EventSummaryResponse(
        UUID id,
        String title,
        String category,
        String location,
        LocalDateTime startTime,
        LocalDateTime endTime,
        EventStatus status,
        List<String> tags,
        long attendeeCount) {

    public static EventSummaryResponse from(Event event) {
        return new EventSummaryResponse(
                event.getId(),
                event.getTitle(),
                event.getCategory(),
                event.getLocation(),
                event.getStartTime(),
                event.getEndTime(),
                event.getStatus(),
                event.getTags(),
                event.getAttendeeCount());
    }
}
