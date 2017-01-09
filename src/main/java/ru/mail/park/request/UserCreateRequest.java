package ru.mail.park.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.util.StringUtils;

public class UserCreateRequest {
    public final String username;
    public final String about;
    public final String name;
    public final String email;
    public final boolean isAnonymous;

    public UserCreateRequest(@JsonProperty("username") String username,
                             @JsonProperty("about") String about,
                             @JsonProperty("name") String name,
                             @JsonProperty("email") String email,
                             @JsonProperty("isAnonymous") boolean isAnonymous) {
        this.username = username;
        this.about = about;
        this.name = name;
        this.email = email;
        this.isAnonymous = isAnonymous;
    }

    public boolean isValid() {
        return !StringUtils.isEmpty(email);
    }
}
