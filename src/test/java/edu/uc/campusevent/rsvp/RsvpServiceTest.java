package edu.uc.campusevent.rsvp;

import edu.uc.campusevent.domain.event.Event;
import edu.uc.campusevent.domain.event.EventRepository;
import edu.uc.campusevent.domain.event.EventStatus;
import edu.uc.campusevent.domain.notification.NotificationService;
import edu.uc.campusevent.domain.rsvp.*;
import edu.uc.campusevent.domain.user.User;
import edu.uc.campusevent.shared.exception.DuplicateRsvpException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RsvpServiceTest {

    @Mock
    RsvpRepository rsvpRepo;
    @Mock
    EventRepository eventRepo;
    @Mock
    NotificationService notifService;
    @InjectMocks
    RsvpService rsvpService;

    @Test
    void rsvpToEvent_success_createsAttendingRsvp() {
        UUID eventId = UUID.randomUUID();
        User user = User.builder().id(UUID.randomUUID()).email("test@uc.edu").build();
        Event event = Event.builder()
                .id(eventId)
                .status(EventStatus.PUBLISHED)
                .capacity(100)
                .build();

        when(eventRepo.findById(eventId)).thenReturn(Optional.of(event));
        when(rsvpRepo.findByUserIdAndEventId(user.getId(), eventId)).thenReturn(Optional.empty());
        when(rsvpRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Rsvp result = rsvpService.rsvpToEvent(eventId, user);

        assertThat(result.getStatus()).isEqualTo(RsvpStatus.ATTENDING);
        verify(notifService).sendRsvpConfirmation(user, event, RsvpStatus.ATTENDING);
    }

    @Test
    void rsvpToEvent_duplicate_throwsDuplicateRsvpException() {
        UUID eventId = UUID.randomUUID();
        User user = User.builder().id(UUID.randomUUID()).build();
        Event event = Event.builder()
                .id(eventId)
                .status(EventStatus.PUBLISHED)
                .build();
        Rsvp existingRsvp = Rsvp.builder().id(UUID.randomUUID()).build();

        when(eventRepo.findById(eventId)).thenReturn(Optional.of(event));
        when(rsvpRepo.findByUserIdAndEventId(user.getId(), eventId))
                .thenReturn(Optional.of(existingRsvp));

        assertThatThrownBy(() -> rsvpService.rsvpToEvent(eventId, user))
                .isInstanceOf(DuplicateRsvpException.class);
    }

    @Test
    void rsvpToEvent_eventNotPublished_throwsIllegalState() {
        UUID eventId = UUID.randomUUID();
        User user = User.builder().id(UUID.randomUUID()).build();
        Event event = Event.builder()
                .id(eventId)
                .status(EventStatus.DRAFT)
                .build();

        when(eventRepo.findById(eventId)).thenReturn(Optional.of(event));

        assertThatThrownBy(() -> rsvpService.rsvpToEvent(eventId, user))
                .isInstanceOf(IllegalStateException.class);
    }
}
