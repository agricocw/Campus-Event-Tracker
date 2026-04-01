package edu.uc.campusevent.domain.event;

import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class EventCalendarService {

    private static final DateTimeFormatter ICS_UTC = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'");

    public String generateEventIcs(Event event) {
        String uid = event.getId() + "@campus-event-tracker";
        String dtStamp = ICS_UTC.format(event.getUpdatedAt() != null ? event.getUpdatedAt().atOffset(ZoneOffset.UTC)
                : event.getStartTime().atOffset(ZoneOffset.UTC));
        String dtStart = ICS_UTC.format(event.getStartTime().atOffset(ZoneOffset.UTC));
        String dtEnd = ICS_UTC.format(event.getEndTime().atOffset(ZoneOffset.UTC));

        StringBuilder builder = new StringBuilder();
        builder.append("BEGIN:VCALENDAR\r\n")
                .append("VERSION:2.0\r\n")
                .append("PRODID:-//Campus Event Tracker//EN\r\n")
                .append("CALSCALE:GREGORIAN\r\n")
                .append("BEGIN:VEVENT\r\n")
                .append("UID:").append(escape(uid)).append("\r\n")
                .append("DTSTAMP:").append(dtStamp).append("\r\n")
                .append("DTSTART:").append(dtStart).append("\r\n")
                .append("DTEND:").append(dtEnd).append("\r\n")
                .append("SUMMARY:").append(escape(event.getTitle())).append("\r\n")
                .append("LOCATION:").append(escape(event.getLocation())).append("\r\n")
                .append("DESCRIPTION:").append(escape(event.getDescription())).append("\r\n")
                .append("URL:https://campus-events.local/events/").append(event.getId()).append("\r\n")
                .append("END:VEVENT\r\n")
                .append("END:VCALENDAR\r\n");
        return builder.toString();
    }

    public String calendarFilename(UUID eventId) {
        return "event-" + eventId + ".ics";
    }

    private String escape(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\")
                .replace(",", "\\,")
                .replace(";", "\\;")
                .replace("\r\n", "\\n")
                .replace("\n", "\\n");
    }
}
