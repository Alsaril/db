package ru.mail.park.response;

public enum ResponseCode {
    OK(0, "OK"),
    NOT_FOUND(1, "NOT FOUND"),
    INVALID_REQUEST(2, "INVALID REQUEST"),
    BAD_REQUEST(3, "BAD REQUEST"),
    UNKNOWN_ERROR(4, "UNKNOWN ERROR"),
    USER_EXISTS(5, "USER EXISTS");

    public final int code;
    public final String text;

    ResponseCode(int code, String text) {
        this.code = code;
        this.text = text;
    }
}
