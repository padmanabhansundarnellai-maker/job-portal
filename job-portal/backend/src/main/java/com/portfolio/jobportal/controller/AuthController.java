package com.portfolio.jobportal.controller;

import com.portfolio.jobportal.dto.Dtos.AuthResponse;
import com.portfolio.jobportal.dto.Dtos.LoginRequest;
import com.portfolio.jobportal.dto.Dtos.RegisterRequest;
import com.portfolio.jobportal.model.Role;
import com.portfolio.jobportal.model.User;
import com.portfolio.jobportal.repository.UserRepository;
import com.portfolio.jobportal.security.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authManager;
    private final JwtService jwt;

    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody RegisterRequest r) {
        if (r.role() == Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Choose Candidate or Recruiter");
        }
        if (users.existsByEmail(r.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
        }
        User u = new User();
        u.setName(r.name());
        u.setEmail(r.email());
        u.setPassword(encoder.encode(r.password()));
        u.setRole(r.role());
        users.save(u);
        return response(u);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest r) {
        authManager.authenticate(new UsernamePasswordAuthenticationToken(r.email(), r.password()));
        User u = users.findByEmail(r.email()).orElseThrow();
        return response(u);
    }

    private AuthResponse response(User u) {
        return new AuthResponse(jwt.generate(u.getEmail(), u.getRole().name()), u.getName(), u.getEmail(), u.getRole());
    }
}
