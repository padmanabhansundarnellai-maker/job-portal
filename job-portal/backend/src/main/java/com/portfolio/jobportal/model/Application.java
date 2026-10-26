package com.portfolio.jobportal.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "applications",
       uniqueConstraints = @UniqueConstraint(columnNames = {"job_id", "candidate_id"}))
@Getter @Setter @NoArgsConstructor
public class Application {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false) private Job job;
    @ManyToOne(optional = false) private User candidate;
    @Enumerated(EnumType.STRING)
    private AppStatus status = AppStatus.APPLIED;
    private String resumeFile;
    private LocalDateTime appliedAt;

    @PrePersist
    void onCreate() { appliedAt = LocalDateTime.now(); }
}
