package com.github.curriculeon.controller;

import com.github.curriculeon.model.Person;
import com.github.curriculeon.service.PersonService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class PersonControllerDirectTest {

    @Mock
    private PersonService personService;

    private PersonController controller;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        controller = new PersonController(personService);
    }

    @Test
    public void createDefault_returnsCreatedPerson() {
        Person expected = new Person(1L, "Leon", "Hunter");
        when(personService.create(org.mockito.ArgumentMatchers.any(Person.class))).thenReturn(expected);

        ResponseEntity<Person> resp = controller.create();
        assertEquals(200, resp.getStatusCodeValue());
        assertNotNull(resp.getBody());
        assertEquals("Leon", resp.getBody().getFirstName());
    }

    @Test
    public void create_and_readAll_paths() {
        Person p = new Person(2L, "A", "B");
        when(personService.create(org.mockito.ArgumentMatchers.any(Person.class))).thenReturn(p);
        ResponseEntity<Person> created = controller.create(p);
        assertEquals(200, created.getStatusCodeValue());

        when(personService.readAll()).thenReturn(Arrays.asList(p));
        ResponseEntity<List<Person>> all = controller.readAll();
        assertEquals(200, all.getStatusCodeValue());
        assertTrue(all.getBody().size() >= 1);
    }

    @Test
    public void getPersonBean_returnsBean() {
        Person person = new Person(0L, "X", "Y");
        ResponseEntity<Person> resp = controller.getPersonBean(person);
        assertEquals(200, resp.getStatusCodeValue());
        assertEquals("X", resp.getBody().getFirstName());
    }
}
