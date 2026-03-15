package edu.uc.campusevent.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller responsible for handling the root URL of the application.
 * Redirects users to the main event feed.
 */
@Controller
public class HomeController {

    /**
     * Redirects the root path ("/") to the event listing page.
     *
     * @return redirect to the events feed
     */
    @GetMapping("/")
    public String home() {
        return "redirect:/events";
    }
}