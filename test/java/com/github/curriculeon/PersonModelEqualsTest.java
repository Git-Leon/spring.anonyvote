package com.github.curriculeon;

import com.github.curriculeon.model.Person;
import org.junit.Assert;
import org.junit.Test;

public class PersonModelEqualsTest {

    @Test
    public void equalsSameId() {
        Person a = new Person(1L, "Alice", "Smith");
        Person b = new Person(1L, "Alice", "Smith");

        Assert.assertEquals(a, b);
        Assert.assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    public void equalsDifferentIdSameData() {
        Person a = new Person(1L, "Alice", "Smith");
        Person b = new Person(2L, "Alice", "Smith");

        Assert.assertNotEquals(a, b);
    }

    @Test
    public void equalsNoIdSameFields() {
        Person a = new Person(null, "Bob", "Jones");
        Person b = new Person(null, "Bob", "Jones");

        Assert.assertEquals(a, b);
        Assert.assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    public void toStringNotEmpty() {
        Person p = new Person(5L, "Eve", "Adams");
        Assert.assertTrue(p.toString().contains("Eve"));
        Assert.assertTrue(p.toString().contains("Adams"));
    }
}
