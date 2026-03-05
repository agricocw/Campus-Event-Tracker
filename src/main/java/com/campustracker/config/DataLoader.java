package com.campustracker.config;

import com.campustracker.model.Event;
import com.campustracker.model.Role;
import com.campustracker.model.User;
import com.campustracker.repository.EventRepository;
import com.campustracker.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(UserRepository userRepository, EventRepository eventRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // Create sample users
        User student = new User("Alex Student", "student@campus.edu",
                passwordEncoder.encode("password"), Role.STUDENT);
        student = userRepository.save(student);

        User organizer = new User("Campus Activities Board", "organizer@campus.edu",
                passwordEncoder.encode("password"), Role.ORGANIZER);
        organizer = userRepository.save(organizer);

        // Create sample events
        eventRepository.save(new Event(
                "Spring Welcome Mixer",
                "Join us for an evening of networking and fun to kick off the spring semester! Free food and drinks provided.",
                LocalDateTime.now().plusDays(7),
                "Student Union Ballroom",
                organizer
        ));

        eventRepository.save(new Event(
                "Tech Career Fair",
                "Meet recruiters from top tech companies. Bring your resume and dress professionally.",
                LocalDateTime.now().plusDays(14),
                "Engineering Building Atrium",
                organizer
        ));

        eventRepository.save(new Event(
                "Outdoor Movie Night",
                "Watch a classic movie under the stars on the campus quad. Blankets and popcorn provided!",
                LocalDateTime.now().plusDays(21),
                "Campus Quad",
                organizer
        ));

        eventRepository.save(new Event(
                "Hackathon 2026",
                "24-hour coding competition. Build something awesome and win prizes! Teams of up to 4.",
                LocalDateTime.now().plusDays(30),
                "Computer Science Building",
                organizer
        ));

        System.out.println("Sample data loaded successfully!");
        System.out.println("  Student login:   student@campus.edu / password");
        System.out.println("  Organizer login:  organizer@campus.edu / password");
    }
}
