package com.github.curriculeon;

import com.github.curriculeon.model.Person;
import org.junit.Assert;
import org.junit.Test;

public class PersonModelAdditionalBranchTests {

    @Test
    public void equalsNullReturnsFalse() {
        Person p = new Person(1L, "A", "B");
        Assert.assertFalse(p.equals(null));
    }

    @Test
    public void equalsDifferentClassReturnsFalse() {
        Person p = new Person(1L, "A", "B");
        Assert.assertFalse(p.equals("not a person"));
    }

    @Test
    public void equalsWhenIdsEqualIgnoresNames() {
        Person a = new Person(5L, "X", "Y");
        Person b = new Person(5L, "Alpha", "Beta");
        // When both ids are non-null and equal, equality is based on id only
        Assert.assertEquals(a, b);
        Assert.assertEquals(b, a);
    }

    @Test
    public void equalsWhenIdsDifferentButNamesMatch() {
        Person a = new Person(5L, "Same", "Names");
        Person b = new Person(6L, "Same", "Names");
        // Different non-null ids -> not equal
        Assert.assertNotEquals(a, b);
        Assert.assertNotEquals(b, a);
    }

    @Test
    public void equalsOneIdNullOtherNonNullButNamesMatch() {
        Person a = new Person(null, "First", "Last");
        Person b = new Person(42L, "First", "Last");
        // One id null and the other non-null -> fallback to business fields
        Assert.assertEquals(a, b);
        Assert.assertEquals(b, a);
    }
}
