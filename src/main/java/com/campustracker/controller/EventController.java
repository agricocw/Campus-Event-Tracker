package com.campustracker.controller;

import com.campustracker.model.Event;
import com.campustracker.model.User;
import com.campustracker.service.EventService;
import com.campustracker.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Controller
public class EventController {

    private final EventService eventService;
    private final UserService userService;

    public EventController(EventService eventService, UserService userService) {
        this.eventService = eventService;
        this.userService = userService;
    }

    @GetMapping("/events")
    public String eventFeed(Model model, Principal principal) {
        List<Event> events = eventService.getUpcomingEvents();
        model.addAttribute("events", events);
        return "events";
    }

    @GetMapping("/events/{id}")
    public String eventDetail(@PathVariable Long id, Model model, Principal principal) {
        Event event = eventService.getEventById(id);
        if (event == null) {
            return "redirect:/events";
        }
        User user = userService.findByEmail(principal.getName());
        boolean hasRsvped = eventService.hasRsvped(id, user);
        model.addAttribute("event", event);
        model.addAttribute("hasRsvped", hasRsvped);
        return "event-detail";
    }

    @GetMapping("/events/new")
    public String createEventForm() {
        return "create-event";
    }

    @PostMapping("/events/new")
    public String createEvent(@RequestParam String title,
                              @RequestParam String description,
                              @RequestParam String date,
                              @RequestParam String location,
                              Principal principal,
                              RedirectAttributes redirectAttributes) {
        try {
            User organizer = userService.findByEmail(principal.getName());
            Event event = new Event(title, description, LocalDateTime.parse(date), location, organizer);
            eventService.createEvent(event);
            redirectAttributes.addFlashAttribute("success", "Event created successfully!");
            return "redirect:/events";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to create event: " + e.getMessage());
            return "redirect:/events/new";
        }
    }

    @PostMapping("/events/{id}/rsvp")
    public String rsvp(@PathVariable Long id, Principal principal, RedirectAttributes redirectAttributes) {
        User user = userService.findByEmail(principal.getName());
        eventService.rsvp(id, user);
        redirectAttributes.addFlashAttribute("success", "RSVP confirmed!");
        return "redirect:/events/" + id;
    }

    @PostMapping("/events/{id}/cancel-rsvp")
    public String cancelRsvp(@PathVariable Long id, Principal principal, RedirectAttributes redirectAttributes) {
        User user = userService.findByEmail(principal.getName());
        eventService.cancelRsvp(id, user);
        redirectAttributes.addFlashAttribute("success", "RSVP cancelled.");
        return "redirect:/events/" + id;
    }

    @GetMapping("/my-events")
    public String myEvents(Model model, Principal principal) {
        User user = userService.findByEmail(principal.getName());
        Set<Event> rsvpedEvents = user.getRsvpedEvents();
        model.addAttribute("events", rsvpedEvents);
        return "my-events";
    }
}
