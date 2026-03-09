package edu.uc.campusevent.notification;

import edu.uc.campusevent.domain.event.Event;
import edu.uc.campusevent.domain.notification.Notification;
import edu.uc.campusevent.domain.notification.NotificationRepository;
import edu.uc.campusevent.domain.notification.NotificationService;
import edu.uc.campusevent.domain.rsvp.RsvpStatus;
import edu.uc.campusevent.domain.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock NotificationRepository notificationRepository;
    @InjectMocks NotificationService notificationService;

    @Test
    void sendRsvpConfirmation_attending_savesNotification() {
        User user = User.builder().email("t@uc.edu").build();
        Event event = Event.builder().title("E").build();
        notificationService.sendRsvpConfirmation(user, event, RsvpStatus.ATTENDING);
        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    void sendRsvpConfirmation_waitlist_savesNotification() {
        User user = User.builder().email("t@uc.edu").build();
        Event event = Event.builder().title("E").build();
        notificationService.sendRsvpConfirmation(user, event, RsvpStatus.WAITLIST);
        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    void sendWaitlistPromotion_savesNotification() {
        User user = User.builder().email("t@uc.edu").build();
        Event event = Event.builder().title("E").build();
        notificationService.sendWaitlistPromotion(user, event);
        verify(notificationRepository).save(any(Notification.class));
    }
}
