package edu.uc.campusevent.event;

import edu.uc.campusevent.domain.event.*;
import edu.uc.campusevent.domain.notification.NotificationService;
import edu.uc.campusevent.domain.rsvp.Rsvp;
import edu.uc.campusevent.domain.rsvp.RsvpRepository;
import edu.uc.campusevent.domain.user.User;
import edu.uc.campusevent.shared.dto.CreateEventForm;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock EventRepository eventRepo;
    @Mock RsvpRepository rsvpRepo;
    @Mock NotificationService notifService;
    @InjectMocks EventService eventService;

    @Test
    void publishEvent_asOwner_changesStatusToPublished() {
        User organizer = User.builder().id(UUID.randomUUID()).build();
        Event draft = Event.builder()
                .id(UUID.randomUUID()).organizer(organizer)
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
                .id(UUID.randomUUID()).organizer(owner)
                .status(EventStatus.DRAFT).build();
        when(eventRepo.findById(event.getId())).thenReturn(Optional.of(event));
        assertThatThrownBy(() -> eventService.publishEvent(event.getId(), intruder))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void getPublishedEvents_returnsPagedResult() {
        Page<Event> page = new PageImpl<>(List.of(Event.builder().build()));
        when(eventRepo.findByStatusOrderByStartTimeAsc(eq(EventStatus.PUBLISHED), any()))
                .thenReturn(page);
        Page<Event> result = eventService.getPublishedEvents(PageRequest.of(0, 10));
        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void getByCategory_filtersCorrectly() {
        when(eventRepo.findByCategoryAndStatus(eq("Workshop"), eq(EventStatus.PUBLISHED), any()))
                .thenReturn(Page.empty());
        Page<Event> result = eventService.getByCategory("Workshop", PageRequest.of(0, 10));
        assertThat(result.getContent()).isEmpty();
    }

    @Test
    void search_delegatesToRepo() {
        when(eventRepo.searchByTitle(eq("spring"), any())).thenReturn(Page.empty());
        Page<Event> result = eventService.search("spring", PageRequest.of(0, 10));
        assertThat(result.getContent()).isEmpty();
    }

    @Test
    void findById_existingEvent_returnsEvent() {
        UUID id = UUID.randomUUID();
        Event event = Event.builder().id(id).build();
        when(eventRepo.findById(id)).thenReturn(Optional.of(event));
        assertThat(eventService.findById(id).getId()).isEqualTo(id);
    }

    @Test
    void findById_nonExisting_throwsEntityNotFound() {
        UUID id = UUID.randomUUID();
        when(eventRepo.findById(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> eventService.findById(id))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void hasRsvp_existing_returnsTrue() {
        UUID eventId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        when(rsvpRepo.findByUserIdAndEventId(userId, eventId))
                .thenReturn(Optional.of(Rsvp.builder().build()));
        assertThat(eventService.hasRsvp(eventId, userId)).isTrue();
    }

    @Test
    void hasRsvp_nonExisting_returnsFalse() {
        UUID eventId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        when(rsvpRepo.findByUserIdAndEventId(userId, eventId)).thenReturn(Optional.empty());
        assertThat(eventService.hasRsvp(eventId, userId)).isFalse();
    }

    @Test
    void getOrganizerEvents_delegatesToRepo() {
        UUID orgId = UUID.randomUUID();
        when(eventRepo.findByOrganizerId(orgId)).thenReturn(List.of());
        assertThat(eventService.getOrganizerEvents(orgId)).isEmpty();
    }

    @Test
    void createEvent_savesWithDraftStatusAndParsedTags() {
        User organizer = User.builder().id(UUID.randomUUID()).build();
        CreateEventForm form = buildForm("java, spring");
        when(eventRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        Event result = eventService.createEvent(form, organizer);
        assertThat(result.getStatus()).isEqualTo(EventStatus.DRAFT);
        assertThat(result.getTags()).containsExactly("java", "spring");
    }

    @Test
    void createEvent_withNullTags_savesEmptyTagList() {
        User organizer = User.builder().id(UUID.randomUUID()).build();
        when(eventRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        Event result = eventService.createEvent(buildForm(null), organizer);
        assertThat(result.getTags()).isEmpty();
    }

    @Test
    void createEvent_withBlankTags_savesEmptyTagList() {
        User organizer = User.builder().id(UUID.randomUUID()).build();
        when(eventRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        Event result = eventService.createEvent(buildForm("   "), organizer);
        assertThat(result.getTags()).isEmpty();
    }

    @Test
    void updateEvent_asOwner_updatesFields() {
        User organizer = User.builder().id(UUID.randomUUID()).build();
        Event event = Event.builder().id(UUID.randomUUID()).organizer(organizer).build();
        CreateEventForm form = buildForm("tag1, tag2");
        form.setTitle("Updated");
        when(eventRepo.findById(event.getId())).thenReturn(Optional.of(event));
        when(eventRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        Event result = eventService.updateEvent(event.getId(), form, organizer);
        assertThat(result.getTitle()).isEqualTo("Updated");
    }

    @Test
    void updateEvent_asNonOwner_throwsAccessDenied() {
        User owner = User.builder().id(UUID.randomUUID()).build();
        User intruder = User.builder().id(UUID.randomUUID()).build();
        Event event = Event.builder().id(UUID.randomUUID()).organizer(owner).build();
        when(eventRepo.findById(event.getId())).thenReturn(Optional.of(event));
        assertThatThrownBy(() -> eventService.updateEvent(event.getId(), buildForm(null), intruder))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void checkOwner_sameUser_noException() {
        User owner = User.builder().id(UUID.randomUUID()).build();
        Event event = Event.builder().organizer(owner).build();
        eventService.checkOwner(event, owner);
    }

    @Test
    void checkOwner_differentUser_throwsAccessDenied() {
        User owner = User.builder().id(UUID.randomUUID()).build();
        User other = User.builder().id(UUID.randomUUID()).build();
        Event event = Event.builder().organizer(owner).build();
        assertThatThrownBy(() -> eventService.checkOwner(event, other))
                .isInstanceOf(AccessDeniedException.class);
    }

    private CreateEventForm buildForm(String tags) {
        CreateEventForm form = new CreateEventForm();
        form.setTitle("Test Event");
        form.setDescription("Description");
        form.setCategory("Workshop");
        form.setLocation("Room 1");
        form.setStartTime(LocalDateTime.now().plusDays(1));
        form.setEndTime(LocalDateTime.now().plusDays(1).plusHours(2));
        form.setCapacity(30);
        form.setTags(tags);
        return form;
    }
}
