package edu.uc.campusevent.event;

import edu.uc.campusevent.domain.event.*;
import edu.uc.campusevent.domain.rsvp.RsvpService;
import edu.uc.campusevent.domain.user.User;
import edu.uc.campusevent.domain.user.UserService;
import edu.uc.campusevent.shared.dto.CreateEventForm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventControllerTest {

    @Mock EventService eventService;
    @Mock UserService userService;
    @Mock RsvpService rsvpService;
    @InjectMocks EventController controller;

    Model model;
    RedirectAttributesModelMap ra;
    UserDetails principal;
    User testUser;

    @BeforeEach
    void setUp() {
        model = new ConcurrentModel();
        ra = new RedirectAttributesModelMap();
        principal = org.springframework.security.core.userdetails.User
                .withUsername("test@uc.edu").password("p").roles("STUDENT").build();
        testUser = User.builder().id(UUID.randomUUID()).email("test@uc.edu").build();
    }

    @Test
    void listEvents_default_returnsPublishedEvents() {
        when(eventService.getPublishedEvents(any())).thenReturn(Page.empty());
        assertThat(controller.listEvents(0, 10, null, null, model)).isEqualTo("events/list");
    }

    @Test
    void listEvents_withSearch_callsSearch() {
        when(eventService.search(eq("spring"), any())).thenReturn(Page.empty());
        controller.listEvents(0, 10, null, "spring", model);
        verify(eventService).search(eq("spring"), any());
    }

    @Test
    void listEvents_withCategory_filtersCategory() {
        when(eventService.getByCategory(eq("Workshop"), any())).thenReturn(Page.empty());
        controller.listEvents(0, 10, "Workshop", null, model);
        verify(eventService).getByCategory(eq("Workshop"), any());
    }

    @Test
    void listEvents_capsPageSizeAt50() {
        when(eventService.getPublishedEvents(any())).thenReturn(Page.empty());
        controller.listEvents(0, 100, null, null, model);
        verify(eventService).getPublishedEvents(argThat(p -> p.getPageSize() == 50));
    }

    @Test
    void listEvents_blankSearch_returnsPublished() {
        when(eventService.getPublishedEvents(any())).thenReturn(Page.empty());
        controller.listEvents(0, 10, null, "  ", model);
        verify(eventService).getPublishedEvents(any());
    }

    @Test
    void listEvents_blankCategory_returnsPublished() {
        when(eventService.getPublishedEvents(any())).thenReturn(Page.empty());
        controller.listEvents(0, 10, "  ", null, model);
        verify(eventService).getPublishedEvents(any());
    }

    @Test
    void eventDetail_withAuth_checksRsvpStatus() {
        UUID id = UUID.randomUUID();
        Event event = Event.builder().id(id).build();
        when(eventService.findById(id)).thenReturn(event);
        when(userService.findByEmail("test@uc.edu")).thenReturn(testUser);
        when(eventService.hasRsvp(id, testUser.getId())).thenReturn(true);
        String view = controller.eventDetail(id, principal, model);
        assertThat(view).isEqualTo("events/detail");
        assertThat(model.getAttribute("hasRsvp")).isEqualTo(true);
    }

    @Test
    void eventDetail_withoutAuth_skipsRsvpCheck() {
        UUID id = UUID.randomUUID();
        when(eventService.findById(id)).thenReturn(Event.builder().id(id).build());
        assertThat(controller.eventDetail(id, null, model)).isEqualTo("events/detail");
    }

    @Test
    void rsvp_redirectsToEventDetail() {
        UUID id = UUID.randomUUID();
        when(userService.findByEmail("test@uc.edu")).thenReturn(testUser);
        assertThat(controller.rsvp(id, principal, ra)).isEqualTo("redirect:/events/" + id);
        verify(rsvpService).rsvpToEvent(id, testUser);
    }

    @Test
    void cancelRsvp_redirectsToEventDetail() {
        UUID id = UUID.randomUUID();
        when(userService.findByEmail("test@uc.edu")).thenReturn(testUser);
        assertThat(controller.cancelRsvp(id, principal, ra)).isEqualTo("redirect:/events/" + id);
        verify(rsvpService).cancelRsvp(id, testUser);
    }

    @Test
    void createForm_returnsCreateView() {
        assertThat(controller.createForm(model)).isEqualTo("events/create");
        assertThat(model.getAttribute("form")).isNotNull();
    }

    @Test
    void createSubmit_valid_redirectsToEvent() {
        CreateEventForm form = new CreateEventForm();
        form.setTitle("T");
        BindingResult br = new BeanPropertyBindingResult(form, "form");
        Event created = Event.builder().id(UUID.randomUUID()).build();
        when(userService.findByEmail("test@uc.edu")).thenReturn(testUser);
        when(eventService.createEvent(form, testUser)).thenReturn(created);
        assertThat(controller.createSubmit(form, br, principal, ra))
                .isEqualTo("redirect:/events/" + created.getId());
    }

    @Test
    void createSubmit_hasErrors_returnsCreateView() {
        CreateEventForm form = new CreateEventForm();
        BindingResult br = new BeanPropertyBindingResult(form, "form");
        br.rejectValue("title", "e", "required");
        assertThat(controller.createSubmit(form, br, principal, ra)).isEqualTo("events/create");
    }

    @Test
    void editForm_returnsEditView() {
        UUID id = UUID.randomUUID();
        Event event = Event.builder().id(id).title("T").description("D").category("C")
                .location("L").startTime(LocalDateTime.now()).endTime(LocalDateTime.now().plusHours(1))
                .capacity(30).tags(List.of("a", "b")).organizer(testUser).build();
        when(userService.findByEmail("test@uc.edu")).thenReturn(testUser);
        when(eventService.findById(id)).thenReturn(event);
        assertThat(controller.editForm(id, principal, model)).isEqualTo("events/edit");
        assertThat(model.getAttribute("eventId")).isEqualTo(id);
    }

    @Test
    void editSubmit_valid_redirectsToEvent() {
        UUID id = UUID.randomUUID();
        CreateEventForm form = new CreateEventForm();
        form.setTitle("U");
        BindingResult br = new BeanPropertyBindingResult(form, "form");
        when(userService.findByEmail("test@uc.edu")).thenReturn(testUser);
        assertThat(controller.editSubmit(id, form, br, principal, ra, model))
                .isEqualTo("redirect:/events/" + id);
    }

    @Test
    void editSubmit_hasErrors_returnsEditView() {
        UUID id = UUID.randomUUID();
        CreateEventForm form = new CreateEventForm();
        BindingResult br = new BeanPropertyBindingResult(form, "form");
        br.rejectValue("title", "e", "required");
        assertThat(controller.editSubmit(id, form, br, principal, ra, model))
                .isEqualTo("events/edit");
        assertThat(model.getAttribute("eventId")).isEqualTo(id);
    }

    @Test
    void publishEvent_redirectsToEvent() {
        UUID id = UUID.randomUUID();
        when(userService.findByEmail("test@uc.edu")).thenReturn(testUser);
        assertThat(controller.publishEvent(id, principal, ra))
                .isEqualTo("redirect:/events/" + id);
        verify(eventService).publishEvent(id, testUser);
    }
}
