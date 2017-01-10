package ru.mail.park.model;

public class Post {
    public final int id;
    public final String date;
    public final int thread;
    public final String message;
    public final String user;
    public final String forum;

    public final Integer parent;
    public final boolean isApproved;
    public final boolean isHighlighted;
    public final boolean isEdited;
    public final boolean isSpam;
    public final boolean isDeleted;

    public Post(int id, String date, int thread, String message, String user, String forum, Integer parent, boolean isApproved, boolean isHighlighted, boolean isEdited, boolean isSpam, boolean isDeleted) {
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
    }
}
