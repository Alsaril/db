package ru.mail.park.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.util.StringUtils;

public class UserDetailsRequest {
    public final String email;

    public UserDetailsRequest(@JsonProperty("email") String email) {
        this.email = email;
    }

    public boolean isValid() {
        return !StringUtils.isEmpty(email);
    }
}
