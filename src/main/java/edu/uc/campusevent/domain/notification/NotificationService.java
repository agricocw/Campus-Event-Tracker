package edu.uc.campusevent.domain.notification;

import edu.uc.campusevent.domain.event.Event;
import edu.uc.campusevent.domain.rsvp.RsvpStatus;
import edu.uc.campusevent.domain.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public void sendRsvpConfirmation(User user, Event event, RsvpStatus status) {
        String message = status == RsvpStatus.ATTENDING
                ? String.format("You're confirmed for '%s'!", event.getTitle())
                : String.format("You're on the waitlist for '%s'.", event.getTitle());

        Notification notification = Notification.builder()
                .user(user)
                .event(event)
                .type("RSVP_CONFIRMATION")
                .message(message)
                .build();

        notificationRepository.save(notification);
        log.info("RSVP notification sent to {} for event {}", user.getEmail(), event.getTitle());
    }

    public void sendWaitlistPromotion(User user, Event event) {
        Notification notification = Notification.builder()
                .user(user)
                .event(event)
                .type("WAITLIST_PROMOTION")
                .message(
                        String.format("Good news! A spot opened up — you're now confirmed for '%s'!", event.getTitle()))
                .build();

        notificationRepository.save(notification);
        log.info("Waitlist promotion notification sent to {} for event {}", user.getEmail(), event.getTitle());
    }
}
