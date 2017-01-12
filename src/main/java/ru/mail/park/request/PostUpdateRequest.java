package ru.mail.park.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.util.StringUtils;

public class PostUpdateRequest {
    public final int post;
    public final String message;

    public PostUpdateRequest(@JsonProperty("post") int post,
                             @JsonProperty("message") String message) {
        this.post = post;
        this.message = message;
    }

    public boolean isValid() {
        return !StringUtils.isEmpty(message);
    }
}
