package ru.mail.park.model;

public class Forum<T> {
    public final int id;
    public final String name;
    public final String short_name;
    public final T user;

    public Forum(int id, String name, String short_name, T user) {
        this.id = id;
        this.name = name;
        this.short_name = short_name;
        this.user = user;
    }
}
