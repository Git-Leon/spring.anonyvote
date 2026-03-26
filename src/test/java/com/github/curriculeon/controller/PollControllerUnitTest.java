package com.github.curriculeon.controller;

import com.github.curriculeon.model.Poll;
import com.github.curriculeon.model.PollOption;
import com.github.curriculeon.service.PollService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class PollControllerUnitTest {

    @Mock
    PollService pollService;

    MockMvc mockMvc;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        PollController controller = new PollController(pollService);
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    public void viewPoll_showsPending_whenNotVisible() throws Exception {
        Poll p = new Poll();
        p.setQuestion("Q");
        p.setResultsVisibleAt(Instant.parse("2099-01-01T00:00:00Z"));
        when(pollService.findById(1L)).thenReturn(Optional.of(p));
        when(pollService.isResultsVisible(p)).thenReturn(false);

        mockMvc.perform(get("/poll/1")).andExpect(status().isOk())
                .andExpect(model().attribute("resultsVisible", false))
                .andExpect(model().attributeExists("poll"));
    }

    @Test
    public void viewPoll_showsResults_whenVisible() throws Exception {
        Poll p = new Poll();
        p.setQuestion("Q");
        p.setResultsVisibleAt(Instant.parse("2000-01-01T00:00:00Z"));
        PollOption o = new PollOption("x");
        o.setPoll(p);
        p.getOptions().add(o);
        when(pollService.findById(2L)).thenReturn(Optional.of(p));
        when(pollService.isResultsVisible(p)).thenReturn(true);
        Map<Long, Long> counts = new HashMap<>();
        counts.put(o.getId(), 5L);
        when(pollService.countsForPoll(p)).thenReturn(counts);

        mockMvc.perform(get("/poll/2")).andExpect(status().isOk())
                .andExpect(model().attribute("resultsVisible", true))
                .andExpect(model().attributeExists("counts"))
                .andExpect(model().attributeExists("totalVotes"));
    }

    @Test
    public void vote_duplicate_setsFlashError() throws Exception {
    // simulate duplicate vote by having service throw
    when(pollService.vote(eq(3L), anyLong(), anyString())).thenThrow(new IllegalStateException("dup"));

        mockMvc.perform(post("/poll/3/vote").param("optionId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("error"));
    }

    @Test
    public void vote_success_setsFlashMessage() throws Exception {
    // no exception -> success (return a dummy Vote)
    when(pollService.vote(eq(4L), anyLong(), anyString())).thenReturn(new com.github.curriculeon.model.Vote());

    mockMvc.perform(post("/poll/4/vote").param("optionId", "1"))
        .andExpect(status().is3xxRedirection())
        .andExpect(flash().attributeExists("message"));
    }
}
