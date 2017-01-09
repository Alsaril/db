package ru.mail.park.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.util.StringUtils;

public class ForumCreateRequest {
    public final String name;
    public final String shortName;
    public final String user;

    @JsonCreator
    public ForumCreateRequest(@JsonProperty("name") String name,
                              @JsonProperty("short_name") String shortName,
                              @JsonProperty("user") String user) {
        this.name = name;
        this.shortName = shortName;
        this.user = user;
    }

    public boolean isValid() {
        return !(StringUtils.isEmpty(name) || StringUtils.isEmpty(shortName) || StringUtils.isEmpty(user));
    }
}
