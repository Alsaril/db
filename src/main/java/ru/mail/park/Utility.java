package ru.mail.park;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Utility {

    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
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

    public static <T> boolean contains(List<T> list, T elem) {
        if (list == null) return false;
        for (T t : list) {
            if (t.equals(elem)) {
                return true;
            }
        }
        return false;
    }
}
