package ru.mail.park.response;

import org.springframework.http.ResponseEntity;
import ru.mail.park.Utility;

public class CommonResponse<T> {
    public final T response;
    public int code;

    CommonResponse(SimpleResponse code, T response) {
        this.code = code.code;
        this.response = response;
    }

    public static <T> ResponseEntity<?> OK(T t) {
        return new CommonResponse<T>(SimpleResponse.OK, t).response();
    }

    public ResponseEntity response() {
        return ResponseEntity.ok(Utility.o2j(this));
    }
}
