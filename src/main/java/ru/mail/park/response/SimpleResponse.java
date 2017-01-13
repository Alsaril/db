package ru.mail.park.response;

import org.springframework.http.ResponseEntity;

public enum SimpleResponse {
    OK(0, "OK"),
    NOT_FOUND(1, "NOT FOUND"),
    BAD_REQUEST(2, "BAD REQUEST"),
    INVALID_REQUEST(3, "INVALID REQUEST"),
    UNKNOWN_ERROR(4, "UNKNOWN ERROR"),
    USER_EXISTS(5, "USER EXISTS");

    public final int code;
    public final String text;
    public final ResponseEntity<?> response;

    SimpleResponse(int code, String text) {
        this.code = code;
        this.text = text;
        response = new CommonResponse<>(this, text).response();
    }
}
