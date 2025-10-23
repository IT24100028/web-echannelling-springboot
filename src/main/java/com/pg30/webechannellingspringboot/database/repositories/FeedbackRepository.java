package com.pg30.webechannellingspringboot.database.repositories;

import com.pg30.webechannellingspringboot.entities.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
}
