package com.github.curriculeon.controller;

import com.github.curriculeon.model.Person;
import com.github.curriculeon.service.PersonService;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;

public class PersonControllerDirectCoverageTest {

    @Test
    public void exerciseAllMethods() {
        PersonService service = Mockito.mock(PersonService.class);
        Person p = new Person(1L, "FN", "LN");

        Mockito.when(service.create(any(Person.class))).thenReturn(p);
        Mockito.when(service.read(1L)).thenReturn(p);
        Mockito.when(service.update(1L, p)).thenReturn(p);
        Mockito.when(service.delete(1L)).thenReturn(p);
        Mockito.when(service.readAll()).thenReturn(Arrays.asList(p));

        PersonController controller = new PersonController(service);

        ResponseEntity<Person> r1 = controller.create();
        Assert.assertEquals(HttpStatus.OK, r1.getStatusCode());

        ResponseEntity<Person> r2 = controller.getPersonBean(p);
        Assert.assertEquals(p, r2.getBody());

        ResponseEntity<Person> r3 = controller.create(p);
        Assert.assertEquals(p, r3.getBody());

        ResponseEntity<Person> r4 = controller.read(1L);
        Assert.assertEquals(p, r4.getBody());

        ResponseEntity<Person> r5 = controller.update(1L, p);
        Assert.assertEquals(p, r5.getBody());

        ResponseEntity<Person> r6 = controller.delete(1L);
        Assert.assertEquals(p, r6.getBody());

        ResponseEntity<java.util.List<Person>> r7 = controller.readAll();
        Assert.assertEquals(1, r7.getBody().size());
    }

}
