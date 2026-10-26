package com.portfolio.jobportal;

import com.portfolio.jobportal.model.*;
import com.portfolio.jobportal.repository.ApplicationRepository;
import com.portfolio.jobportal.repository.JobRepository;
import com.portfolio.jobportal.repository.UserRepository;
import com.portfolio.jobportal.service.ApplicationService;
import com.portfolio.jobportal.service.EmailService;
import com.portfolio.jobportal.service.FileStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApplicationServiceTest {
    @Mock ApplicationRepository apps;
    @Mock JobRepository jobs;
    @Mock UserRepository users;
    @Mock FileStorageService files;
    @Mock EmailService email;
    @InjectMocks ApplicationService service;

    private User candidate;
    private User recruiter;
    private Job job;

    @BeforeEach
    void setUp() {
        candidate = new User();
        candidate.setId(1L);
        candidate.setEmail("c@x.com");
        candidate.setRole(Role.CANDIDATE);
        recruiter = new User();
        recruiter.setId(2L);
        recruiter.setEmail("r@x.com");
        recruiter.setRole(Role.RECRUITER);
        job = new Job();
        job.setId(10L);
        job.setTitle("Java Developer");
        job.setRecruiter(recruiter);
    }

    @Test
    void applyingTwiceToSameJobIsRejected() {
        when(users.findByEmail("c@x.com")).thenReturn(Optional.of(candidate));
        when(jobs.findById(10L)).thenReturn(Optional.of(job));
        when(apps.existsByJobIdAndCandidateId(10L, 1L)).thenReturn(true);
        MockMultipartFile cv = new MockMultipartFile("resume", "cv.pdf", "application/pdf", "x".getBytes());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.apply(10L, "c@x.com", cv));

        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
        verify(files, never()).store(any());
    }

    @Test
    void onlyTheJobOwnerCanChangeStatus() {
        Application app = new Application();
        app.setJob(job);
        app.setCandidate(candidate);
        when(apps.findById(1L)).thenReturn(Optional.of(app));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.updateStatus(1L, AppStatus.SHORTLISTED, "other@x.com"));

        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
        verify(apps, never()).save(any());
    }

    @Test
    void ownerUpdatesStatusAndCandidateIsNotified() {
        Application app = new Application();
        app.setJob(job);
        app.setCandidate(candidate);
        when(apps.findById(1L)).thenReturn(Optional.of(app));
        when(apps.save(any(Application.class))).thenAnswer(inv -> inv.getArgument(0));

        Application result = service.updateStatus(1L, AppStatus.SHORTLISTED, "r@x.com");

        assertEquals(AppStatus.SHORTLISTED, result.getStatus());
        verify(email).sendStatusUpdate(eq("c@x.com"), eq("Java Developer"), eq(AppStatus.SHORTLISTED));
    }
}
