package com.github.curriculeon;

import com.github.curriculeon.model.Person;
import org.junit.Assert;
import org.junit.Test;

public class PersonModelBranchCoverageTest {

    @Test
    public void equalsSameInstance() {
        Person p = new Person(1L, "Same", "Instance");
        Assert.assertTrue(p.equals(p));
    }

    @Test
    public void equalsFirstNameNullVsNonNull() {
        Person a = new Person(null, null, "Last");
        Person b = new Person(null, "First", "Last");
        Assert.assertNotEquals(a, b);
        Assert.assertNotEquals(b, a);
    }

    @Test
    public void equalsLastNameNullVsNonNull() {
        Person a = new Person(null, "First", null);
        Person b = new Person(null, "First", "Last");
        Assert.assertNotEquals(a, b);
        Assert.assertNotEquals(b, a);
    }

    @Test
    public void equalsBothNamesNull() {
        Person a = new Person(null, null, null);
        Person b = new Person(null, null, null);
        Assert.assertEquals(a, b);
    }

    @Test
    public void hashCodeWithId() {
        Person p = new Person(99L, "X", "Y");
        Assert.assertEquals(Long.valueOf(99L).hashCode(), p.hashCode());
    }

    @Test
    public void hashCodeWithNoNames() {
        Person p = new Person(null, null, null);
        Assert.assertEquals(0, p.hashCode());
    }

    @Test
    public void hashCodeWithFirstNameOnly() {
        Person p = new Person(null, "Solo", null);
        int expected = 31 * ("Solo".hashCode()) + 0;
        // When id is null, implementation uses firstName hash then multiplies by 31 and adds lastName hash
        Assert.assertEquals(expected, p.hashCode());
    }

    @Test
    public void toStringContainsFields() {
        Person p = new Person(7L, "Alpha", "Beta");
        String s = p.toString();
        Assert.assertTrue(s.contains("Alpha"));
        Assert.assertTrue(s.contains("Beta"));
        Assert.assertTrue(s.contains("7"));
    }
}
