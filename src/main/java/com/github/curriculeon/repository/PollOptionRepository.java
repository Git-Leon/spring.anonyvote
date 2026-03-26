package com.github.curriculeon.repository;

import com.github.curriculeon.model.PollOption;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PollOptionRepository extends CrudRepository<PollOption, Long> {
}
