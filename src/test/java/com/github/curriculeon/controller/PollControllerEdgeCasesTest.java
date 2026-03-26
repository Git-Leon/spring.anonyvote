package com.github.curriculeon.controller;

import com.github.curriculeon.model.Vote;
import com.github.curriculeon.service.PollService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.servlet.http.Cookie;
import java.util.Optional;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PollControllerEdgeCasesTest {

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
    public void viewPoll_missingPoll_throws() throws Exception {
        when(pollService.findById(999L)).thenReturn(Optional.empty());
        try {
            mockMvc.perform(get("/poll/999"));
        } catch (org.springframework.web.util.NestedServletException ex) {
            assertTrue(ex.getCause() instanceof java.util.NoSuchElementException);
            return;
        }

        // if no exception was thrown, fail the test
        throw new AssertionError("Expected NoSuchElementException to be thrown");
    }

    @Test
    public void vote_withExistingCookie_usesExistingFingerprint() throws Exception {
        // ensure controller reads fingerprint from cookie and forwards it to service
        when(pollService.vote(eq(5L), anyLong(), eq("fp"))).thenReturn(new Vote());

        mockMvc.perform(post("/poll/5/vote").cookie(new Cookie("ANV_VOTER", "fp")).param("optionId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("message"));

        verify(pollService).vote(eq(5L), anyLong(), eq("fp"));
    }

    @Test
    public void viewPoll_withCookie_setsFingerprintInModel() throws Exception {
        com.github.curriculeon.model.Poll p = new com.github.curriculeon.model.Poll();
        p.setQuestion("Q");
        when(pollService.findById(10L)).thenReturn(Optional.of(p));
        when(pollService.isResultsVisible(p)).thenReturn(false);

        mockMvc.perform(get("/poll/10").cookie(new javax.servlet.http.Cookie("ANV_VOTER", "cookie-fp")))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    Object fingerprint = result.getRequest().getAttribute("org.springframework.ui.Model");
                    // The Model is available as a request attribute; instead, assert via model map
                    javax.servlet.ServletRequest req = result.getRequest();
                    // Use the ModelAndView from the result to inspect model attributes
                    org.springframework.web.servlet.ModelAndView mav = result.getModelAndView();
                    assertNotNull(mav);
                    assertEquals("cookie-fp", mav.getModel().get("fingerprint"));
                });
    }

    @Test
    public void vote_withoutCookie_createsCookie_andCallsService() throws Exception {
        when(pollService.vote(eq(6L), anyLong(), anyString())).thenReturn(new Vote());

        mockMvc.perform(post("/poll/6/vote").param("optionId", "2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie().exists("ANV_VOTER"))
                .andExpect(flash().attributeExists("message"));

        // ensure service was called with some non-empty fingerprint
        verify(pollService).vote(eq(6L), anyLong(), org.mockito.ArgumentMatchers.argThat(s -> s != null && !s.trim().isEmpty()));
    }

}
