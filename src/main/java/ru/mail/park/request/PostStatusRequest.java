package ru.mail.park.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PostStatusRequest {
    public final int post;

    public PostStatusRequest(@JsonProperty("post") int post) {
        this.post = post;
    }
}
