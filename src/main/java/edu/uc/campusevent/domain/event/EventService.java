package edu.uc.campusevent.domain.event;

import edu.uc.campusevent.domain.notification.NotificationService;
import edu.uc.campusevent.domain.rsvp.RsvpRepository;
import edu.uc.campusevent.domain.user.User;
import edu.uc.campusevent.shared.dto.CreateEventForm;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class EventService {

    private final EventRepository eventRepo;
    private final RsvpRepository rsvpRepo;
    private final NotificationService notifService;

    @Cacheable(value = "events", key = "#pageable.pageNumber + '-' + #pageable.pageSize")
    @Transactional(readOnly = true)
    public Page<Event> getPublishedEvents(Pageable pageable) {
        return eventRepo.findByStatusOrderByStartTimeAsc(EventStatus.PUBLISHED, pageable);
    }

    // #7 — Category filtering
    @Transactional(readOnly = true)
    public Page<Event> getByCategory(String category, Pageable pageable) {
        return eventRepo.findByCategoryAndStatus(category, EventStatus.PUBLISHED, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Event> search(String query, Pageable pageable) {
        return eventRepo.searchByTitle(query, pageable);
    }

    @Transactional(readOnly = true)
    public Event findById(UUID id) {
        return findOrThrow(id);
    }

    // Fixed: actually checks the database for existing RSVP
    @Transactional(readOnly = true)
    public boolean hasRsvp(UUID eventId, UUID userId) {
        return rsvpRepo.findByUserIdAndEventId(userId, eventId).isPresent();
    }

    // #8 — Organizer's created events
    @Transactional(readOnly = true)
    public List<Event> getOrganizerEvents(UUID organizerId) {
        return eventRepo.findByOrganizerId(organizerId);
    }

    public Event createEvent(CreateEventForm form, User organizer) {
        List<String> tagList = parseTags(form.getTags());

        Event event = Event.builder()
                .title(form.getTitle())
                .description(form.getDescription())
                .category(form.getCategory())
                .location(form.getLocation())
                .startTime(form.getStartTime())
                .endTime(form.getEndTime())
                .capacity(form.getCapacity())
                .organizer(organizer)
                .status(EventStatus.DRAFT)
                .tags(tagList)
                .build();
        return eventRepo.save(event);
    }

    // #5 — Update event
    @CacheEvict(value = "events", allEntries = true)
    public Event updateEvent(UUID eventId, CreateEventForm form, User requestingUser) {
        Event event = findOrThrow(eventId);
        checkOwner(event, requestingUser);

        event.setTitle(form.getTitle());
        event.setDescription(form.getDescription());
        event.setCategory(form.getCategory());
        event.setLocation(form.getLocation());
        event.setStartTime(form.getStartTime());
        event.setEndTime(form.getEndTime());
        event.setCapacity(form.getCapacity());
        event.setTags(parseTags(form.getTags()));

        return eventRepo.save(event);
    }

    @CacheEvict(value = "events", allEntries = true)
    public Event publishEvent(UUID eventId, User requestingUser) {
        Event event = findOrThrow(eventId);
        checkOwner(event, requestingUser);
        event.setStatus(EventStatus.PUBLISHED);
        return eventRepo.save(event);
    }

    @CacheEvict(value = "events", allEntries = true)
    public void deleteEvent(UUID eventId, User requestingUser) {
        Event event = findOrThrow(eventId);
        checkOwner(event, requestingUser);
        eventRepo.delete(event);
    }

    /** Verifies that requestingUser is the event organizer. */
    public void checkOwner(Event event, User requestingUser) {
        if (!event.getOrganizer().getId().equals(requestingUser.getId())) {
            throw new AccessDeniedException("Not the event owner");
        }
    }

    private Event findOrThrow(UUID id) {
        return eventRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Event not found: " + id));
    }

    private List<String> parseTags(String tags) {
        if (tags == null || tags.isBlank())
            return Collections.emptyList();
        return Arrays.stream(tags.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }
}
