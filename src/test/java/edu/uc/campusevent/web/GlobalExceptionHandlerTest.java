package edu.uc.campusevent.web;

import edu.uc.campusevent.shared.exception.DuplicateRsvpException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleNotFound_returns404View() {
        Model model = new ConcurrentModel();
        assertThat(handler.handleNotFound(new EntityNotFoundException("gone"), model))
                .isEqualTo("error/404");
        assertThat(model.getAttribute("message")).isEqualTo("gone");
    }

    @Test
    void handleForbidden_returns403View() {
        Model model = new ConcurrentModel();
        assertThat(handler.handleForbidden(new AccessDeniedException("no"), model))
                .isEqualTo("error/403");
    }

    @Test
    void handleDuplicateRsvp_withReferer_redirectsToReferer() {
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getHeader("Referer")).thenReturn("/events/123");
        RedirectAttributesModelMap ra = new RedirectAttributesModelMap();
        assertThat(handler.handleDuplicateRsvp(new DuplicateRsvpException("dup"), req, ra))
                .isEqualTo("redirect:/events/123");
    }

    @Test
    void handleDuplicateRsvp_noReferer_redirectsToEvents() {
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getHeader("Referer")).thenReturn(null);
        RedirectAttributesModelMap ra = new RedirectAttributesModelMap();
        assertThat(handler.handleDuplicateRsvp(new DuplicateRsvpException("dup"), req, ra))
                .isEqualTo("redirect:/events");
    }
}
