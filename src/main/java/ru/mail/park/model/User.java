package ru.mail.park.model;

public class User {
    public final int id;
    public final String username;
    public final String about;
    public final String name;
    public final String email;
    public final boolean isAnonymous;

    public User(int id, String username, String about, String name, String email, boolean isAnonymous) {
        this.id = id;
        this.username = username;
        this.about = about;
        this.name = name;
        this.email = email;
        this.isAnonymous = isAnonymous;
    }
}
