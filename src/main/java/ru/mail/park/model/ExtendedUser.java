package ru.mail.park.model;

import java.util.List;

public class ExtendedUser extends User {
    public final List<String> followers;
    public final List<String> following;
    public final List<String> subscriptions;

    public ExtendedUser(User user, List<String> followers, List<String> following, List<String> subscriptions) {
        super(user.id, user.username, user.about, user.name, user.email, user.isAnonymous);
        this.followers = followers;
        this.following = following;
        this.subscriptions = subscriptions;
    }
}
