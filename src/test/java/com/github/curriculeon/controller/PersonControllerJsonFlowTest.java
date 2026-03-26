package com.github.curriculeon.controller;

import com.github.curriculeon.model.Person;
import com.github.curriculeon.repository.PersonRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class PersonControllerJsonFlowTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    PersonRepository personRepository;

    @Test
    public void postJson_then_getReadAll_containsCreatedPerson() throws Exception {
        // start clean
        personRepository.deleteAll();

        String payload = "{\"firstName\":\"FlowTester\",\"lastName\":\"JsonFlow\"}";

        // create person via JSON POST
        mockMvc.perform(post("/person-controller/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("FlowTester"));

        // fetch all as JSON and ensure created person is present
        mockMvc.perform(get("/person-controller/read-all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*].firstName", hasItem("FlowTester")));
    }
}
