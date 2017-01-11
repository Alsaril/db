package ru.mail.park.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ThreadStatusRequest {
    public final int thread;

    public ThreadStatusRequest(@JsonProperty("thread") int thread) {
        this.thread = thread;
    }
}
