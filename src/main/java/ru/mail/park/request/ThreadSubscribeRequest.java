package ru.mail.park.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.util.StringUtils;

public class ThreadSubscribeRequest {
    public final int thread;
    public final String user;

    public ThreadSubscribeRequest(@JsonProperty("thread") int thread,
                                  @JsonProperty("user") String user) {
        this.thread = thread;
        this.user = user;
    }

    public boolean isValid() {
        return !StringUtils.isEmpty(user);
    }

}
