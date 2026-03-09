package edu.uc.campusevent.domain.rsvp;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RsvpRepository extends JpaRepository<Rsvp, UUID> {

    Optional<Rsvp> findByUserIdAndEventId(UUID userId, UUID eventId);

    List<Rsvp> findByUserIdOrderByCreatedAtDesc(UUID userId);

    long countByEventIdAndStatus(UUID eventId, RsvpStatus status);

    // First person on the waitlist (earliest createdAt)
    Optional<Rsvp> findFirstByEventIdAndStatusOrderByCreatedAtAsc(
            UUID eventId, RsvpStatus status);
}
