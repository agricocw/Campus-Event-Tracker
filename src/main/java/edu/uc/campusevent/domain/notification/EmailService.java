package edu.uc.campusevent.domain.notification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Email service — currently logs emails to console.
 * To enable real email sending, uncomment spring-boot-starter-mail
 * in pom.xml and configure SMTP in application properties.
 */
@Service
@Slf4j
public class EmailService {

    public void sendSimpleEmail(String to, String subject, String body) {
        // Log-only implementation — no SMTP dependency required
        log.info("📧 EMAIL [to={}, subject={}]: {}", to, subject, body);
    }
}
