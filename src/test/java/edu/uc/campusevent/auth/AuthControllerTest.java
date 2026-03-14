package edu.uc.campusevent.auth;

import edu.uc.campusevent.domain.user.User;
import edu.uc.campusevent.domain.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock UserService userService;
    @InjectMocks AuthController controller;
    Model model;
    RedirectAttributesModelMap ra;

    @BeforeEach
    void setUp() {
        model = new ConcurrentModel();
        ra = new RedirectAttributesModelMap();
    }

    @Test
    void loginPage_returnsLoginView() {
        assertThat(controller.loginPage()).isEqualTo("auth/login");
    }

    @Test
    void registerPage_returnsRegisterView() {
        assertThat(controller.registerPage(model)).isEqualTo("auth/register");
        assertThat(model.getAttribute("form")).isNotNull();
    }

    @Test
    void registerSubmit_valid_redirectsToLogin() {
        RegistrationForm form = new RegistrationForm();
        form.setEmail("n@uc.edu");
        form.setPassword("password123");
        form.setConfirmPassword("password123");
        form.setFirstName("J");
        form.setLastName("D");
        form.setRole("STUDENT");
        BindingResult br = new BeanPropertyBindingResult(form, "form");
        when(userService.registerUser(any())).thenReturn(User.builder().build());
        assertThat(controller.registerSubmit(form, br, ra, model)).isEqualTo("redirect:/auth/login");
    }

    @Test
    void registerSubmit_passwordMismatch_returnsRegister() {
        RegistrationForm form = new RegistrationForm();
        form.setPassword("password123");
        form.setConfirmPassword("different");
        BindingResult br = new BeanPropertyBindingResult(form, "form");
        assertThat(controller.registerSubmit(form, br, ra, model)).isEqualTo("auth/register");
        assertThat(br.hasErrors()).isTrue();
    }

    @Test
    void registerSubmit_bindingErrors_returnsRegister() {
        RegistrationForm form = new RegistrationForm();
        form.setPassword("p");
        form.setConfirmPassword("p");
        BindingResult br = new BeanPropertyBindingResult(form, "form");
        br.rejectValue("email", "e", "required");
        assertThat(controller.registerSubmit(form, br, ra, model)).isEqualTo("auth/register");
    }

    @Test
    void registerSubmit_serviceThrows_returnsRegisterWithError() {
        RegistrationForm form = new RegistrationForm();
        form.setEmail("dup@uc.edu");
        form.setPassword("password123");
        form.setConfirmPassword("password123");
        form.setFirstName("J");
        form.setLastName("D");
        form.setRole("STUDENT");
        BindingResult br = new BeanPropertyBindingResult(form, "form");
        when(userService.registerUser(any()))
                .thenThrow(new IllegalArgumentException("Email already registered"));
        assertThat(controller.registerSubmit(form, br, ra, model)).isEqualTo("auth/register");
        assertThat(model.getAttribute("error")).isEqualTo("Email already registered");
    }
}
