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

    @Test
    public void equalsNullAndDifferentClass() {
        Person a = new Person(10L, "X", "Y");
        Assert.assertNotEquals(a, null);
        Assert.assertNotEquals(a, "not a person");
    }

    @Test
    public void equalsOneIdNullOtherNonNullButSameFields() {
        Person a = new Person(null, "Sam", "Lane");
        Person b = new Person(20L, "Sam", "Lane");

        // Should compare by fields when id isn't present on both
        Assert.assertEquals(a, b);
    }

    @Test
    public void equalsFieldNullCombinations() {
        Person a = new Person(null, null, "Solo");
        Person b = new Person(null, null, "Solo");
        Assert.assertEquals(a, b);

        Person c = new Person(null, "Alpha", null);
        Person d = new Person(null, "Alpha", null);
        Assert.assertEquals(c, d);

        Person e = new Person(null, null, null);
        Person f = new Person(null, null, null);
        Assert.assertEquals(e, f);

        // mismatches
        Person g = new Person(null, "A", "B");
        Person h = new Person(null, "A", "C");
        Assert.assertNotEquals(g, h);
    }
}
