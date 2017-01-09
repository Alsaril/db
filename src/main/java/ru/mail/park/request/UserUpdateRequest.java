package ru.mail.park.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.util.StringUtils;

public class UserUpdateRequest {
    public final String about;
    public final String name;
    public final String email;

    public UserUpdateRequest(@JsonProperty("about") String about,
                             @JsonProperty("name") String name,
                             @JsonProperty("user") String email) {
        this.about = about;
        this.name = name;
        this.email = email;
    }

    public boolean isValid() {
        return !(StringUtils.isEmpty(about) ||
                StringUtils.isEmpty(name) ||
                StringUtils.isEmpty(email));
    }
}
