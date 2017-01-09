package ru.mail.park.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.util.StringUtils;

public class FollowRequest {
    public final String follower;
    public final String followee;

    public FollowRequest(@JsonProperty("follower") String follower,
                         @JsonProperty("followee") String followee) {
        this.follower = follower;
        this.followee = followee;
    }

    public boolean isValid() {
        return !(StringUtils.isEmpty(follower) || StringUtils.isEmpty(followee));
    }
}
