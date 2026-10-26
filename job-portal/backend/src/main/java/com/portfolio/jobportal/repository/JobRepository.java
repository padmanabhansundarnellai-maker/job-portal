package com.portfolio.jobportal.repository;

import com.portfolio.jobportal.model.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long> {

    @Query("select j from Job j where "
         + "(lower(j.title) like lower(concat('%', :q, '%')) or lower(j.company) like lower(concat('%', :q, '%'))) "
         + "and lower(j.location) like lower(concat('%', :location, '%'))")
    Page<Job> search(@Param("q") String q, @Param("location") String location, Pageable pageable);

    List<Job> findByRecruiterEmailOrderByCreatedAtDesc(String email);
}
