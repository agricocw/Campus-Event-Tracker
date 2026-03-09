package edu.uc.campusevent.notification;

import edu.uc.campusevent.domain.notification.EmailService;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;

class EmailServiceTest {

    private final EmailService emailService = new EmailService();

    @Test
    void sendSimpleEmail_logsWithoutException() {
        assertThatCode(() -> emailService.sendSimpleEmail("t@uc.edu", "Subject", "Body"))
                .doesNotThrowAnyException();
    }
}
