package ru.mail.park;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Utility {

    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final ObjectMapper mapper = new ObjectMapper();

    public static String o2j(Object o) {
        String result;
        try {
            result = mapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            result = "error";
        }
        return result;
    }

    public static <T> T j2o(String s, Class<T> clazz) {
        T result;
        try {
            result = mapper.readValue(s, clazz);
        } catch (IOException e) {
            result = null;
        }
        return result;
    }

    public static <T> boolean contains(List<T> list, T elem) {
        return list != null && list.contains(elem);
    }

    public static boolean check(List<String> list, String... elems) {
        if (list == null || list.isEmpty()) return false;
        final List<String> copy = new ArrayList<>(list);
        copy.removeAll(Arrays.asList(elems));
        return !copy.isEmpty();
    }
}
