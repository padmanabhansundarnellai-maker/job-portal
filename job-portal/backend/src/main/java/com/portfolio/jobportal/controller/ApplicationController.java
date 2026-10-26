package com.portfolio.jobportal.controller;

import com.portfolio.jobportal.model.AppStatus;
import com.portfolio.jobportal.model.Application;
import com.portfolio.jobportal.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplicationController {
    private final ApplicationService service;

    @PostMapping(value = "/{jobId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('CANDIDATE')")
    public Application apply(@PathVariable Long jobId, @RequestParam("resume") MultipartFile resume, Authentication auth) {
        return service.apply(jobId, auth.getName(), resume);
    }

    @GetMapping("/mine")
    @PreAuthorize("hasRole('CANDIDATE')")
    public List<Application> mine(Authentication auth) {
        return service.forCandidate(auth.getName());
    }

    @GetMapping("/recruiter")
    @PreAuthorize("hasRole('RECRUITER')")
    public List<Application> forRecruiter(Authentication auth) {
        return service.forRecruiter(auth.getName());
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('RECRUITER')")
    public Application updateStatus(@PathVariable Long id, @RequestParam AppStatus status, Authentication auth) {
        return service.updateStatus(id, status, auth.getName());
    }

    @GetMapping("/{id}/resume")
    public ResponseEntity<Resource> resume(@PathVariable Long id, Authentication auth) {
        Resource file = service.resume(id, auth.getName());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .body(file);
    }
}
