package edu.uc.campusevent.domain.user;

import edu.uc.campusevent.domain.event.Event;
import edu.uc.campusevent.domain.event.EventService;
import edu.uc.campusevent.domain.rsvp.Rsvp;
import edu.uc.campusevent.domain.rsvp.RsvpService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final RsvpService rsvpService;
    private final EventService eventService;

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
}
