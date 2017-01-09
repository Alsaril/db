package ru.mail.park.model;

import java.util.List;

public class ExtendedUser extends User {
    public final List<String> followers;
    public final List<String> following;
    public final List<String> subscriptions;

    public ExtendedUser(int id, String username, String about, String name, String email, boolean isAnonymous, List<String> followers, List<String> following, List<String> subscriptions) {
        super(id, username, about, name, email, isAnonymous);
        this.followers = followers;
        this.following = following;
        this.subscriptions = subscriptions;
    }
}
