package com.github.curriculeon.controller;

import com.github.curriculeon.model.Poll;
import com.github.curriculeon.model.PollOption;
import com.github.curriculeon.service.PollService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import javax.servlet.http.Cookie;
import java.time.Instant;
import java.util.*;

import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(PollController.class)
public class PollControllerMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PollService pollService;

    @Test
    public void indexAddsPollsAndNow() throws Exception {
        Poll p = new Poll("Q?", Instant.now(), Instant.now().plusSeconds(86400));
        when(pollService.recentPolls()).thenReturn(Arrays.asList(p));

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("polls"))
                .andExpect(model().attributeExists("now"));
    }

    @Test
    public void createFormReturnsCreateView() throws Exception {
        mockMvc.perform(get("/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("create"));
    }

    @Test
    public void viewPoll_noCookie_resultsNotVisible() throws Exception {
        Poll p = new Poll("Q?", Instant.now(), Instant.now().plusSeconds(86400));
        when(pollService.findById(1L)).thenReturn(Optional.of(p));
        when(pollService.isResultsVisible(p)).thenReturn(false);

        mockMvc.perform(get("/poll/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("poll"))
                .andExpect(model().attribute("resultsVisible", false))
                .andExpect(model().attribute("fingerprint", nullValue()));
    }

    @Test
    public void viewPoll_withCookie_and_visible_showsCounts() throws Exception {
        Poll p = new Poll("Q?", Instant.now().minusSeconds(100000), Instant.now().minusSeconds(1000));
        when(pollService.findById(2L)).thenReturn(Optional.of(p));
        when(pollService.isResultsVisible(p)).thenReturn(true);
        Map<Long, Long> counts = new HashMap<>();
        counts.put(10L, 2L);
        counts.put(11L, 3L);
        when(pollService.countsForPoll(p)).thenReturn(counts);

        mockMvc.perform(get("/poll/2").cookie(new Cookie("ANV_VOTER", "abc")))
                .andExpect(status().isOk())
                .andExpect(view().name("poll"))
                .andExpect(model().attribute("resultsVisible", true))
                .andExpect(model().attribute("fingerprint", "abc"))
                .andExpect(model().attributeExists("counts"))
                .andExpect(model().attributeExists("totalVotes"));
    }

    @Test
    public void vote_createsCookieAndRedirects() throws Exception {
        // no cookie provided => controller should create one
        mockMvc.perform(post("/poll/3/vote").param("optionId", "5").contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/poll/3"))
                .andExpect(cookie().exists("ANV_VOTER"));
    }

    @Test
    public void vote_duplicate_addsFlashError() throws Exception {
        // simulate duplicate vote thrown by service
        Mockito.doThrow(new IllegalStateException("duplicate")).when(pollService).vote(Mockito.eq(4L), Mockito.eq(6L), Mockito.anyString());

        mockMvc.perform(post("/poll/4/vote").param("optionId", "6").cookie(new Cookie("ANV_VOTER", "x")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/poll/4"))
                .andExpect(flash().attributeExists("error"));
    }

}
