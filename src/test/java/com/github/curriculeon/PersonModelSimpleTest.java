package com.github.curriculeon;

import com.github.curriculeon.model.Person;
import org.junit.Test;

import static org.junit.Assert.*;

public class PersonModelSimpleTest {

    @Test
    public void testGetSetId() {
        Person p = new Person();
        assertNull(p.getId());

        p.setId(123L);
        assertEquals(Long.valueOf(123L), p.getId());
    }

    @Test
    public void testFirstAndLastNameAccessors() {
        Person p = new Person();
        p.setFirstName("Alice");
        p.setLastName("Smith");

        assertEquals("Alice", p.getFirstName());
        assertEquals("Smith", p.getLastName());
    }
}
