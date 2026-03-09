package edu.uc.campusevent.domain.rsvp;

import edu.uc.campusevent.domain.event.Event;
import edu.uc.campusevent.domain.event.EventRepository;
import edu.uc.campusevent.domain.event.EventStatus;
import edu.uc.campusevent.domain.notification.NotificationService;
import edu.uc.campusevent.domain.user.User;
import edu.uc.campusevent.shared.exception.DuplicateRsvpException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class RsvpService {

    private final RsvpRepository rsvpRepo;
    private final EventRepository eventRepo;
    private final NotificationService notifService;

    public Rsvp rsvpToEvent(UUID eventId, User user) {
        Event event = eventRepo.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event not found"));

        if (event.getStatus() != EventStatus.PUBLISHED) {
            throw new IllegalStateException("Event is not open for RSVPs");
        }

        if (rsvpRepo.findByUserIdAndEventId(user.getId(), eventId).isPresent()) {
            throw new DuplicateRsvpException("Already RSVPed to this event");
        }

        RsvpStatus status = event.isFull() ? RsvpStatus.WAITLIST : RsvpStatus.ATTENDING;

        Rsvp rsvp = Rsvp.builder()
                .user(user)
                .event(event)
                .status(status)
                .build();

        rsvpRepo.save(rsvp);
        notifService.sendRsvpConfirmation(user, event, status);
        return rsvp;
    }

    public void cancelRsvp(UUID eventId, User user) {
        Rsvp rsvp = rsvpRepo.findByUserIdAndEventId(user.getId(), eventId)
                .orElseThrow(() -> new EntityNotFoundException("RSVP not found"));
        rsvpRepo.delete(rsvp);
        promoteFromWaitlist(eventId); // fill the vacated spot
    }

    @Transactional(readOnly = true)
    public List<Rsvp> getUserRsvps(UUID userId) {
        return rsvpRepo.findByUserIdOrderByCreatedAtDesc(userId);
    }

    private void promoteFromWaitlist(UUID eventId) {
        rsvpRepo.findFirstByEventIdAndStatusOrderByCreatedAtAsc(eventId, RsvpStatus.WAITLIST)
                .ifPresent(waitlistRsvp -> {
                    waitlistRsvp.setStatus(RsvpStatus.ATTENDING);
                    rsvpRepo.save(waitlistRsvp);
                    notifService.sendWaitlistPromotion(
                            waitlistRsvp.getUser(), waitlistRsvp.getEvent());
                });
    }
}
