package com.github.curriculeon.model;

import org.junit.Assert;
import org.junit.Test;

import java.time.Instant;

public class PollOptionVoteModelTest {

    @Test
    public void pollOption_settersAndVote_getFingerprint() {
        Poll p = new Poll("Q?", Instant.now(), Instant.now().plusSeconds(1000));
        PollOption o = new PollOption("text");
        o.setText("new");
        o.setPoll(p);

        Assert.assertEquals("new", o.getText());
        Assert.assertEquals(p, o.getPoll());

        Vote v = new Vote(p, o, Instant.now(), "fp-123");
        Assert.assertEquals("fp-123", v.getFingerprint());
    }

}
