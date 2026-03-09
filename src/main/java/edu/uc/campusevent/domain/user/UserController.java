package edu.uc.campusevent.domain.user;

import edu.uc.campusevent.domain.event.Event;
import edu.uc.campusevent.domain.event.EventService;
import edu.uc.campusevent.domain.rsvp.Rsvp;
import edu.uc.campusevent.domain.rsvp.RsvpService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final RsvpService rsvpService;
    private final EventService eventService;

    @GetMapping("/my-events")
    public String myEvents(@AuthenticationPrincipal UserDetails principal,
            Model model) {
        User user = userService.findByEmail(principal.getUsername());
        List<Rsvp> rsvps = rsvpService.getUserRsvps(user.getId());
        model.addAttribute("rsvps", rsvps);
        return "user/my-events";
    }

    // #8 — Organizer dashboard showing their created events
    @GetMapping("/my-created-events")
    public String myCreatedEvents(@AuthenticationPrincipal UserDetails principal,
            Model model) {
        User user = userService.findByEmail(principal.getUsername());
        List<Event> events = eventService.getOrganizerEvents(user.getId());
        model.addAttribute("events", events);
        return "user/my-created-events";
    }

    @GetMapping("/settings")
    public String settings(@AuthenticationPrincipal UserDetails principal,
            Model model) {
        User user = userService.findByEmail(principal.getUsername());
        model.addAttribute("user", user);
        return "user/settings";
    }
}
