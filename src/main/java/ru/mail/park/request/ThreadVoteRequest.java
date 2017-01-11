package ru.mail.park.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ThreadVoteRequest {
    public final int thread;
    public final int vote;

    public ThreadVoteRequest(@JsonProperty("thread") int thread,
                             @JsonProperty("vote") int vote) {
        this.thread = thread;
        this.vote = vote;
    }

    public boolean isValid() {
        return vote == -1 || vote == 1;
    }

}
