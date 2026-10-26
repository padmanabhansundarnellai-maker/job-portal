package com.portfolio.jobportal.controller;

import com.portfolio.jobportal.model.User;
import com.portfolio.jobportal.repository.ApplicationRepository;
import com.portfolio.jobportal.repository.JobRepository;
import com.portfolio.jobportal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {
    private final UserRepository users;
    private final JobRepository jobs;
    private final ApplicationRepository apps;

    @GetMapping("/stats")
    public Map<String, Long> stats() {
        return Map.of("users", users.count(), "jobs", jobs.count(), "applications", apps.count());
    }

    @GetMapping("/users")
    public List<User> users() {
        return users.findAll();
    }

    @DeleteMapping("/users/{id}")
    public void delete(@PathVariable Long id, Authentication auth) {
        User u = users.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        if (u.getEmail().equals(auth.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You cannot delete yourself");
        }
        try {
            users.delete(u);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User has jobs or applications and cannot be deleted");
        }
    }
}
