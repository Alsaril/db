package ru.mail.park.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.util.StringUtils;

public class ThreadUpdateRequest {
    public final int thread;
    public final String message;
    public final String slug;

    public ThreadUpdateRequest(@JsonProperty("thread") int thread,
                               @JsonProperty("message") String message,
                               @JsonProperty("slug") String slug) {
        this.thread = thread;
        this.message = message;
        this.slug = slug;
    }

    public boolean isValid() {
        return !StringUtils.isEmpty(message) && !StringUtils.isEmpty(slug);
    }
}
