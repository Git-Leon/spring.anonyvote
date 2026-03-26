package com.github.curriculeon.service;

import com.github.curriculeon.model.Poll;
import com.github.curriculeon.model.PollOption;
import com.github.curriculeon.model.Vote;
import com.github.curriculeon.repository.PollOptionRepository;
import com.github.curriculeon.repository.PollRepository;
import com.github.curriculeon.repository.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PollService {
    private final PollRepository pollRepository;
    private final PollOptionRepository optionRepository;
    private final VoteRepository voteRepository;
    private final Clock clock;

    @Autowired
    public PollService(PollRepository pollRepository, PollOptionRepository optionRepository, VoteRepository voteRepository, Clock clock) {
        this.pollRepository = pollRepository;
        this.optionRepository = optionRepository;
        this.voteRepository = voteRepository;
        this.clock = clock;
    }

    public Poll createPoll(String question, List<String> optionTexts) {
        if (optionTexts == null) throw new IllegalArgumentException("Options required");
        if (optionTexts.size() < 2) throw new IllegalArgumentException("At least 2 options required");
        if (optionTexts.size() > 6) throw new IllegalArgumentException("At most 6 options allowed");

        Instant now = Instant.now(clock);
        Instant resultsVisibleAt = now.plus(Duration.ofHours(24));

        Poll poll = new Poll(question, now, resultsVisibleAt);
        for (String text : optionTexts) {
            poll.addOption(new PollOption(text));
        }
        return pollRepository.save(poll);
    }

    public Optional<Poll> findById(Long id) {
        return pollRepository.findById(id);
    }

    public List<Poll> recentPolls() {
        return pollRepository.findAllByOrderByCreatedAtDesc();
    }

    public boolean isResultsVisible(Poll poll) {
        Instant now = Instant.now(clock);
        return now.equals(poll.getResultsVisibleAt()) || now.isAfter(poll.getResultsVisibleAt());
    }

    /**
     * Cast an anonymous vote. If fingerprint is provided, attempt simple duplicate prevention.
     */
    public Vote vote(Long pollId, Long optionId, String fingerprint) {
        Poll poll = pollRepository.findById(pollId).orElseThrow(() -> new NoSuchElementException("Poll not found"));
        PollOption option = optionRepository.findById(optionId).orElseThrow(() -> new NoSuchElementException("Option not found"));

        if (fingerprint != null && !fingerprint.trim().isEmpty()) {
            if (voteRepository.existsByPoll_IdAndFingerprint(pollId, fingerprint)) {
                throw new IllegalStateException("Duplicate vote detected for this fingerprint");
            }
        }

        Vote vote = new Vote(poll, option, Instant.now(clock), fingerprint);
        return voteRepository.save(vote);
    }

    public Map<Long, Long> countsForPoll(Poll poll) {
        Map<Long, Long> map = new HashMap<>();
        for (PollOption o : poll.getOptions()) {
            long c = voteRepository.countByOption_Id(o.getId());
            map.put(o.getId(), c);
        }
        return map;
    }
}
