package edu.uc.campusevent.user;

import edu.uc.campusevent.domain.event.EventService;
import edu.uc.campusevent.domain.rsvp.RsvpService;
import edu.uc.campusevent.domain.user.User;
import edu.uc.campusevent.domain.user.UserController;
import edu.uc.campusevent.domain.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock UserService userService;
    @Mock RsvpService rsvpService;
    @Mock EventService eventService;
    @InjectMocks UserController controller;

    Model model;
    UserDetails principal;
    User testUser;

    @BeforeEach
    void setUp() {
        model = new ConcurrentModel();
        principal = org.springframework.security.core.userdetails.User
                .withUsername("test@uc.edu").password("p").roles("STUDENT").build();
        testUser = User.builder().id(UUID.randomUUID()).email("test@uc.edu").build();
    }

    @Test
    void myEvents_returnsMyEventsView() {
        when(userService.findByEmail("test@uc.edu")).thenReturn(testUser);
        when(rsvpService.getUserRsvps(testUser.getId())).thenReturn(List.of());
        assertThat(controller.myEvents(principal, model)).isEqualTo("user/my-events");
    }

    @Test
    void myCreatedEvents_returnsCreatedEventsView() {
        when(userService.findByEmail("test@uc.edu")).thenReturn(testUser);
        when(eventService.getOrganizerEvents(testUser.getId())).thenReturn(List.of());
        assertThat(controller.myCreatedEvents(principal, model)).isEqualTo("user/my-created-events");
    }

    @Test
    void settings_returnsSettingsView() {
        when(userService.findByEmail("test@uc.edu")).thenReturn(testUser);
        assertThat(controller.settings(principal, model)).isEqualTo("user/settings");
        assertThat(model.getAttribute("user")).isEqualTo(testUser);
    }
}
