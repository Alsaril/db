package ru.mail.park.DAO;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import ru.mail.park.Utility;
import ru.mail.park.model.Forum;
import ru.mail.park.model.Thread;
import ru.mail.park.model.User;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Service
@Transactional
public class ThreadDAO {
    private final JdbcTemplate template;
    private final UserDAO userDAO;
    private final ForumDAO forumDAO;

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
        return new Thread<>(keyHolder.getKey().intValue(), forum.name, title, isClosed, user.email, date, message, slug, isDeleted, 0, 0, 0, 0);
    }

    private RowMapper<Thread<?, ?>> threadMapper(boolean includeUser, boolean includeForum) {
        return (rs, i) -> {
            final int id = rs.getInt("id");
            final String title = rs.getString("title");
            final String message = rs.getString("message");
            final String date = rs.getTimestamp("date").toLocalDateTime().format(Utility.FORMATTER);
            final String slug = rs.getString("slug");
            final int userId = rs.getInt("user_id");
            final int forumId = rs.getInt("forum_id");
            final boolean isClosed = rs.getBoolean("isClosed");
            final boolean isDeleted = rs.getBoolean("isDeleted");
            final int likes = rs.getInt("likes");
            final int dislikes = rs.getInt("dislikes");
            final int points = rs.getInt("points");
            final int posts = rs.getInt("posts");
            final User user = userDAO.get(userId);
            final Forum<?> forum = forumDAO.get(forumId, includeUser);
            return new Thread<>(id, includeForum ? forum : forum.short_name, title, isClosed, includeUser ? user : user.email, date, message, slug, isDeleted, likes, dislikes, points, posts);
        };
    }

    public Thread<?, ?> get(int id, boolean includeUser, boolean includeForum) {
        try {
            return template.queryForObject(
                    "SELECT * FROM thread WHERE id = ?", threadMapper(includeUser, includeForum), id);
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
        final String query = "UPDATE thread SET isDeleted = 1, posts = 0 WHERE id = ?";
        if (template.update(query, id) == 0) return false;
        template.update("UPDATE post SET isDeleted = 1 WHERE thread_id = ?", id);
        return true;
    }

    public boolean restore(int id) {
        final int count = template.update("UPDATE post SET isDeleted = 0 WHERE thread_id = ?", id);
        final String query = "UPDATE thread SET isDeleted = 0, posts = ? WHERE id = ?";
        return template.update(query, count, id) != 0;
    }

    public boolean subscibe(String email, int id) {
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

    public Thread<?, ?> vote(int id, int vote) {
        final String query;
        if (vote == 1) {
            query = "UPDATE thread SET likes = likes + 1, points = points + 1 WHERE id = ?";
        } else {
            query = "UPDATE thread SET dislikes = dislikes + 1, points = points - 1 WHERE id = ?";
        }
        if (template.update(query, id) == 0) {
            return null;
        }
        return get(id, false, false);
    }

    public List<Thread<?, ?>> listThreads(String email, String forum, int limit, String since, String order) {
        final String source = "SELECT * FROM thread t JOIN ";
        final StringBuilder query = new StringBuilder(source);
        if (StringUtils.isEmpty(email)) {
            query.append(" forum f ON f.id = t.forum_id WHERE f.shortname = ?");
        } else {
            query.append(" user u ON u.id = t.user_id WHERE u.email = ?");
        }
        if (since != null) {
            query.append(" AND t.date >= '").append(since).append('\'');
        }
        query.append(" ORDER BY date ").append(order);
        if (limit != -1) {
            query.append(" LIMIT ").append(limit);
        }
        return template.query(query.toString(), threadMapper(false, false), StringUtils.isEmpty(email) ? forum : email);
    }

    public void addPost(int id) {
        final String query = "UPDATE thread SET posts = posts + 1 WHERE id = ?";
        template.update(query, id);
    }
}
