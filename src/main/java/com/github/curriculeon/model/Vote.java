package com.github.curriculeon.model;

import javax.persistence.*;
import java.time.Instant;

@Entity
public class Vote {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Poll poll;

    @ManyToOne(fetch = FetchType.LAZY)
    private PollOption option;

    private Instant createdAt;

    /**
     * Optional anonymous fingerprint (cookie-based UUID) to reduce obvious duplicate voting.
     * Not required for voting; if absent duplicate protection is not applied.
     */
    private String fingerprint;

    public Vote() {}

    public Vote(Poll poll, PollOption option, Instant createdAt, String fingerprint) {
        this.poll = poll;
        this.option = option;
        this.createdAt = createdAt;
        this.fingerprint = fingerprint;
    }

    public Long getId() {
        return id;
    }

    public Poll getPoll() {
        return poll;
    }

    public PollOption getOption() {
        return option;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public String getFingerprint() {
        return fingerprint;
    }
}
