package com.taskflow.repository;

import com.taskflow.common.enums.SprintStatus;
import com.taskflow.entity.Sprint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SprintRepository extends JpaRepository<Sprint, Long> {
    List<Sprint> findByProjectId(Long projectId);
    List<Sprint> findByStatus(SprintStatus status);
    long countByStatus(SprintStatus status);
}
