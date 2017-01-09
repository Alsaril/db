package ru.mail.park.response;

public class SimpleResponse extends CommonResponse<String> {
    public SimpleResponse(ResponseCode code) {
        super(code, code.text);
    }
}
