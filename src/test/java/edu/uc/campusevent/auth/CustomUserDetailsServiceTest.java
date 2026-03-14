package edu.uc.campusevent.auth;

import edu.uc.campusevent.domain.user.User;
import edu.uc.campusevent.domain.user.UserRepository;
import edu.uc.campusevent.domain.user.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock UserRepository userRepository;
    @InjectMocks CustomUserDetailsService service;

    @Test
    void loadUserByUsername_existingUser_returnsUserDetails() {
        User user = User.builder()
                .email("test@uc.edu").passwordHash("hashed")
                .role(UserRole.STUDENT).enabled(true).build();
        when(userRepository.findByEmail("test@uc.edu")).thenReturn(Optional.of(user));
        UserDetails result = service.loadUserByUsername("test@uc.edu");
        assertThat(result.getUsername()).isEqualTo("test@uc.edu");
        assertThat(result.isAccountNonLocked()).isTrue();
    }

    @Test
    void loadUserByUsername_disabledUser_returnsLockedAccount() {
        User user = User.builder()
                .email("l@uc.edu").passwordHash("hashed")
                .role(UserRole.STUDENT).enabled(false).build();
        when(userRepository.findByEmail("l@uc.edu")).thenReturn(Optional.of(user));
        UserDetails result = service.loadUserByUsername("l@uc.edu");
        assertThat(result.isAccountNonLocked()).isFalse();
    }

    @Test
    void loadUserByUsername_nonExisting_throwsUsernameNotFound() {
        when(userRepository.findByEmail("x@uc.edu")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.loadUserByUsername("x@uc.edu"))
                .isInstanceOf(UsernameNotFoundException.class);
    }
}
