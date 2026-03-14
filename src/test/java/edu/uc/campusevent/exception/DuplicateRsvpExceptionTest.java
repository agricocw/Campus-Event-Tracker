package edu.uc.campusevent.exception;

import edu.uc.campusevent.shared.exception.DuplicateRsvpException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DuplicateRsvpExceptionTest {

    @Test
    void constructor_setsMessage() {
        DuplicateRsvpException ex = new DuplicateRsvpException("test");
        assertThat(ex.getMessage()).isEqualTo("test");
    }
}
