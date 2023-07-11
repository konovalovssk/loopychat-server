package com.fwea.loopychat.controller;

import com.fwea.loopychat.AppException;
import com.fwea.loopychat.entity.User;
import com.fwea.loopychat.form.LoginForm;
import com.fwea.loopychat.form.RegisterForm;
import com.fwea.loopychat.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.validation.BindingResult;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.core.Authentication;
import org.springframework.context.annotation.DependsOn;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@DependsOn("securityFilterChain")
public class AuthController {
    private final RememberMeServices rememberMeServices;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public CurrentUser login(@Valid @RequestBody LoginForm form, BindingResult bindingResult,
                                HttpServletRequest request, HttpServletResponse response) {
        log.info("[login] Begins '{}'", form.getUsername());
        if (request.getUserPrincipal() != null) {
            throw new AppException("Please logout first");
        }

        if (bindingResult.hasErrors()) {
            throw new AppException("Invalid username or password");
        }

        try {
            request.login(form.getUsername(), form.getPassword());
        } catch (ServletException e) {
            throw new AppException("Invalid username or password");
        }

        var auth = (Authentication) request.getUserPrincipal();
        var user = (User) auth.getPrincipal();
        log.info("User '{}' logged in", user.getUsername());

        rememberMeServices.loginSuccess(request, response, auth);
        return new CurrentUser(user.getId(), user.getUsername());
    }

    @PostMapping("/logout")
    public LogoutResponse logout(HttpServletRequest request) throws ServletException {
        request.logout();
        return new LogoutResponse();
    }

    @PostMapping("/register")
    public CurrentUser register(@Valid @RequestBody RegisterForm form, BindingResult bindingResult) {
        log.info("User '{}' register.", form.toString());
        if (userRepository.findByUsername(form.getUsername()).isPresent()) {
            throw new AppException("Username already used");
        }

        var user = new User();
        user.setGender(form.getGender());
        user.setUsername(form.getUsername());
        user.setPassword(passwordEncoder.encode(form.getPassword()));
        user.setCreatedAt(new Date());
        user.setUpdatedAt(new Date());

        User registered;
        try {
            registered = userRepository.save(user);
        } catch (Exception e) {
            throw new AppException("Internal server error");
        }

        return new CurrentUser(registered.getId(), registered.getUsername());
    }

    @GetMapping("/csrf")
    public CsrfResponse csrf(HttpServletRequest request) {
        var csrf = (CsrfToken) request.getAttribute("_csrf");
        return new CsrfResponse(csrf.getHeaderName(), csrf.getToken());
    }

    public record CsrfResponse(String headerName, String token) {}
    public record LogoutResponse() {}
    public record CurrentUser(Integer id, String username) {}
}
