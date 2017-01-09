package ru.mail.park.response;

import org.springframework.http.ResponseEntity;
import ru.mail.park.Utility;

public class CommonResponse<T> {
    public final T response;
    public int code;

    public CommonResponse(ResponseCode code, T response) {
        this.code = code.code;
        this.response = response;
    }

    public ResponseEntity response() {
        return ResponseEntity.ok(Utility.o2j(this));
    }
}
