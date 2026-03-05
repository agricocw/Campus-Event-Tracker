package com.campustracker.repository;

import com.campustracker.model.Event;
import com.campustracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByDateAfterOrderByDateAsc(LocalDateTime now);
    List<Event> findByOrganizer(User organizer);
}
