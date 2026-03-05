package com.campustracker.service;

import com.campustracker.model.Event;
import com.campustracker.model.User;
import com.campustracker.repository.EventRepository;
import com.campustracker.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public EventService(EventRepository eventRepository, UserRepository userRepository) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }

    public List<Event> getUpcomingEvents() {
        return eventRepository.findByDateAfterOrderByDateAsc(LocalDateTime.now());
    }

    public Event getEventById(Long id) {
        return eventRepository.findById(id).orElse(null);
    }

    public Event createEvent(Event event) {
        return eventRepository.save(event);
    }

    @Transactional
    public void rsvp(Long eventId, User user) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));
        user.getRsvpedEvents().add(event);
        userRepository.save(user);
    }

    @Transactional
    public void cancelRsvp(Long eventId, User user) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));
        user.getRsvpedEvents().remove(event);
        userRepository.save(user);
    }

    public boolean hasRsvped(Long eventId, User user) {
        return user.getRsvpedEvents().stream()
                .anyMatch(e -> e.getId().equals(eventId));
    }
}
