package ru.mail.park.model;

public class Forum<T> {
    public final int id;
    public final String name;
    public final String shortName;
    public final T user;

    public Forum(int id, String name, String shortName, T user) {
        this.id = id;
        this.name = name;
        this.shortName = shortName;
        this.user = user;
    }
}
