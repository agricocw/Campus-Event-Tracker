package edu.uc.campusevent.user;

import edu.uc.campusevent.domain.event.EventService;
import edu.uc.campusevent.domain.rsvp.RsvpService;
import edu.uc.campusevent.domain.user.User;
import edu.uc.campusevent.domain.user.UserController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock RsvpService rsvpService;
    @Mock EventService eventService;
    @InjectMocks UserController controller;

    Model model;
    User testUser;

    @BeforeEach
    void setUp() {
        model = new ConcurrentModel();
        testUser = User.builder().id(UUID.randomUUID()).email("test@uc.edu").build();
    }

    @Test
    void myEvents_returnsMyEventsView() {
        when(rsvpService.getUserRsvps(testUser.getId())).thenReturn(List.of());
        assertThat(controller.myEvents(testUser, model)).isEqualTo("user/my-events");
    }

    @Test
    void myCreatedEvents_returnsCreatedEventsView() {
        when(eventService.getOrganizerEvents(testUser.getId())).thenReturn(List.of());
        assertThat(controller.myCreatedEvents(testUser, model)).isEqualTo("user/my-created-events");
    }

    @Test
    void settings_returnsSettingsView() {
        assertThat(controller.settings(testUser, model)).isEqualTo("user/settings");
        assertThat(model.getAttribute("user")).isEqualTo(testUser);
    }
}
