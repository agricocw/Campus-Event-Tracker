package edu.uc.campusevent.web;

import edu.uc.campusevent.shared.exception.DuplicateRsvpException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(EntityNotFoundException.class)
    public String handleNotFound(EntityNotFoundException ex, Model model) {
        logger.error("Entity not found: {}", ex.getMessage(), ex);
        model.addAttribute("message", ex.getMessage());
        return "error/404";
    }

    @ExceptionHandler(AccessDeniedException.class)
    public String handleForbidden(AccessDeniedException ex, Model model) {
        logger.warn("Access denied: {}", ex.getMessage(), ex);
        model.addAttribute("message", "You don't have permission to access this.");
        return "error/403";
    }

    @ExceptionHandler(DuplicateRsvpException.class)
    public String handleDuplicateRsvp(
            DuplicateRsvpException ex,
            HttpServletRequest request,
            RedirectAttributes ra) {

        logger.warn("Duplicate RSVP attempt detected", ex);

        ra.addFlashAttribute("error", "You've already RSVPed to this event.");
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/events");
    }
}