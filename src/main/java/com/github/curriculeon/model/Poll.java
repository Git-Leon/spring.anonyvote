package com.github.curriculeon.model;

import javax.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Poll {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String question;

    private Instant createdAt;

    private Instant resultsVisibleAt;

    @OneToMany(mappedBy = "poll", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<PollOption> options = new ArrayList<>();

    public Poll() {}

    public Poll(String question, Instant createdAt, Instant resultsVisibleAt) {
        this.question = question;
        this.createdAt = createdAt;
        this.resultsVisibleAt = resultsVisibleAt;
    }

    public Long getId() {
        return id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getResultsVisibleAt() {
        return resultsVisibleAt;
    }

    public void setResultsVisibleAt(Instant resultsVisibleAt) {
        this.resultsVisibleAt = resultsVisibleAt;
    }

    public List<PollOption> getOptions() {
        return options;
    }

    public void setOptions(List<PollOption> options) {
        this.options.clear();
        if (options != null) this.options.addAll(options);
        for (PollOption o : this.options) {
            o.setPoll(this);
        }
    }

    public void addOption(PollOption option) {
        option.setPoll(this);
        this.options.add(option);
    }
}
