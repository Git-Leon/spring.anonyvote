package com.github.curriculeon.controller;

import com.github.curriculeon.model.Poll;
import com.github.curriculeon.model.Vote;
import com.github.curriculeon.service.PollService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import javax.servlet.http.Cookie;
import java.time.Instant;
import java.util.Optional;

import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(SpringRunner.class)
@WebMvcTest(PollController.class)
public class PollControllerAdditionalBranchTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PollService pollService;

    @Test
    public void viewPoll_cookiesPresent_butNoAnvVoter_fingerprintIsNull() throws Exception {
        Poll p = new Poll("Q?", Instant.now(), Instant.now().plusSeconds(86400));
        when(pollService.findById(7L)).thenReturn(Optional.of(p));
        when(pollService.isResultsVisible(p)).thenReturn(false);

        // send a cookie array that does NOT include ANV_VOTER
        mockMvc.perform(get("/poll/7").cookie(new Cookie("OTHER", "v1"), new Cookie("ANOTHER", "v2")))
                .andExpect(status().isOk())
                .andExpect(view().name("poll"))
                .andExpect(model().attribute("fingerprint", nullValue()));
    }

    @Test
    public void vote_blankFingerprint_cookieReplaced_andServiceCalledWithNewFingerprint() throws Exception {
        when(pollService.vote(eq(8L), ArgumentMatchers.anyLong(), ArgumentMatchers.anyString())).thenReturn(new Vote());

        // send a blank ANV_VOTER cookie (only whitespace) to exercise trim().isEmpty() branch
        mockMvc.perform(post("/poll/8/vote").cookie(new Cookie("ANV_VOTER", "   ")).param("optionId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(cookie().exists("ANV_VOTER"));

        // ensure service was called with a non-empty fingerprint that is not the original blank value
        verify(pollService).vote(eq(8L), ArgumentMatchers.anyLong(), ArgumentMatchers.argThat(s -> s != null && !s.trim().isEmpty() && !s.equals("   ")));
    }

}
