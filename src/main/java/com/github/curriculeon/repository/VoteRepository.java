package com.github.curriculeon.repository;

import com.github.curriculeon.model.Vote;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoteRepository extends CrudRepository<Vote, Long> {
    boolean existsByPoll_IdAndFingerprint(Long pollId, String fingerprint);

    long countByOption_Id(Long optionId);

    List<Vote> findByPoll_Id(Long pollId);

    List<Vote> findByPoll_IdAndFingerprint(Long pollId, String fingerprint);
}
