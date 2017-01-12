package ru.mail.park.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PostVoteRequest {
    public final int post;
    public final int vote;

    public PostVoteRequest(@JsonProperty("post") int post,
                           @JsonProperty("vote") int vote) {
        this.post = post;
        this.vote = vote;
    }

    public boolean isValid() {
        return vote == -1 || vote == 1;
    }
}
