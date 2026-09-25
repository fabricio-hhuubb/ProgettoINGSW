package com.bugboard26.backend.controller;

import com.bugboard26.backend.model.Role;
import com.bugboard26.backend.model.User;
import com.bugboard26.backend.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.bugboard26.backend.security.UserDetailsImpl;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

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
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        SecurityContextHolder.getContext().setAuthentication(auth);

        // Salva il contesto di sicurezza nella sessione HTTP -> genera il cookie JSESSIONID
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

    // CREAZIONE UTENTE: accesso riservato agli admin in SecurityConfig
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            return ResponseEntity.badRequest().body("Email già registrata");
        }

        User newUser = new User(
                request.email(),
                passwordEncoder.encode(request.password()),
                request.role()
        );
        userRepository.save(newUser);

        return ResponseEntity.ok("Utente creato");
    }

    @GetMapping("/me")
    public ResponseEntity<UserInfo> getCurrentUser(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = userDetails.getUser();

        return ResponseEntity.ok(new UserInfo(user.getEmail(), user.getRole()));
    }

    // Serve per restituire solo le informazioni utili al frontend
    public record UserInfo(String email, Role role) {}

    // DTO interni (record Java: sintassi compatta per classi "dati")
    public record LoginRequest(String email, String password) {}
    public record RegisterRequest(String email, String password, Role role) {}
}

// Cosa fa:

// /login — riceve email/password, chiede a Spring Security
// di verificarle (authenticationManager.authenticate),
// se sono corrette salva l'utente autenticato nella sessione HTTP → il browser
// riceve automaticamente un cookie JSESSIONID da quel momento in poi
// /logout — invalida la sessione, il cookie non è più valido
// /register — crea un nuovo utente, con la password
// hashata tramite passwordEncoder.encode(...)
// (qui usiamo finalmente l'hashing di cui parlavamo prima)