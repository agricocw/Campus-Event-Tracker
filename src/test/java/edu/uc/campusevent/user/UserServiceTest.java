package edu.uc.campusevent.user;

import edu.uc.campusevent.auth.RegistrationForm;
import edu.uc.campusevent.domain.user.*;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock UserRepository userRepository;
    @Mock PasswordEncoder passwordEncoder;
    @InjectMocks UserService userService;

    @Test
    void findByEmail_existingUser_returnsUser() {
        User user = User.builder().email("test@uc.edu").build();
        when(userRepository.findByEmail("test@uc.edu")).thenReturn(Optional.of(user));
        assertThat(userService.findByEmail("test@uc.edu").getEmail()).isEqualTo("test@uc.edu");
    }

    @Test
    void findByEmail_nonExisting_throwsEntityNotFound() {
        when(userRepository.findByEmail("x@uc.edu")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.findByEmail("x@uc.edu"))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void registerUser_newUser_savesSuccessfully() {
        RegistrationForm form = new RegistrationForm();
        form.setEmail("new@uc.edu");
        form.setPassword("password123");
        form.setFirstName("John");
        form.setLastName("Doe");
        form.setRole("STUDENT");
        when(userRepository.existsByEmail("new@uc.edu")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encoded");
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        User result = userService.registerUser(form);
        assertThat(result.getEmail()).isEqualTo("new@uc.edu");
        assertThat(result.getRole()).isEqualTo(UserRole.STUDENT);
    }

    @Test
    void registerUser_duplicateEmail_throwsIllegalArgument() {
        RegistrationForm form = new RegistrationForm();
        form.setEmail("dup@uc.edu");
        when(userRepository.existsByEmail("dup@uc.edu")).thenReturn(true);
        assertThatThrownBy(() -> userService.registerUser(form))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
