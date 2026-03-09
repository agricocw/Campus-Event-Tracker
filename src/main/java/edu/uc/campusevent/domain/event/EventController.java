package edu.uc.campusevent.domain.event;

import edu.uc.campusevent.domain.rsvp.RsvpService;
import edu.uc.campusevent.domain.user.User;
import edu.uc.campusevent.domain.user.UserService;
import edu.uc.campusevent.shared.dto.CreateEventForm;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;
    private final UserService userService;
    private final RsvpService rsvpService;

    // GET /events?page=0&size=10&category=Workshop&q=spring
    @GetMapping
    public String listEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String q,
            Model model) {

        // #11 — Cap pagination size at 50
        size = Math.min(size, 50);

        Pageable pageable = PageRequest.of(page, size, Sort.by("startTime").ascending());

        Page<Event> events;
        if (q != null && !q.isBlank()) {
            events = eventService.search(q, pageable);
        } else if (category != null && !category.isBlank()) {
            // #7 — Category filtering
            events = eventService.getByCategory(category, pageable);
        } else {
            events = eventService.getPublishedEvents(pageable);
        }

        model.addAttribute("events", events);
        model.addAttribute("category", category);
        model.addAttribute("q", q);
        model.addAttribute("categories",
                java.util.List.of("Academic", "Social", "Sports", "Arts", "Career", "Workshop", "Other"));
        return "events/list";
    }

    // GET /events/{id}
    @GetMapping("/{id}")
    public String eventDetail(@PathVariable UUID id,
            @AuthenticationPrincipal UserDetails principal,
            Model model) {
        Event event = eventService.findById(id);
        model.addAttribute("event", event);
        if (principal != null) {
            User user = userService.findByEmail(principal.getUsername());
            boolean hasRsvp = eventService.hasRsvp(id, user.getId());
            model.addAttribute("hasRsvp", hasRsvp);
        }
        return "events/detail";
    }

    // POST /events/{id}/rsvp
    @PostMapping("/{id}/rsvp")
    public String rsvp(@PathVariable UUID id,
            @AuthenticationPrincipal UserDetails principal,
            RedirectAttributes ra) {
        User user = userService.findByEmail(principal.getUsername());
        rsvpService.rsvpToEvent(id, user);
        ra.addFlashAttribute("success", "You're registered! Check My Events.");
        return "redirect:/events/" + id;
    }

    // #3 — POST /events/{id}/cancel-rsvp
    @PostMapping("/{id}/cancel-rsvp")
    public String cancelRsvp(@PathVariable UUID id,
            @AuthenticationPrincipal UserDetails principal,
            RedirectAttributes ra) {
        User user = userService.findByEmail(principal.getUsername());
        rsvpService.cancelRsvp(id, user);
        ra.addFlashAttribute("success", "RSVP cancelled.");
        return "redirect:/events/" + id;
    }

    // GET /events/create (ORGANIZER only — guarded in SecurityConfig)
    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("form", new CreateEventForm());
        return "events/create";
    }

    // POST /events/create
    @PostMapping("/create")
    public String createSubmit(
            @Valid @ModelAttribute("form") CreateEventForm form,
            BindingResult result,
            @AuthenticationPrincipal UserDetails principal,
            RedirectAttributes ra) {
        if (result.hasErrors())
            return "events/create";
        User organizer = userService.findByEmail(principal.getUsername());
        Event event = eventService.createEvent(form, organizer);
        ra.addFlashAttribute("success", "Event created as draft.");
        return "redirect:/events/" + event.getId();
    }

    // #5 — GET /events/{id}/edit
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable UUID id,
            @AuthenticationPrincipal UserDetails principal,
            Model model) {
        User user = userService.findByEmail(principal.getUsername());
        Event event = eventService.findById(id);
        eventService.checkOwner(event, user);

        CreateEventForm form = new CreateEventForm();
        form.setTitle(event.getTitle());
        form.setDescription(event.getDescription());
        form.setCategory(event.getCategory());
        form.setLocation(event.getLocation());
        form.setStartTime(event.getStartTime());
        form.setEndTime(event.getEndTime());
        form.setCapacity(event.getCapacity());
        form.setTags(String.join(", ", event.getTags()));
        model.addAttribute("form", form);
        model.addAttribute("eventId", id);
        return "events/edit";
    }

    // #5 — POST /events/{id}/edit
    @PostMapping("/{id}/edit")
    public String editSubmit(@PathVariable UUID id,
            @Valid @ModelAttribute("form") CreateEventForm form,
            BindingResult result,
            @AuthenticationPrincipal UserDetails principal,
            RedirectAttributes ra,
            Model model) {
        if (result.hasErrors()) {
            model.addAttribute("eventId", id);
            return "events/edit";
        }
        User user = userService.findByEmail(principal.getUsername());
        eventService.updateEvent(id, form, user);
        ra.addFlashAttribute("success", "Event updated!");
        return "redirect:/events/" + id;
    }

    // POST /events/{id}/publish
    @PostMapping("/{id}/publish")
    public String publishEvent(@PathVariable UUID id,
            @AuthenticationPrincipal UserDetails principal,
            RedirectAttributes ra) {
        User user = userService.findByEmail(principal.getUsername());
        eventService.publishEvent(id, user);
        ra.addFlashAttribute("success", "Event published!");
        return "redirect:/events/" + id;
    }
}
