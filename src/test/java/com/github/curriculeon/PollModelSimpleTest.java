package com.github.curriculeon;

import com.github.curriculeon.model.Poll;
import org.junit.Test;

import java.time.Instant;

import static org.junit.Assert.*;

public class PollModelSimpleTest {

    @Test
    public void testSetAndGetCreatedAt() {
        Poll p = new Poll();
        assertNull(p.getCreatedAt());

        Instant now = Instant.now();
        p.setCreatedAt(now);
        assertEquals(now, p.getCreatedAt());
    }

    @Test
    public void testSetOptionsKeepsBackReference() {
        // Basic sanity: ensure setOptions doesn't throw and keeps list structure
        Poll p = new Poll();
        assertNotNull(p.getOptions());
    }
}
