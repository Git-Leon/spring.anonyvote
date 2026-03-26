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

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class PersonControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    PersonRepository personRepository;

    @Test
    public void createPerson_jsonEndpoint_persistsAndReturnsCreated() throws Exception {
        // ensure repository is clean for test (in-memory DB)
        personRepository.deleteAll();

        String payload = "{\"firstName\":\"Integration\",\"lastName\":\"Tester\"}";

        mockMvc.perform(post("/person-controller/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Integration"))
                .andExpect(jsonPath("$.lastName").value("Tester"));

        // verify repository contains the created person
        List<Person> people = new ArrayList<>();
        personRepository.findAll().forEach(people::add);
        assertThat(people).isNotEmpty();
        boolean found = people.stream().anyMatch(p -> "Integration".equals(p.getFirstName()) && "Tester".equals(p.getLastName()));
        assertThat(found).isTrue();
    }
}
