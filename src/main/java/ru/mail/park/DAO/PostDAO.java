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
                if (parent == null) {
                    pst.setNull(1, Types.INTEGER);
                } else {
                    pst.setInt(1, parent.id);
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
        thread.addPost();

        final int id = keyHolder.getKey().intValue();

        final String query = "UPDATE thread SET posts = posts + 1 WHERE id = ?";
        template.update(query, thread.id);

        final Integer root;
        final String path;

        if (parent == null) {
            root = id;
            path = "";
        } else {
            root = parent.root;
            path = String.format("%s%08d", parent.path, id);
        }
        setPath(id, root, path);
        return new Post<>(id, date, thread, message, user, forum, parent == null ? null : parent.id, root, isApproved, isHighlighted, isEdited, isSpam, isDeleted, 0, 0, 0, path);
    }

    private void setPath(int id, int root, String path) {
        final String query = "UPDATE post SET root_id = ?, path = ? WHERE id = ?";
        template.update(query, root, path, id);
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
            final Integer rootId = (Integer) rs.getObject("root_id");
            final String path = rs.getString("path");
            final boolean isApproved = rs.getBoolean("isApproved");
            final boolean isHighlighted = rs.getBoolean("isHighlighted");
            final boolean isEdited = rs.getBoolean("isEdited");
            final boolean isSpam = rs.getBoolean("isSpam");
            final boolean isDeleted = rs.getBoolean("isDeleted");
            final int likes = rs.getInt("likes");
            final int dislikes = rs.getInt("dislikes");
            final int points = rs.getInt("points");
            final Thread<?, ?> thread = threadDAO.get(threadId, false, false);
            final User user = userDAO.details(userId);
            final Forum<?> forum = forumDAO.get(forumId, false);
            return new Post<>(id, date, includeThread ? thread : thread.id, message, includeUser ? user : user.email, includeForum ? forum : forum.short_name, parentId, rootId, isApproved, isHighlighted, isEdited, isSpam, isDeleted, likes, dislikes, points, path);
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

    public List<Post<?, ?, ?>> userListPosts(String email, int limit, String since, String order) {
        final String source = "SELECT * FROM post p JOIN profile u ON p.user_id = u.id WHERE u.email = ?";
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

    public List<Post<?, ?, ?>> forumListPosts(String forum, int limit, String since, String order, boolean includeUser, boolean includeThread, boolean includeForum) {
        final String source = "SELECT * FROM post p JOIN forum f ON p.forum_id = f.id WHERE f.shortname = ?";
        final StringBuilder query = new StringBuilder(source);
        if (since != null) {
            query.append(" AND p.date >= '").append(since).append('\'');
        }
        query.append(" ORDER BY date ").append(order);
        if (limit != -1) {
            query.append(" LIMIT ").append(limit);
        }
        return template.query(query.toString(), postMapper(includeUser, includeThread, includeForum), forum);
    }

    public Post<?, ?, ?> vote(int id, int vote) {
        final String query;
        if (vote == 1) {
            query = "UPDATE post SET likes = likes + 1, points = points + 1 WHERE id = ?";
        } else {
            query = "UPDATE post SET dislikes = dislikes + 1, points = points - 1 WHERE id = ?";
        }
        if (template.update(query, id) == 0) {
            return null;
        }
        return get(id, false, false, false);
    }

    public boolean remove(int id) {
        final String query = "UPDATE post SET isDeleted = 1 WHERE id = ?";
        if (template.update(query, id) != 0) {
            template.update("UPDATE thread t JOIN post p ON t.id = p.thread_id SET t.posts = t.posts - 1 WHERE p.id = ?", id);
            return true;
        }
        return false;
    }

    public boolean restore(int id) {
        final String query = "UPDATE post SET isDeleted = 0 WHERE id = ?";
        if (template.update(query, id) != 0) {
            template.update("UPDATE thread t JOIN post p ON t.id = p.thread_id SET t.posts = t.posts + 1 WHERE p.id = ?", id);
            return true;
        }
        return false;
    }

    public List<Post<?, ?, ?>> list(String thread, String forum, int limit, String since, String order) {
        final String source = "SELECT * FROM post";
        final StringBuilder query = new StringBuilder(source);
        if (StringUtils.isEmpty(thread)) {
            query.append(" p JOIN  forum f ON f.id = p.forum_id WHERE f.shortname = ?");
        } else {
            query.append(" WHERE thread_id = ?");
        }
        if (since != null) {
            query.append(" AND date >= '").append(since).append('\'');
        }
        query.append(" ORDER BY date ").append(order);
        if (limit != -1) {
            query.append(" LIMIT ").append(limit);
        }
        return template.query(query.toString(), postMapper(false, false, false), StringUtils.isEmpty(thread) ? forum : thread);
    }

    public List<Post<?, ?, ?>> threadListPosts(Thread<?, ?> thread, int limit, String since, String order, String sort) {
        if (sort.equals("flat")) {
            final String source = "SELECT * FROM post WHERE thread_id = ?";
            final StringBuilder query = new StringBuilder(source);
            if (since != null) {
                query.append(" AND date >= '").append(since).append('\'');
            }
            query.append(" ORDER BY date ").append(order);
            if (limit != -1) {
                query.append(" LIMIT ").append(limit);
            }
            return template.query(query.toString(), postMapper(false, false, false), thread.id);
        } else if (sort.equals("tree")) {
            final String source = "SELECT * FROM post WHERE thread_id = ?";
            final StringBuilder query = new StringBuilder(source);
            if (since != null) {
                query.append(" AND date >= '").append(since).append('\'');
            }
            query.append(" ORDER BY root_id ").append(order).append(", path ASC");
            if (limit != -1) {
                query.append(" LIMIT ").append(limit);
            }
            return template.query(query.toString(), postMapper(false, false, false), thread.id);
        } else {
            final String source = "SELECT * FROM post p JOIN (SELECT * FROM post WHERE thread_id = ? AND parent_id IS NULL";
            final StringBuilder query = new StringBuilder(source);
            if (limit != -1) {
                query.append(" LIMIT ").append(limit);
            }
            query.append(") r ON p.root_id = r.id WHERE p.thread_id = ?");
            if (since != null) {
                query.append(" AND p.date >= '").append(since).append('\'');
            }
            query.append(" ORDER BY p.root_id ").append(order).append(", p.path ASC");
            return template.query(query.toString(), postMapper(false, false, false), thread.id, thread.id);
        }
    }
}
