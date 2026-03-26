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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class PollServiceTest {

    @Mock
    private PollRepository pollRepository;

    @Mock
    private PollOptionRepository optionRepository;

    @Mock
    private VoteRepository voteRepository;

    private Clock fixedClock;

    private PollService pollService;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        Instant now = Instant.parse("2020-01-01T00:00:00Z");
        fixedClock = Clock.fixed(now, ZoneOffset.UTC);
        pollService = new PollService(pollRepository, optionRepository, voteRepository, fixedClock);
    }

    @Test
    public void createPoll_setsResultsVisibleAt24Hours() {
        when(pollRepository.save(any(Poll.class))).thenAnswer(i -> i.getArguments()[0]);

        Poll p = pollService.createPoll("Q?", Arrays.asList("A", "B"));

        assertEquals("Q?", p.getQuestion());
        assertEquals(Instant.parse("2020-01-01T00:00:00Z"), p.getCreatedAt());
        assertEquals(Instant.parse("2020-01-02T00:00:00Z"), p.getResultsVisibleAt());
        assertEquals(2, p.getOptions().size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void createPoll_nullOptions_throws() {
        pollService.createPoll("Q?", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createPoll_tooFewOptions_throws() {
        pollService.createPoll("Q?", Arrays.asList("onlyOne"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void createPoll_tooManyOptions_throws() {
        List<String> seven = Arrays.asList("o1","o2","o3","o4","o5","o6","o7");
        pollService.createPoll("Q?", seven);
    }

    @Test
    public void vote_duplicateFingerprint_throws() {
        when(pollRepository.findById(1L)).thenReturn(Optional.of(new Poll()));
        when(optionRepository.findById(2L)).thenReturn(Optional.of(new PollOption("x")));
        when(voteRepository.existsByPoll_IdAndFingerprint(1L, "fp")).thenReturn(true);

        try {
            pollService.vote(1L, 2L, "fp");
            fail("Expected IllegalStateException");
        } catch (IllegalStateException ignored) {
        }
    }

    @Test
    public void vote_success_persistsVote() {
        Poll poll = new Poll();
        PollOption option = new PollOption("x");
        option.setPoll(poll);

        when(pollRepository.findById(1L)).thenReturn(Optional.of(poll));
        when(optionRepository.findById(2L)).thenReturn(Optional.of(option));
        when(voteRepository.existsByPoll_IdAndFingerprint(1L, "fp")).thenReturn(false);
        when(voteRepository.save(any(Vote.class))).thenAnswer(i -> i.getArguments()[0]);

        Vote v = pollService.vote(1L, 2L, "fp");

        assertEquals(poll, v.getPoll());
        assertEquals(option, v.getOption());
        assertEquals(Instant.parse("2020-01-01T00:00:00Z"), v.getCreatedAt());
    }

    @Test
    public void recentPolls_delegatesToRepository() {
        List<Poll> sample = Arrays.asList(new Poll(), new Poll());
        when(pollRepository.findAllByOrderByCreatedAtDesc()).thenReturn(sample);

        List<Poll> out = pollService.recentPolls();

        assertEquals(sample, out);
    }

    @Test
    public void createPoll_ignoresEmptyAndTrimsOptions() {
        when(pollRepository.save(any(Poll.class))).thenAnswer(i -> i.getArguments()[0]);

        Poll p = pollService.createPoll("Q?", Arrays.asList("  A  ", "", "   ", "B"));

        assertEquals(2, p.getOptions().size());
        assertEquals("A", p.getOptions().get(0).getText());
        assertEquals("B", p.getOptions().get(1).getText());
    }

    @Test
    public void hasVoted_returnsTrueWhenVoteExists() {
        when(voteRepository.findByPoll_IdAndFingerprint(5L, "fp1")).thenReturn(Arrays.asList(new Vote()));

        boolean out = pollService.hasVoted(5L, "fp1");

        assertTrue(out);
    }

    @Test
    public void hasVoted_returnsFalseWhenNoVote() {
        when(voteRepository.findByPoll_IdAndFingerprint(6L, "fp2")).thenReturn(Arrays.asList());

        boolean out = pollService.hasVoted(6L, "fp2");

        assertFalse(out);
    }
}
