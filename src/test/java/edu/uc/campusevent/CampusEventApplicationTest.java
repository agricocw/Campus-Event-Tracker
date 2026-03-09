package edu.uc.campusevent;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("dev")
class CampusEventApplicationTest {

    @Test
    void contextLoads() {
        // Verifies the full application context starts without errors
    }
}
