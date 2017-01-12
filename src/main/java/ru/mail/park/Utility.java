package ru.mail.park;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Utility {

    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final ObjectMapper mapper = new ObjectMapper();
    private static Writer w;

    static {
        try {
            w = new FileWriter("/home/igor/log");
        } catch (IOException e) {
            w = null;
        }
    }

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
        if (list == null) return false;
        for (T t : list) {
            if (t.equals(elem)) {
                return true;
            }
        }
        return false;
    }

    public static void write(Object o) {
        try {
            if (w != null) {
                w.write(o2j(o));
                w.write("\n\n\n");
            }
        } catch (IOException e) {

        }
    }
}
