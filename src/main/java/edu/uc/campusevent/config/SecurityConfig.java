package edu.uc.campusevent.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // enables @PreAuthorize on service methods
public class SecurityConfig {

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                http
                                .authorizeHttpRequests(auth -> auth
                                                // Public routes
                                                .requestMatchers("/", "/events", "/events/{id}").permitAll()
                                                .requestMatchers("/auth/login", "/auth/register").permitAll()
                                                .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                                                .requestMatchers("/actuator/health").permitAll()
                                                .requestMatchers("/h2-console/**").permitAll() // H2 dev console
                                                // Organizer-only routes
                                                .requestMatchers("/events/create", "/events/{id}/edit")
                                                .hasRole("ORGANIZER")
                                                .requestMatchers("/events/{id}/delete").hasAnyRole("ORGANIZER", "ADMIN")
                                                // All other routes require authentication
                                                .anyRequest().authenticated())
                                .formLogin(form -> form
                                                .loginPage("/auth/login") // Custom Thymeleaf login page
                                                .loginProcessingUrl("/auth/login") // POST target
                                                .defaultSuccessUrl("/events", true)
                                                .failureUrl("/auth/login?error=true")
                                                .permitAll())
                                .logout(logout -> logout
                                                .logoutUrl("/auth/logout")
                                                .logoutSuccessUrl("/auth/login?logout=true")
                                                .invalidateHttpSession(true)
                                                .deleteCookies("JSESSIONID"))
                                .sessionManagement(session -> session
                                                .maximumSessions(1) // prevent concurrent logins
                                                .maxSessionsPreventsLogin(false) // new login kicks old session
                                )
                                .csrf(csrf -> csrf
                                                .ignoringRequestMatchers("/actuator/**", "/h2-console/**"))
                                .headers(headers -> headers
                                                .frameOptions(frame -> frame.sameOrigin()) // Allow H2 console iframe
                                );
                return http.build();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder(12); // strength factor 12
        }

        @Bean
        public AuthenticationManager authManager(AuthenticationConfiguration cfg)
                        throws Exception {
                return cfg.getAuthenticationManager();
        }
}
