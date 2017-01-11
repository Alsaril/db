package ru.mail.park.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.util.StringUtils;

public class ThreadCreateRequest {
    public final String forum;
    public final String title;
    public final boolean isClosed;
    public final String user;
    public final String date;
    public final String message;
    public final String slug;
    public final boolean isDeleted;

    public ThreadCreateRequest(@JsonProperty("forum") String forum,
                               @JsonProperty("title") String title,
                               @JsonProperty("isClosed") boolean isClosed,
                               @JsonProperty("user") String user,
                               @JsonProperty("date") String date,
                               @JsonProperty("message") String message,
                               @JsonProperty("slug") String slug,
                               @JsonProperty("isDeleted") boolean isDeleted) {
        this.forum = forum;
        this.title = title;
        this.isClosed = isClosed;
        this.user = user;
        this.date = date;
        this.message = message;
        this.slug = slug;
        this.isDeleted = isDeleted;
    }

    public boolean isValid() {
        return !StringUtils.isEmpty(forum) &&
                !StringUtils.isEmpty(title) &&
                !StringUtils.isEmpty(user) &&
                !StringUtils.isEmpty(date) &&
                !StringUtils.isEmpty(message) &&
                !StringUtils.isEmpty(slug);
    }
}
