package edu.uc.campusevent.config;

import edu.uc.campusevent.domain.event.EventRepository;
import edu.uc.campusevent.domain.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataSeederTest {

    @Mock UserRepository userRepo;
    @Mock EventRepository eventRepo;
    @Mock PasswordEncoder encoder;
    @InjectMocks DataSeeder dataSeeder;

    @Test
    void run_emptyDatabase_seedsData() {
        when(userRepo.count()).thenReturn(0L);
        when(encoder.encode(anyString())).thenReturn("encoded");
        when(userRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(eventRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        dataSeeder.run();
        verify(userRepo, times(3)).save(any());
        verify(eventRepo, times(6)).save(any());
    }

    @Test
    void run_existingData_skipsSeeding() {
        when(userRepo.count()).thenReturn(5L);
        dataSeeder.run();
        verify(userRepo, never()).save(any());
    }
}
