package edu.uc.campusevent.web;

import edu.uc.campusevent.domain.notification.NotificationRepository;
import edu.uc.campusevent.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalModelAdvice {

    private final NotificationRepository notificationRepository;

    @ModelAttribute
    public void addNotificationData(@AuthenticationPrincipal User principal, Model model) {
        if (principal != null) {
            var unread = notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(principal.getId());
            model.addAttribute("unreadNotifications", unread);
            model.addAttribute("unreadNotificationCount", unread.size());
        }
    }
}
