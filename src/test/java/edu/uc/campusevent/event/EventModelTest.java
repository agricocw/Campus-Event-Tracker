package edu.uc.campusevent.event;

import edu.uc.campusevent.domain.event.Event;
import edu.uc.campusevent.domain.rsvp.Rsvp;
import edu.uc.campusevent.domain.rsvp.RsvpStatus;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class EventModelTest {

    @Test
    void getAttendeeCount_countsOnlyAttending() {
        Event event = Event.builder()
                .rsvps(new ArrayList<>(List.of(
                        Rsvp.builder().status(RsvpStatus.ATTENDING).build(),
                        Rsvp.builder().status(RsvpStatus.ATTENDING).build(),
                        Rsvp.builder().status(RsvpStatus.WAITLIST).build(),
                        Rsvp.builder().status(RsvpStatus.CANCELLED).build()
                ))).build();
        assertThat(event.getAttendeeCount()).isEqualTo(2);
    }

    @Test
    void getAttendeeCount_noRsvps_returnsZero() {
        assertThat(Event.builder().build().getAttendeeCount()).isEqualTo(0);
    }

    @Test
    void isFull_nullCapacity_returnsFalse() {
        assertThat(Event.builder().capacity(null).build().isFull()).isFalse();
    }

    @Test
    void isFull_underCapacity_returnsFalse() {
        Event event = Event.builder().capacity(10)
                .rsvps(new ArrayList<>(List.of(
                        Rsvp.builder().status(RsvpStatus.ATTENDING).build()
                ))).build();
        assertThat(event.isFull()).isFalse();
    }

    @Test
    void isFull_atCapacity_returnsTrue() {
        Event event = Event.builder().capacity(1)
                .rsvps(new ArrayList<>(List.of(
                        Rsvp.builder().status(RsvpStatus.ATTENDING).build()
                ))).build();
        assertThat(event.isFull()).isTrue();
    }
}
