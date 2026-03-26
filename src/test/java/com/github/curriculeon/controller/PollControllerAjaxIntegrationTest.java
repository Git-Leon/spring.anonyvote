package com.github.curriculeon.controller;

import com.github.curriculeon.model.Poll;
import com.github.curriculeon.model.PollOption;
import com.github.curriculeon.repository.PollRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class PollControllerAjaxIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    PollRepository pollRepository;

    @Test
    public void ajaxVote_then_countsReflectVote() throws Exception {
        // create a poll
        mockMvc.perform(post("/create")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("question", "Ajax integration test?")
                .param("option", "One")
                .param("option", "Two")
        ).andExpect(status().is3xxRedirection());

        List<Poll> polls = pollRepository.findAllByOrderByCreatedAtDesc();
        assertThat(polls).isNotEmpty();
        Poll poll = polls.get(0);

        // pick an option and cast an AJAX vote
        PollOption opt = poll.getOptions().get(0);
        mockMvc.perform(post("/poll/" + poll.getId() + "/vote")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("optionId", String.valueOf(opt.getId()))
                .header("X-Requested-With", "XMLHttpRequest")
        ).andExpect(status().isOk())
         .andExpect(jsonPath("$.message").exists());

        // request counts JSON and assert totalVotes >= 1
        mockMvc.perform(get("/poll/" + poll.getId() + "/counts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalVotes", greaterThanOrEqualTo(1)));
    }
}
