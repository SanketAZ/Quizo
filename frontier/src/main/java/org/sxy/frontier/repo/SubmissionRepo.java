package org.sxy.frontier.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.sxy.frontier.module.Submission;

import java.util.UUID;

public interface SubmissionRepo extends JpaRepository<Submission, UUID> {
}