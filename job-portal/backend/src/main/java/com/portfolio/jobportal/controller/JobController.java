package com.portfolio.jobportal.controller;

import com.portfolio.jobportal.dto.Dtos.JobRequest;
import com.portfolio.jobportal.model.Job;
import com.portfolio.jobportal.model.Role;
import com.portfolio.jobportal.model.User;
import com.portfolio.jobportal.repository.ApplicationRepository;
import com.portfolio.jobportal.repository.JobRepository;
import com.portfolio.jobportal.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class JobController {
    private final JobRepository jobs;
    private final UserRepository users;
    private final ApplicationRepository apps;

    @GetMapping("/jobs")
    public Page<Job> search(@RequestParam(defaultValue = "") String q,
                            @RequestParam(defaultValue = "") String location,
                            @RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "5") int size) {
        return jobs.search(q.trim(), location.trim(),
                PageRequest.of(page, Math.min(size, 50), Sort.by(Sort.Direction.DESC, "createdAt")));
    }

    @GetMapping("/jobs/{id}")
    public Job get(@PathVariable Long id) {
        return jobs.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Job not found"));
    }

    @PostMapping("/jobs")
    @PreAuthorize("hasRole('RECRUITER')")
    public Job create(@Valid @RequestBody JobRequest r, Authentication auth) {
        User recruiter = users.findByEmail(auth.getName()).orElseThrow();
        Job j = new Job();
        j.setTitle(r.title());
        j.setCompany(r.company());
        j.setLocation(r.location());
        j.setDescription(r.description());
        j.setRecruiter(recruiter);
        return jobs.save(j);
    }

    @GetMapping("/recruiter/jobs")
    @PreAuthorize("hasRole('RECRUITER')")
    public List<Job> mine(Authentication auth) {
        return jobs.findByRecruiterEmailOrderByCreatedAtDesc(auth.getName());
    }

    @DeleteMapping("/jobs/{id}")
    @PreAuthorize("hasAnyRole('RECRUITER','ADMIN')")
    @Transactional
    public void delete(@PathVariable Long id, Authentication auth) {
        Job j = jobs.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Job not found"));
        boolean admin = users.findByEmail(auth.getName()).orElseThrow().getRole() == Role.ADMIN;
        if (!admin && !j.getRecruiter().getEmail().equals(auth.getName())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This is not your job posting");
        }
        apps.deleteByJobId(id);
        jobs.delete(j);
    }
}
