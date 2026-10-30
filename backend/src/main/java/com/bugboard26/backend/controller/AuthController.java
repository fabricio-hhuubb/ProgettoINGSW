package com.bugboard26.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bugboard26.backend.dto.LoginRequest;
import com.bugboard26.backend.dto.RegisterRequest;
import com.bugboard26.backend.dto.UserInfo;
import com.bugboard26.backend.model.User;
import com.bugboard26.backend.repository.UserRepository;
import com.bugboard26.backend.security.UserDetailsImpl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authenticationManager,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // LOGIN
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));

        SecurityContextHolder.getContext().setAuthentication(auth);

        // Salva il contesto di sicurezza nella sessione HTTP -> genera il cookie
        // JSESSIONID
        HttpSession session = httpRequest.getSession(true);
        session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

        return ResponseEntity.ok("Login effettuato");
    }

    // LOGOUT
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok("Logout effettuato");
    }

    // CREAZIONE UTENTE accesso riservato agli admin in SecurityConfig
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {

        // Controlliamo la password originale prima dell'hashing
        if (request.password() == null || request.password().isBlank()) {
            return ResponseEntity.badRequest().body("La password non può essere vuota o contenere solo spazi");
        }

        if (userRepository.existsByEmail(request.email())) {
            return ResponseEntity.badRequest().body("Email già registrata");
        }

        User newUser = new User(
                request.email(),
                passwordEncoder.encode(request.password()),
                request.role());
        userRepository.save(newUser);

        return ResponseEntity.ok("Utente creato");
    }

    @GetMapping("/me")
    public ResponseEntity<UserInfo> getCurrentUser(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = userDetails.getUser();

        return ResponseEntity.ok(new UserInfo(user.getEmail(), user.getRole()));
    }

}
