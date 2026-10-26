package com.portfolio.jobportal.service;

import com.portfolio.jobportal.model.AppStatus;
import com.portfolio.jobportal.model.Application;
import com.portfolio.jobportal.model.Job;
import com.portfolio.jobportal.model.User;
import com.portfolio.jobportal.repository.ApplicationRepository;
import com.portfolio.jobportal.repository.JobRepository;
import com.portfolio.jobportal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ApplicationService {
    private final ApplicationRepository apps;
    private final JobRepository jobs;
    private final UserRepository users;
    private final FileStorageService files;
    private final EmailService email;

    public Application apply(Long jobId, String candidateEmail, MultipartFile resume) {
        User candidate = users.findByEmail(candidateEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
        Job job = jobs.findById(jobId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Job not found"));
        if (apps.existsByJobIdAndCandidateId(jobId, candidate.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "You already applied to this job");
        }
        Application a = new Application();
        a.setJob(job);
        a.setCandidate(candidate);
        a.setResumeFile(files.store(resume));
        return apps.save(a);
    }

    public List<Application> forCandidate(String email) {
        return apps.findByCandidateEmailOrderByAppliedAtDesc(email);
    }

    public List<Application> forRecruiter(String email) {
        return apps.findByJobRecruiterEmailOrderByAppliedAtDesc(email);
    }

    public Application updateStatus(Long id, AppStatus status, String recruiterEmail) {
        Application a = apps.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Application not found"));
        if (!a.getJob().getRecruiter().getEmail().equals(recruiterEmail)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This is not your job posting");
        }
        a.setStatus(status);
        Application saved = apps.save(a);
        email.sendStatusUpdate(saved.getCandidate().getEmail(), saved.getJob().getTitle(), status);
        return saved;
    }

    public Resource resume(Long id, String requesterEmail) {
        Application a = apps.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Application not found"));
        boolean allowed = a.getCandidate().getEmail().equals(requesterEmail)
                || a.getJob().getRecruiter().getEmail().equals(requesterEmail);
        if (!allowed) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed");
        }
        return files.load(a.getResumeFile());
    }
}
