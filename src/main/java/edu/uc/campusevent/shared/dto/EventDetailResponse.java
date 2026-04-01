package edu.uc.campusevent.shared.dto;

import edu.uc.campusevent.domain.event.Event;
import edu.uc.campusevent.domain.event.EventStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record EventDetailResponse(
        UUID id,
        String title,
        String description,
        String category,
        String location,
        LocalDateTime startTime,
        LocalDateTime endTime,
        Integer capacity,
        EventStatus status,
        List<String> tags,
        long attendeeCount,
        UUID organizerId,
        String organizerName) {

    public static EventDetailResponse from(Event event) {
        return new EventDetailResponse(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getCategory(),
                event.getLocation(),
                event.getStartTime(),
                event.getEndTime(),
                event.getCapacity(),
                event.getStatus(),
                event.getTags(),
                event.getAttendeeCount(),
                event.getOrganizer().getId(),
                event.getOrganizer().getFirstName() + " " + event.getOrganizer().getLastName());
    }
}
