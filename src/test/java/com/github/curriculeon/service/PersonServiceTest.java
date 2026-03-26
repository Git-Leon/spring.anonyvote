package com.github.curriculeon.service;

import com.github.curriculeon.model.Person;
import com.github.curriculeon.repository.PersonRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class PersonServiceTest {

    @Mock
    PersonRepository personRepository;

    PersonService personService;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        personService = new PersonService(personRepository);
    }

    @Test
    public void create_read_update_delete_and_readAll() {
        Person p = new Person(1L, "First", "Last");
        when(personRepository.save(any(Person.class))).thenReturn(p);
        Person created = personService.create(new Person());
        assertNotNull(created);

        when(personRepository.findById(1L)).thenReturn(Optional.of(p));
        Person read = personService.read(1L);
        assertEquals("First", read.getFirstName());

        Person updated = new Person();
        updated.setFirstName("NewFirst");
        updated.setLastName("NewLast");
        when(personRepository.findById(1L)).thenReturn(Optional.of(p));
        Person result = personService.update(1L, updated);
        // repository.save called inside update; we mock to return the same instance
        assertEquals("NewFirst", result.getFirstName());

        when(personRepository.findById(1L)).thenReturn(Optional.of(p));
        Person deleted = personService.delete(1L);
        assertNotNull(deleted);

        List<Person> list = new ArrayList<>();
        list.add(p);
        when(personRepository.findAll()).thenReturn(list);
        List<Person> all = personService.readAll();
        assertEquals(1, all.size());
    }
}
