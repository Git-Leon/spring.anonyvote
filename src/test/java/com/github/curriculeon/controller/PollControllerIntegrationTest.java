package com.github.curriculeon.controller;

import com.github.curriculeon.model.Poll;
import com.github.curriculeon.model.PollOption;
import com.github.curriculeon.repository.PollRepository;
import com.github.curriculeon.repository.VoteRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class PollControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    PollRepository pollRepository;

    @Autowired
    VoteRepository voteRepository;

    @Test
    public void createViewAndVote_flow() throws Exception {
        // create a poll
        mockMvc.perform(post("/create")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("question", "Integration test?")
                .param("option", "Alpha")
                .param("option", "Beta")
        ).andExpect(status().is3xxRedirection());

        // check repository has poll
        List<Poll> polls = pollRepository.findAllByOrderByCreatedAtDesc();
        assertThat(polls).isNotEmpty();
        Poll poll = polls.get(0);

        // view the poll (results should be pending)
        MvcResult r = mockMvc.perform(get("/poll/" + poll.getId()))
                .andExpect(status().isOk())
                .andReturn();
        String content = r.getResponse().getContentAsString();
        assertThat(content).contains("Voting is open");
        assertThat(content).doesNotContain("Results (visible)");

        // vote for first option
        PollOption opt = poll.getOptions().get(0);
        mockMvc.perform(post("/poll/" + poll.getId() + "/vote")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("optionId", String.valueOf(opt.getId()))
        ).andExpect(status().is3xxRedirection());

        // verify a vote exists in repository
        long votes = voteRepository.countByOption_Id(opt.getId());
        assertThat(votes).isGreaterThanOrEqualTo(1L);
    }

    @Test
    public void createWithEmptyOptions_ignoresBlanksInRenderedPoll() throws Exception {
        // create a poll with extra empty option parameters
        mockMvc.perform(post("/create")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("question", "Front-end blanks test?")
                .param("option", "Mickey Mouse")
                .param("option", "Donald Duck")
                .param("option", "")
                .param("option", "   ")
                .param("option", "")
        ).andExpect(status().is3xxRedirection());

        // check repository has poll
        List<Poll> polls = pollRepository.findAllByOrderByCreatedAtDesc();
        assertThat(polls).isNotEmpty();
        Poll poll = polls.get(0);

        // view the poll page and ensure only non-empty options are rendered
        MvcResult r = mockMvc.perform(get("/poll/" + poll.getId()))
                .andExpect(status().isOk())
                .andReturn();
        String content = r.getResponse().getContentAsString();

        // The page should contain the provided option texts
        assertThat(content).contains("Mickey Mouse");
        assertThat(content).contains("Donald Duck");

        // It should NOT contain an empty span as a result of blank option text
        assertThat(content).doesNotContain("<span></span>");
        // Also avoid spans that only contain whitespace
        assertThat(content).doesNotContain("<span>   </span>");
    }

}
