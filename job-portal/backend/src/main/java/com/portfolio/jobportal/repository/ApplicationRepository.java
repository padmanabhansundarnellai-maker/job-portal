package com.portfolio.jobportal.repository;

import com.portfolio.jobportal.model.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    boolean existsByJobIdAndCandidateId(Long jobId, Long candidateId);
    List<Application> findByCandidateEmailOrderByAppliedAtDesc(String email);
    List<Application> findByJobRecruiterEmailOrderByAppliedAtDesc(String email);
    void deleteByJobId(Long jobId);
}
