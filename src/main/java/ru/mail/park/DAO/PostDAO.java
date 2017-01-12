package ru.mail.park.DAO;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mail.park.Utility;
import ru.mail.park.model.Forum;
import ru.mail.park.model.Post;
import ru.mail.park.model.Thread;
import ru.mail.park.model.User;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Types;
import java.util.List;

@Service
@Transactional
public class PostDAO {
    private final JdbcTemplate template;
    private final ThreadDAO threadDAO;
    private final UserDAO userDAO;
    private final ForumDAO forumDAO;

    public PostDAO(JdbcTemplate template, ThreadDAO threadDAO, UserDAO userDAO, ForumDAO forumDAO) {
        this.template = template;
        this.threadDAO = threadDAO;
        this.userDAO = userDAO;
        this.forumDAO = forumDAO;
    }

    public Post create(Forum forum, Thread thread, User user, String date, String message, Post parent, boolean isApproved, boolean isHighlighted, boolean isEdited, boolean isSpam, boolean isDeleted) {
        final KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            template.update(connection -> {
                final String query = "INSERT INTO post (parent_id, isApproved, isHighlighted, isEdited, isSpam, isDeleted, date, thread_id, message, user_id, forum_id) VALUES (?,?,?,?,?,?,?,?,?,?,?);";
                final PreparedStatement pst = connection.prepareStatement(query,
                        Statement.RETURN_GENERATED_KEYS);
                if (parent != null) {
                    pst.setInt(1, parent.id);
                } else {
                    pst.setNull(1, Types.INTEGER);
                }
                pst.setBoolean(2, isApproved);
                pst.setBoolean(3, isHighlighted);
                pst.setBoolean(4, isEdited);
                pst.setBoolean(5, isSpam);
                pst.setBoolean(6, isDeleted);
                pst.setString(7, date);
                pst.setInt(8, thread.id);
                pst.setString(9, message);
                pst.setInt(10, user.id);
                pst.setInt(11, forum.id);
                return pst;
            }, keyHolder);
        } catch (DuplicateKeyException e) {
            return null;
        }
        threadDAO.addPost(thread.id);
        thread.addPost();
        return new Post<>(keyHolder.getKey().intValue(), date, thread, message, user, forum, parent == null ? null : parent.id, isApproved, isHighlighted, isEdited, isSpam, isDeleted, 0, 0, 0);
    }

    private RowMapper<Post<?, ?, ?>> postMapper(boolean includeUser, boolean includeThread, boolean includeForum) {
        return (rs, i) -> {
            final int id = rs.getInt("id");
            final String message = rs.getString("message");
            final String date = rs.getTimestamp("date").toLocalDateTime().format(Utility.FORMATTER);
            final int threadId = rs.getInt("thread_id");
            final int userId = rs.getInt("user_id");
            final int forumId = rs.getInt("forum_id");
            final Integer parentId = (Integer) rs.getObject("parent_id");
            final boolean isApproved = rs.getBoolean("isApproved");
            final boolean isHighlighted = rs.getBoolean("isHighlighted");
            final boolean isEdited = rs.getBoolean("isEdited");
            final boolean isSpam = rs.getBoolean("isSpam");
            final boolean isDeleted = rs.getBoolean("isDeleted");
            final int likes = rs.getInt("likes");
            final int dislikes = rs.getInt("dislikes");
            final int points = rs.getInt("points");
            final Thread<?, ?> thread = threadDAO.get(threadId, includeUser, includeForum);
            final User user = userDAO.get(userId);
            final Forum<?> forum = forumDAO.get(forumId, includeUser);
            final Post<?, ?, ?> parent = parentId == null ? null : get(parentId, false, false, false);
            return new Post<>(id, date, includeThread ? thread : thread.id, message, includeUser ? user : user.email, includeForum ? forum : forum.short_name, parent == null ? null : parent.id, isApproved, isHighlighted, isEdited, isSpam, isDeleted, likes, dislikes, points);
        };
    }

    public Post<?, ?, ?> get(int id, boolean includeUser, boolean includeThread, boolean includeForum) {
        try {
            return template.queryForObject(
                    "SELECT * FROM post WHERE id = ?", postMapper(includeUser, includeThread, includeForum), id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public Post<?, ?, ?> update(int id, String message) {
        final String query = "UPDATE post SET message = ? WHERE id = ?";
        if (template.update(query, message, id) == 0) {
            return null;
        }
        return get(id, false, false, false);
    }

    public List<Post<?, ?, ?>> listPosts(String email, int limit, String since, String order) {
        final String source = "SELECT * FROM post p JOIN user u ON p.user_id = u.id WHERE u.email = ?";
        final StringBuilder query = new StringBuilder(source);
        if (since != null) {
            query.append(" AND p.date >= '").append(since).append('\'');
        }
        query.append(" ORDER BY date ").append(order);
        if (limit != -1) {
            query.append(" LIMIT ").append(limit);
        }
        return template.query(query.toString(), postMapper(false, false, false), email);
    }
}
