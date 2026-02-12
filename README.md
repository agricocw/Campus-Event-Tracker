# Campus Event Tracker – Design Document
 
## 1. Introduction
 
The **Campus Event Tracker** is a mobile and web application designed to help students discover, RSVP to, and receive reminders for campus events. It centralizes event information from various departments and student organizations, making it easier for students to stay engaged with campus life.
 
Students often miss events due to scattered announcements or lack of reminders. This application improves campus engagement and communication by providing a single, organized platform for event discovery and participation.
 
**Target Users**
- Students
- Event organizers (student organizations and campus departments)
 
---
 
## 2. Storyboard (Screen Mockups)
 
### Planned Screens
- Login / Signup
- Event Feed (list of upcoming events)
- Event Details (date, time, location, description, RSVP button)
- My Events (RSVPed events and reminders)
- Create Event (for organizers)
- Settings (profile and notification preferences)
 
Mockups will be created using **PowerPoint or Figma** and linked here once finalized.
 
---
 
## 3. Functional Requirements
 
### Requirement 1 – Browse Events
- As a student  
- I want to browse upcoming campus events  
- So that I can decide which ones to attend  
 
**Scenarios**
- Given I am logged in  
- When I open the event feed  
- Then I see a list of upcoming events with key details  
 
- Given there are no upcoming events  
- When I open the event feed  
- Then I see a message stating “No upcoming events available.”
 
---
 
### Requirement 2 – RSVP to Events
- As a student
- I want to RSVP to events
- So that I can receive reminders and updates  
 
**Scenarios**
- Given I am viewing an event  
- When I click the RSVP button  
- Then the event is added to my upcoming events and reminders are enabled  
 
- Given I am not logged in  
- When I attempt to RSVP  
- Then I am prompted to log in before proceeding
 
---
 
### Requirement 3 – Create Events
- As an event organizer  
- I want to create and publish events  
- So that students can discover and attend them  
 
**Scenarios**
- Given I am logged in as an organizer  
- When I submit a completed event creation form  
- Then the event appears in the public event feed  
 
- Given required fields are missing  
- When I submit the event creation form  
- Then validation errors are displayed and the event is not published
 
---
 
## 4. Class Diagram
 
```mermaid
classDiagram
    class User {
        String id
        String name
        String email
        List<Event> rsvpedEvents
    }
 
    class Event {
        String id
        String title
        String description
        Date date
        String location
        List<User> attendees
    }
 
    class Organizer {
        String id
        String name
        List<Event> createdEvents
    }
 
    User --> Event : RSVPs
    Organizer --> Event : Creates

## 5. Class Diagram Description
User: Represents a student who browses events and RSVPs.
Event: Represents a campus event with details such as date, location, and attendees
Organizer: Represents a user with permission to create and manage events.
 
## 6. JSON Schema
 
{
  "$schema": "http://json-schema.org/draft-07/schema#",
  "title": "CampusEvent",
  "type": "object",
  "properties": {
    "id": { "type": "string" },
    "title": { "type": "string" },
    "description": { "type": "string" },
    "date": { "type": "string", "format": "date-time" },
    "location": { "type": "string" },
    "attendees": {
      "type": "array",
      "items": { "type": "string" }
    }
  },
  "required": ["id", "title", "date", "location"]
}

## 7. Scrum Roles
 
Product Owner: Christopher Agricola
Scrum Master: Riddhi Mahajan
DevOps: Shamak Patel
Frontend Developer: Rudi Vogel 
Backend Developer: Riddhi Mahajan
 
## 8. GitHub Project and Milestones
GitHub Repository: https://github.com/agricocw/Campus-Event-Tracker/tree/main
GitHub Project Board: [Add project board link]
 
Milestones
Milestone #1: User login, event feed, RSVP functionality
Milestone #2: Notifications and reminders
Milestone #3: Calendar integration and UI polish
 
## 9. Weekly Meeting
Time: TBD
Platform: Microsoft Teams
Meeting Link: Emailed to instructor and team members
