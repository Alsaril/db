package ru.mail.park.model;

public class Post<U, T, F> {
    public final int id;
    public final String date;
    public final T thread;
    public final String message;
    public final U user;
    public final F forum;

    public final Integer parent;
    public final boolean isApproved;
    public final boolean isHighlighted;
    public final boolean isEdited;
    public final boolean isSpam;
    public final boolean isDeleted;

    public int likes;
    public int dislikes;
    public int points;

    public Post(int id, String date, T thread, String message, U user, F forum, Integer parent, boolean isApproved, boolean isHighlighted, boolean isEdited, boolean isSpam, boolean isDeleted, int likes, int dislikes, int points) {
        this.id = id;
        this.date = date;
        this.thread = thread;
        this.message = message;
        this.user = user;
        this.forum = forum;
        this.parent = parent;
        this.isApproved = isApproved;
        this.isHighlighted = isHighlighted;
        this.isEdited = isEdited;
        this.isSpam = isSpam;
        this.isDeleted = isDeleted;
        this.likes = likes;
        this.dislikes = dislikes;
        this.points = points;
    }
}
