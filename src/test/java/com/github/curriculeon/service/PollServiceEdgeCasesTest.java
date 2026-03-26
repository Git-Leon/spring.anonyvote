package com.github.curriculeon.service;

import com.github.curriculeon.model.Poll;
import com.github.curriculeon.model.PollOption;
import com.github.curriculeon.model.Vote;
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
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class PollServiceEdgeCasesTest {

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

    @Test
    public void isResultsVisible_before_equals_after_behaviour() {
        Poll p = new Poll();

        // future -> not visible
        p.setResultsVisibleAt(Instant.parse("2020-01-02T00:00:00Z"));
        assertFalse(pollService.isResultsVisible(p));

        // equal -> visible
        p.setResultsVisibleAt(Instant.parse("2020-01-01T00:00:00Z"));
        assertTrue(pollService.isResultsVisible(p));

        // past -> visible
        p.setResultsVisibleAt(Instant.parse("2019-12-31T00:00:00Z"));
        assertTrue(pollService.isResultsVisible(p));
    }

    @Test
    public void vote_nullFingerprint_skipsDuplicateCheck_andPersists() {
        Poll poll = new Poll();
        PollOption option = new PollOption("x");
        option.setPoll(poll);

        when(pollRepository.findById(1L)).thenReturn(Optional.of(poll));
        when(optionRepository.findById(2L)).thenReturn(Optional.of(option));
        when(voteRepository.save(any(Vote.class))).thenAnswer(i -> i.getArguments()[0]);

        Vote v = pollService.vote(1L, 2L, null);

        // duplicate check should not be invoked for null fingerprint
        verify(voteRepository, never()).existsByPoll_IdAndFingerprint(anyLong(), anyString());
        assertNull(v.getFingerprint());
        assertEquals(poll, v.getPoll());
    }

    @Test
    public void vote_blankFingerprint_skipsDuplicateCheck_andPersists() {
        Poll poll = new Poll();
        PollOption option = new PollOption("x");
        option.setPoll(poll);

        when(pollRepository.findById(1L)).thenReturn(Optional.of(poll));
        when(optionRepository.findById(2L)).thenReturn(Optional.of(option));
        when(voteRepository.save(any(Vote.class))).thenAnswer(i -> i.getArguments()[0]);

        Vote v = pollService.vote(1L, 2L, "   ");

        // duplicate check should not be invoked for blank fingerprint
        verify(voteRepository, never()).existsByPoll_IdAndFingerprint(anyLong(), anyString());
        assertEquals("   ", v.getFingerprint());
        assertEquals(poll, v.getPoll());
    }
}
