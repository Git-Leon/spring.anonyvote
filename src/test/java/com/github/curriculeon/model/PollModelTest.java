package com.github.curriculeon.model;

import org.junit.Assert;
import org.junit.Test;

import java.time.Instant;
import java.util.Arrays;

public class PollModelTest {

    @Test
    public void setOptions_handlesNullAndSetsBackReferences() {
        Poll p = new Poll("Q?", Instant.now(), Instant.now().plusSeconds(1000));

        // null should clear and not throw
        p.setOptions(null);
        Assert.assertNotNull(p.getOptions());
        Assert.assertTrue(p.getOptions().isEmpty());

        // set real options
        PollOption o1 = new PollOption("A");
        PollOption o2 = new PollOption("B");
        p.setOptions(Arrays.asList(o1, o2));

        Assert.assertEquals(2, p.getOptions().size());
        for (PollOption o : p.getOptions()) {
            Assert.assertEquals(p, o.getPoll());
        }
    }

}
