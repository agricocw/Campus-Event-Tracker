package edu.uc.campusevent.domain.user;

import edu.uc.campusevent.domain.event.Event;
import edu.uc.campusevent.domain.event.EventService;
import edu.uc.campusevent.domain.notification.NotificationService;
import edu.uc.campusevent.domain.rsvp.Rsvp;
import edu.uc.campusevent.domain.rsvp.RsvpService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final RsvpService rsvpService;
    private final EventService eventService;
    private final NotificationService notificationService;

    @GetMapping("/my-events")
    public String myEvents(@AuthenticationPrincipal User user, Model model) {
        List<Rsvp> rsvps = rsvpService.getUserRsvps(user.getId());
        model.addAttribute("rsvps", rsvps);
        return "user/my-events";
    }

    @GetMapping("/my-created-events")
    public String myCreatedEvents(@AuthenticationPrincipal User user, Model model) {
        List<Event> events = eventService.getOrganizerEvents(user.getId());
        model.addAttribute("events", events);
        return "user/my-created-events";
    }

    @GetMapping("/settings")
    public String settings(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("user", user);
        return "user/settings";
    }

    @PostMapping("/notifications/{id}/read")
    @ResponseBody
    public ResponseEntity<Void> markNotificationRead(
            @PathVariable UUID id,
            @AuthenticationPrincipal User user) {
        notificationService.markAsRead(id, user.getId());
        return ResponseEntity.ok().build();
    }
}
