package ru.mail.park.DAO;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mail.park.Utility;
import ru.mail.park.model.Forum;
import ru.mail.park.model.Thread;
import ru.mail.park.model.User;

import java.sql.PreparedStatement;
import java.sql.Statement;

@Service
@Transactional
public class ThreadDAO {
    private final JdbcTemplate template;
    private UserDAO userDAO;
    private ForumDAO forumDAO;

    public ThreadDAO(JdbcTemplate template, UserDAO userDAO, ForumDAO forumDAO) {
        this.template = template;
        this.userDAO = userDAO;
        this.forumDAO = forumDAO;
    }

    public Thread<String, String> create(Forum forum, User user, String title, String date, String message, String slug, boolean isClosed, boolean isDeleted) {
        final KeyHolder keyHolder = new GeneratedKeyHolder();
        template.update(connection -> {
            final String query = "INSERT INTO thread (forum_id, user_id, title, date, message, slug, isClosed, isDeleted) VALUES (?,?,?,?,?,?,?,?)";
            final PreparedStatement pst = connection.prepareStatement(query,
                    Statement.RETURN_GENERATED_KEYS);
            pst.setInt(1, forum.id);
            pst.setInt(2, user.id);
            pst.setString(3, title);
            pst.setString(4, date);
            pst.setString(5, message);
            pst.setString(6, slug);
            pst.setBoolean(7, isClosed);
            pst.setBoolean(8, isDeleted);
            return pst;
        }, keyHolder);
        return new Thread<>(keyHolder.getKey().intValue(), forum.name, title, isClosed, user.email, date, message, slug, isDeleted);
    }

    public Thread<?, ?> get(int id, boolean includeUser, boolean includeForum) {
        try {
            return template.queryForObject(
                    "SELECT * FROM thread WHERE id = ?",
                    (rs, i) -> {
                        final String title = rs.getString("title");
                        final String message = rs.getString("message");
                        final String date = rs.getTimestamp("date").toLocalDateTime().format(Utility.FORMATTER);
                        final String slug = rs.getString("slug");
                        final int userId = rs.getInt("user_id");
                        final int forumId = rs.getInt("forum_id");
                        final boolean isClosed = rs.getBoolean("isClosed");
                        final boolean isDeleted = rs.getBoolean("isDeleted");
                        /*final int posts = rs.getInt("posts");
                        final int likes = rs.getInt("likes");
                        final int dislikes = rs.getInt("dislikes");*/
                        final User user = userDAO.get(userId);
                        final Forum forum = forumDAO.get(forumId, false);
                        return new Thread<>(id, includeForum ? forum : forum.short_name, title, isClosed, includeUser ? user : user.email, date, message, slug, isDeleted);
                        /*result.setPosts(posts);
                        result.setLikes(likes);
                        result.setDislikes(dislikes);
                        result.setId(id);*/
                    }, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public Thread<?, ?> update(int id, String message, String slug) {
        final String query = "UPDATE thread SET message = ?, slug = ? WHERE id = ?";
        if (template.update(query, message, slug, id) == 0) {
            return null;
        }
        return get(id, false, false);
    }

    public boolean close(int id) {
        final String query = "UPDATE thread SET isClosed = 1 WHERE id = ?";
        return template.update(query, id) != 0;
    }

    public boolean open(int id) {
        final String query = "UPDATE thread SET isClosed = 0 WHERE id = ?";
        return template.update(query, id) != 0;
    }

    public boolean remove(int id) {
        final String query = "UPDATE thread SET isDeleted = 1 WHERE id = ?";
        return template.update(query, id) != 0; // remove posts
    }

    public boolean restore(int id) {
        final String query = "UPDATE thread SET isDeleted = 0 WHERE id = ?";
        return template.update(query, id) != 0; //restore posts
    }

    public boolean subscibe(String email, int id) { // 3 queries
        final User user = userDAO.fromEmail(email);
        final Thread<?, ?> thread = get(id, false, false);
        if (user == null || thread == null) return false;
        try {
            final String query = "INSERT INTO subscription(user_id, thread_id) VALUES (?, ?)";
            template.update(query, user.id, thread.id);
        } catch (DuplicateKeyException e) {
        }
        return true;
    }

    public boolean unsubscibe(String email, int id) {
        final User user = userDAO.fromEmail(email);
        if (user == null) return false;
        final String query = "DELETE FROM subscription WHERE user_id = ? AND thread_id = ?";
        template.update(query, user.id, id);
        return true;
    }
}
