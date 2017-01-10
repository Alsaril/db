package ru.mail.park.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.util.StringUtils;

public class PostCreateRequest {
    public final String date;
    public final int thread;
    public final String message;
    public final String user;
    public final String forum;

    public final Integer parent;
    public final boolean isApproved;
    public final boolean isHighlighted;
    public final boolean isEdited;
    public final boolean isSpam;
    public final boolean isDeleted;

    public PostCreateRequest(@JsonProperty("date") String date,
                             @JsonProperty("thread") int thread,
                             @JsonProperty("message") String message,
                             @JsonProperty("user") String user,
                             @JsonProperty("forum") String forum,
                             @JsonProperty("parent") Integer parent,
                             @JsonProperty("isApproved") boolean isApproved,
                             @JsonProperty("isHighlighted") boolean isHighlighted,
                             @JsonProperty("isEdited") boolean isEdited,
                             @JsonProperty("isSpam") boolean isSpam,
                             @JsonProperty("isDeleted") boolean isDeleted) {
        this.date = date;
        this.thread = thread;
        this.message = message;
        this.user = user;
        this.forum = forum;
        this.parent = parent;
        this.isApproved = isApproved;
        this.isHighlighted = isHighlighted;
        this.isEdited = isEdited;
        this.isSpam = isSpam;
        this.isDeleted = isDeleted;
    }

    public boolean isValid() { //TODO check validation
        return !StringUtils.isEmpty(date) && !StringUtils.isEmpty(user) && !StringUtils.isEmpty(forum);
    }
}
