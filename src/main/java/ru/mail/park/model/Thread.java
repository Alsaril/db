package ru.mail.park.model;

public class Thread<U, F> {
    public final int id;
    public final F forum;
    public final String title;
    public final boolean isClosed;
    public final U user;
    public final String date;
    public final String message;
    public final String slug;
    public final boolean isDeleted;
    public final int likes;
    public final int dislikes;
    public final int points;
    public int posts;

    public Thread(int id, F forum, String title, boolean isClosed, U user, String date, String message, String slug, boolean isDeleted, int likes, int dislikes, int points, int posts) {
        this.id = id;
        this.forum = forum;
        this.title = title;
        this.isClosed = isClosed;
        this.user = user;
        this.date = date;
        this.message = message;
        this.slug = slug;
        this.isDeleted = isDeleted;
        this.likes = likes;
        this.dislikes = dislikes;
        this.points = points;
        this.posts = posts;
    }

    public void addPost() {
        posts++;
    }
}
