package ru.mail.park;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class Utility {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static String o2j(Object o) {
        String result = null;
        try {
            result = mapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            result = "error";
        }
        return result;
    }

    public static <T> T j2o(String s, Class<T> clazz) {
        T result = null;
        try {
            result = mapper.readValue(s, clazz);
        } catch (IOException e) {
        }
        return result;
    }
}
