package edu.uc.campusevent.rsvp;

import edu.uc.campusevent.domain.event.Event;
import edu.uc.campusevent.domain.event.EventRepository;
import edu.uc.campusevent.domain.event.EventStatus;
import edu.uc.campusevent.domain.notification.NotificationService;
import edu.uc.campusevent.domain.rsvp.*;
import edu.uc.campusevent.domain.user.User;
import edu.uc.campusevent.shared.exception.DuplicateRsvpException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RsvpServiceTest {

    @Mock RsvpRepository rsvpRepo;
    @Mock EventRepository eventRepo;
    @Mock NotificationService notifService;
    @InjectMocks RsvpService rsvpService;

    @Test
    void rsvpToEvent_success_createsAttendingRsvp() {
        UUID eventId = UUID.randomUUID();
        User user = User.builder().id(UUID.randomUUID()).email("test@uc.edu").build();
        Event event = Event.builder()
                .id(eventId).status(EventStatus.PUBLISHED).capacity(100).build();
        when(eventRepo.findById(eventId)).thenReturn(Optional.of(event));
        when(rsvpRepo.findByUserIdAndEventId(user.getId(), eventId)).thenReturn(Optional.empty());
        when(rsvpRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        Rsvp result = rsvpService.rsvpToEvent(eventId, user);
        assertThat(result.getStatus()).isEqualTo(RsvpStatus.ATTENDING);
        verify(notifService).sendRsvpConfirmation(user, event, RsvpStatus.ATTENDING);
    }

    @Test
    void rsvpToEvent_fullEvent_createsWaitlistRsvp() {
        UUID eventId = UUID.randomUUID();
        User user = User.builder().id(UUID.randomUUID()).build();
        Rsvp existing = Rsvp.builder().status(RsvpStatus.ATTENDING).build();
        Event event = Event.builder()
                .id(eventId).status(EventStatus.PUBLISHED).capacity(1)
                .rsvps(new ArrayList<>(List.of(existing))).build();
        when(eventRepo.findById(eventId)).thenReturn(Optional.of(event));
        when(rsvpRepo.findByUserIdAndEventId(user.getId(), eventId)).thenReturn(Optional.empty());
        when(rsvpRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        Rsvp result = rsvpService.rsvpToEvent(eventId, user);
        assertThat(result.getStatus()).isEqualTo(RsvpStatus.WAITLIST);
    }

    @Test
    void rsvpToEvent_duplicate_throwsDuplicateRsvpException() {
        UUID eventId = UUID.randomUUID();
        User user = User.builder().id(UUID.randomUUID()).build();
        Event event = Event.builder().id(eventId).status(EventStatus.PUBLISHED).build();
        when(eventRepo.findById(eventId)).thenReturn(Optional.of(event));
        when(rsvpRepo.findByUserIdAndEventId(user.getId(), eventId))
                .thenReturn(Optional.of(Rsvp.builder().build()));
        assertThatThrownBy(() -> rsvpService.rsvpToEvent(eventId, user))
                .isInstanceOf(DuplicateRsvpException.class);
    }

    @Test
    void rsvpToEvent_eventNotPublished_throwsIllegalState() {
        UUID eventId = UUID.randomUUID();
        User user = User.builder().id(UUID.randomUUID()).build();
        Event event = Event.builder().id(eventId).status(EventStatus.DRAFT).build();
        when(eventRepo.findById(eventId)).thenReturn(Optional.of(event));
        assertThatThrownBy(() -> rsvpService.rsvpToEvent(eventId, user))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void rsvpToEvent_eventNotFound_throwsEntityNotFound() {
        UUID eventId = UUID.randomUUID();
        when(eventRepo.findById(eventId)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> rsvpService.rsvpToEvent(eventId, User.builder().id(UUID.randomUUID()).build()))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void cancelRsvp_existingRsvp_deletesIt() {
        UUID eventId = UUID.randomUUID();
        User user = User.builder().id(UUID.randomUUID()).build();
        Event event = Event.builder().id(eventId).rsvps(new ArrayList<>()).build();
        Rsvp rsvp = Rsvp.builder().id(UUID.randomUUID()).event(event).build();
        event.getRsvps().add(rsvp);
        when(rsvpRepo.findByUserIdAndEventId(user.getId(), eventId)).thenReturn(Optional.of(rsvp));
        when(rsvpRepo.findFirstByEventIdAndStatusOrderByCreatedAtAsc(eventId, RsvpStatus.WAITLIST))
                .thenReturn(Optional.empty());
        rsvpService.cancelRsvp(eventId, user);
        verify(rsvpRepo).delete(rsvp);
        assertThat(event.getRsvps()).doesNotContain(rsvp);
    }

    @Test
    void cancelRsvp_withWaitlistUser_promotesNext() {
        UUID eventId = UUID.randomUUID();
        User user = User.builder().id(UUID.randomUUID()).build();
        User waitUser = User.builder().id(UUID.randomUUID()).email("w@uc.edu").build();
        Event event = Event.builder().id(eventId).title("E").rsvps(new ArrayList<>()).build();
        Rsvp rsvp = Rsvp.builder().id(UUID.randomUUID()).event(event).build();
        event.getRsvps().add(rsvp);
        Rsvp waitRsvp = Rsvp.builder().id(UUID.randomUUID()).user(waitUser)
                .event(event).status(RsvpStatus.WAITLIST).build();
        when(rsvpRepo.findByUserIdAndEventId(user.getId(), eventId)).thenReturn(Optional.of(rsvp));
        when(rsvpRepo.findFirstByEventIdAndStatusOrderByCreatedAtAsc(eventId, RsvpStatus.WAITLIST))
                .thenReturn(Optional.of(waitRsvp));
        when(rsvpRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        rsvpService.cancelRsvp(eventId, user);
        assertThat(waitRsvp.getStatus()).isEqualTo(RsvpStatus.ATTENDING);
        verify(notifService).sendWaitlistPromotion(waitUser, event);
    }

    @Test
    void cancelRsvp_notFound_throwsEntityNotFound() {
        UUID eventId = UUID.randomUUID();
        User user = User.builder().id(UUID.randomUUID()).build();
        when(rsvpRepo.findByUserIdAndEventId(user.getId(), eventId)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> rsvpService.cancelRsvp(eventId, user))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void getUserRsvps_returnsOrderedList() {
        UUID userId = UUID.randomUUID();
        when(rsvpRepo.findByUserIdOrderByCreatedAtDesc(userId))
                .thenReturn(List.of(Rsvp.builder().build()));
        assertThat(rsvpService.getUserRsvps(userId)).hasSize(1);
    }
}
