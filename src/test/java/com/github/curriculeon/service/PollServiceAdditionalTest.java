package com.github.curriculeon.service;

import com.github.curriculeon.model.Poll;
import com.github.curriculeon.model.PollOption;
import com.github.curriculeon.repository.PollOptionRepository;
import com.github.curriculeon.repository.PollRepository;
import com.github.curriculeon.repository.VoteRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Collections;
import java.util.NoSuchElementException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class PollServiceAdditionalTest {

    @Mock
    PollRepository pollRepository;

    @Mock
    PollOptionRepository optionRepository;

    @Mock
    VoteRepository voteRepository;

    Clock fixedClock;
    PollService pollService;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        fixedClock = Clock.fixed(Instant.parse("2020-01-01T00:00:00Z"), ZoneOffset.UTC);
        pollService = new PollService(pollRepository, optionRepository, voteRepository, fixedClock);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createPoll_tooFewOptions_throws() {
        pollService.createPoll("q", Collections.singletonList("onlyOne"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void createPoll_tooManyOptions_throws() {
        pollService.createPoll("q", Arrays.asList("1","2","3","4","5","6","7"));
    }

    @Test(expected = NoSuchElementException.class)
    public void vote_missingPoll_throws() {
        when(pollRepository.findById(99L)).thenReturn(java.util.Optional.empty());
        when(optionRepository.findById(1L)).thenReturn(java.util.Optional.of(new PollOption("x")));
        pollService.vote(99L, 1L, null);
    }

    @Test(expected = NoSuchElementException.class)
    public void vote_missingOption_throws() {
        when(pollRepository.findById(1L)).thenReturn(java.util.Optional.of(new Poll()));
        when(optionRepository.findById(2L)).thenReturn(java.util.Optional.empty());
        pollService.vote(1L, 2L, null);
    }

    @Test
    public void countsForPoll_returnsZeroWhenNoVotes() {
        Poll poll = new Poll();
        PollOption o1 = new PollOption("a");
        o1.setPoll(poll);
        poll.getOptions().add(o1);

        when(voteRepository.countByOption_Id(anyLong())).thenReturn(0L);

        java.util.Map<Long, Long> counts = pollService.countsForPoll(poll);
        // option id may be null (not persisted), ensure map contains key for option id null -> 0
        assertTrue(counts.containsKey(o1.getId()));
        assertEquals(Long.valueOf(0L), counts.get(o1.getId()));
    }
}
