package edu.uc.campusevent.event;

import edu.uc.campusevent.domain.event.*;
import edu.uc.campusevent.domain.notification.NotificationService;
import edu.uc.campusevent.domain.rsvp.RsvpRepository;
import edu.uc.campusevent.domain.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    EventRepository eventRepo;
    @Mock
    RsvpRepository rsvpRepo;
    @Mock
    NotificationService notifService;
    @InjectMocks
    EventService eventService;

    @Test
    void publishEvent_asOwner_changesStatusToPublished() {
        User organizer = User.builder().id(UUID.randomUUID()).build();
        Event draft = Event.builder()
                .id(UUID.randomUUID())
                .organizer(organizer)
                .status(EventStatus.DRAFT).build();

        when(eventRepo.findById(draft.getId())).thenReturn(Optional.of(draft));
        when(eventRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Event result = eventService.publishEvent(draft.getId(), organizer);

        assertThat(result.getStatus()).isEqualTo(EventStatus.PUBLISHED);
    }

    @Test
    void publishEvent_asNonOwner_throwsAccessDenied() {
        User owner = User.builder().id(UUID.randomUUID()).build();
        User intruder = User.builder().id(UUID.randomUUID()).build();
        Event event = Event.builder()
                .id(UUID.randomUUID())
                .organizer(owner)
                .status(EventStatus.DRAFT).build();

        when(eventRepo.findById(event.getId())).thenReturn(Optional.of(event));

        assertThatThrownBy(() -> eventService.publishEvent(event.getId(), intruder))
                .isInstanceOf(AccessDeniedException.class);
    }
}
