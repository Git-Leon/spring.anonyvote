package com.github.curriculeon.repository;

import com.github.curriculeon.model.Poll;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PollRepository extends CrudRepository<Poll, Long> {
    List<Poll> findAllByOrderByCreatedAtDesc();
}
