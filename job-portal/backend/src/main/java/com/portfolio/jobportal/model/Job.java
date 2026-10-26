package com.portfolio.jobportal.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "jobs")
@Getter @Setter @NoArgsConstructor
public class Job {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false) private String title;
    @Column(nullable = false) private String company;
    @Column(nullable = false) private String location;
    @Column(length = 2000) private String description;
    private LocalDateTime createdAt;
    @ManyToOne(optional = false)
    private User recruiter;

    @PrePersist
    void onCreate() { createdAt = LocalDateTime.now(); }
}
