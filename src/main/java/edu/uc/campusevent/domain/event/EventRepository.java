package edu.uc.campusevent.domain.event;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface EventRepository extends JpaRepository<Event, UUID> {

        // All published events, paginated (used by event feed)
        Page<Event> findByStatusOrderByStartTimeAsc(EventStatus status, Pageable pageable);

        // Filter by category + status (#7)
        Page<Event> findByCategoryAndStatus(String category, EventStatus status, Pageable pageable);

        // Organizer's own events (#8) — paginated and non-paginated
        Page<Event> findByOrganizerIdOrderByStartTimeDesc(UUID organizerId, Pageable pageable);

        List<Event> findByOrganizerId(UUID organizerId);

        // Events starting within a time window — used by reminder scheduler
        @Query("SELECT e FROM Event e WHERE e.status = 'PUBLISHED' " +
                        "AND e.startTime BETWEEN :from AND :to")
        List<Event> findPublishedBetween(
                        @Param("from") LocalDateTime from,
                        @Param("to") LocalDateTime to);

        // Full-text title search (LIKE — use pg_trgm index in prod)
        @Query("SELECT e FROM Event e WHERE e.status = 'PUBLISHED' " +
                        "AND LOWER(e.title) LIKE LOWER(CONCAT('%', :q, '%'))")
        Page<Event> searchByTitle(@Param("q") String query, Pageable pageable);
}
