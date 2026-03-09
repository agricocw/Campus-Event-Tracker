package edu.uc.campusevent.web;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HomeControllerTest {

    private final HomeController controller = new HomeController();

    @Test
    void home_redirectsToEvents() {
        assertThat(controller.home()).isEqualTo("redirect:/events");
    }
}
