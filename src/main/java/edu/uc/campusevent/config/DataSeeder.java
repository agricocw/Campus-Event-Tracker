package edu.uc.campusevent.config;

import edu.uc.campusevent.domain.event.Event;
import edu.uc.campusevent.domain.event.EventRepository;
import edu.uc.campusevent.domain.event.EventStatus;
import edu.uc.campusevent.domain.user.User;
import edu.uc.campusevent.domain.user.UserRepository;
import edu.uc.campusevent.domain.user.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Seeds the H2 dev database with sample users and events.
 * Only runs when the "dev" profile is active.
 */
@Component
@Profile("dev")
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepo;
    private final EventRepository eventRepo;
    private final PasswordEncoder encoder;

    @Override
    public void run(String... args) {
        if (userRepo.count() > 0) {
            log.info("Database already seeded — skipping.");
            return;
        }

        log.info("🌱 Seeding dev database...");

        // ── Users ─────────────────────────────────────────────
        User student = userRepo.save(User.builder()
                .email("student@uc.edu")
                .passwordHash(encoder.encode("password123"))
                .firstName("Alex")
                .lastName("Student")
                .role(UserRole.STUDENT)
                .enabled(true)
                .build());

        User organizer = userRepo.save(User.builder()
                .email("organizer@uc.edu")
                .passwordHash(encoder.encode("password123"))
                .firstName("Jordan")
                .lastName("Organizer")
                .role(UserRole.ORGANIZER)
                .enabled(true)
                .build());

        User admin = userRepo.save(User.builder()
                .email("admin@uc.edu")
                .passwordHash(encoder.encode("password123"))
                .firstName("Taylor")
                .lastName("Admin")
                .role(UserRole.ADMIN)
                .enabled(true)
                .build());

        // ── Events ────────────────────────────────────────────
        LocalDateTime now = LocalDateTime.now();

        eventRepo.save(Event.builder()
                .title("Spring Boot Workshop")
                .description("Learn the fundamentals of Spring Boot 3.x with hands-on exercises. "
                        + "We'll cover dependency injection, REST APIs, JPA, and security.")
                .category("Workshop")
                .location("Rhodes Hall 800")
                .startTime(now.plusDays(3).withHour(14).withMinute(0))
                .endTime(now.plusDays(3).withHour(17).withMinute(0))
                .capacity(30)
                .organizer(organizer)
                .status(EventStatus.PUBLISHED)
                .tags(List.of("java", "spring", "coding"))
                .build());

        eventRepo.save(Event.builder()
                .title("Campus Career Fair 2026")
                .description("Connect with top employers in tech, finance, and engineering. "
                        + "Bring your resume and dress professionally!")
                .category("Career")
                .location("Tangeman University Center, Great Hall")
                .startTime(now.plusDays(7).withHour(10).withMinute(0))
                .endTime(now.plusDays(7).withHour(15).withMinute(0))
                .capacity(500)
                .organizer(organizer)
                .status(EventStatus.PUBLISHED)
                .tags(List.of("career", "networking", "jobs"))
                .build());

        eventRepo.save(Event.builder()
                .title("Bearcat Basketball Watch Party")
                .description("Cheer on the Bearcats at our big-screen watch party! "
                        + "Free snacks and drinks provided.")
                .category("Sports")
                .location("Fifth Third Arena, Student Lounge")
                .startTime(now.plusDays(5).withHour(19).withMinute(0))
                .endTime(now.plusDays(5).withHour(22).withMinute(0))
                .capacity(200)
                .organizer(organizer)
                .status(EventStatus.PUBLISHED)
                .tags(List.of("sports", "basketball", "free food"))
                .build());

        eventRepo.save(Event.builder()
                .title("Open Mic Night")
                .description("Show off your talent — singing, poetry, comedy, anything goes! "
                        + "Sign up at the door.")
                .category("Arts")
                .location("MainStreet Cafe")
                .startTime(now.plusDays(10).withHour(20).withMinute(0))
                .endTime(now.plusDays(10).withHour(23).withMinute(0))
                .organizer(organizer)
                .status(EventStatus.PUBLISHED)
                .tags(List.of("music", "poetry", "open mic"))
                .build()); // no capacity = unlimited

        eventRepo.save(Event.builder()
                .title("Study Group: Data Structures")
                .description("Weekly study group for IT3045. "
                        + "We'll work through practice problems together.")
                .category("Academic")
                .location("Langsam Library, Room 308")
                .startTime(now.plusDays(2).withHour(16).withMinute(0))
                .endTime(now.plusDays(2).withHour(18).withMinute(0))
                .capacity(15)
                .organizer(organizer)
                .status(EventStatus.PUBLISHED)
                .tags(List.of("study", "academic", "data structures"))
                .build());

        eventRepo.save(Event.builder()
                .title("Unpublished Draft Event")
                .description("This is a draft event that hasn't been published yet.")
                .category("Social")
                .location("TBD")
                .startTime(now.plusDays(14).withHour(18).withMinute(0))
                .endTime(now.plusDays(14).withHour(21).withMinute(0))
                .organizer(organizer)
                .status(EventStatus.DRAFT)
                .build());

        log.info("✅ Seeded 3 users and 6 events.");
        log.info("   🔑 Login credentials: student@uc.edu / password123");
        log.info("   🔑 Login credentials: organizer@uc.edu / password123");
        log.info("   🔑 Login credentials: admin@uc.edu / password123");
    }
}
