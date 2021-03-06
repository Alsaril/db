package ru.mail.park.DAO;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mail.park.model.ExtendedUser;
import ru.mail.park.model.User;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserDAO {
    private static final RowMapper<User> userMapper = (rs, i) -> new User(
            rs.getInt("id"),
            rs.getString("username"),
            rs.getString("about"),
            rs.getString("name"),
            rs.getString("email"),
            rs.getBoolean("isAnonymous"));
    private final JdbcTemplate template;


    public UserDAO(JdbcTemplate template) {
        this.template = template;
    }

    public User create(String username, String about, String name, String email, boolean isAnonymous) {
        final KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            template.update(connection -> {
                final String query = "INSERT INTO profile (username, about, name, email, isAnonymous) VALUES (?,?,?,?,?);";
                final PreparedStatement pst = connection.prepareStatement(query,
                        Statement.RETURN_GENERATED_KEYS);
                pst.setString(1, username);
                pst.setString(2, about);
                pst.setString(3, name);
                pst.setString(4, email);
                pst.setString(5, isAnonymous ? "1" : "0");
                return pst;
            }, keyHolder);
        } catch (DuplicateKeyException e) {
            return null;
        }
        return new User(keyHolder.getKey().intValue(), username, about, name, email, isAnonymous);
    }

    public ExtendedUser update(String email, String name, String about) {
        final String query = "UPDATE profile SET name = ?, about = ? WHERE email = ?;";
        if (template.update(query, name, about, email) == 0) {
            return null;
        }
        return details(email);
    }

    public User fromEmail(String email) {
        final User user;
        try {
            final String userQuery = "SELECT * FROM profile WHERE email = ?";
            user = template.queryForObject(userQuery, userMapper, email);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
        return user;
    }

    public User get(int id) {
        final User user;
        try {
            final String userQuery = "SELECT * FROM profile WHERE id = ?";
            user = template.queryForObject(userQuery, userMapper, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
        return user;
    }

    public ExtendedUser details(String email) {
        return details(fromEmail(email));
    }

    public ExtendedUser details(int id) {
        return details(get(id));
    }

    public ExtendedUser details(User user) {
        if (user == null) return null;
        final String followersQuery = "SELECT u.email FROM profile u JOIN follow f ON u.id = f.follower_id WHERE followee_id = ?";
        final List<String> followers = template.queryForList(followersQuery, String.class, user.id);
        final String followingQuery = "SELECT u.email FROM profile u JOIN follow f ON u.id = f.followee_id WHERE follower_id = ?";
        final List<String> following = template.queryForList(followingQuery, String.class, user.id);
        final String subscrQuery = "SELECT thread_id FROM subscription WHERE user_id = ?";
        final List<Integer> subscriptions = template.queryForList(subscrQuery, Integer.class, user.id);
        return new ExtendedUser(user, followers, following, subscriptions);
    }

    private String updateQuery(String source, int limit, int since, String order) {
        final StringBuilder query = new StringBuilder(source);
        if (since != -1) {
            query.append(" and u.id >= ").append(since);
        }
        query.append(" ORDER BY name ").append(order);
        if (limit != -1) {
            query.append(" LIMIT ").append(limit);
        }
        return query.toString();
    }

    public List<ExtendedUser> listFollowing(String email, int limit, int since, String order) {
        final User source = fromEmail(email);
        if (source == null) return null;
        final String query = updateQuery("SELECT u.id, u.username, u.about, u.name, u.email, u.isAnonymous FROM profile u JOIN follow f ON u.id = f.followee_id WHERE follower_id = ?", limit, since, order);
        final List<User> following = template.query(query, userMapper, source.id);
        return following.stream().map(this::details).collect(Collectors.toList());
    }

    public List<ExtendedUser> listFollowers(String email, int limit, int since, String order) {
        final User source = fromEmail(email);
        if (source == null) return null;
        final String query = updateQuery("SELECT u.id, u.username, u.about, u.name, u.email, u.isAnonymous FROM profile u JOIN follow f ON u.id = f.follower_id WHERE followee_id = ?", limit, since, order);
        final List<User> following = template.query(query, userMapper, source.id);
        return following.stream().map(this::details).collect(Collectors.toList());
    }

    public ExtendedUser follow(String follower, String followee) {
        final User r = fromEmail(follower);
        final User e = fromEmail(followee);
        if (r == null || e == null || followee.equals(follower)) return null; //TODO differ equality

        try {
            final String query = "INSERT INTO follow (follower_id, followee_id) VALUES (?,?)";
            template.update(query, r.id, e.id);
        } catch (DuplicateKeyException ex) {
        }
        return details(r);
    }

    public ExtendedUser unfollow(String follower, String followee) {
        final User r = fromEmail(follower);
        final User e = fromEmail(followee);
        if (r == null || e == null || followee.equals(follower)) return null;

        final String query = "DELETE FROM follow WHERE follower_id = ? AND followee_id = ?;";
        template.update(query, r.id, e.id);
        return details(r);
    }

    public List<User> forumListUsers(String forum, int limit, String since, String order) {
        final String source = "SELECT u.id, u.username, u.about, u.name, u.email, u.isAnonymous FROM profile u JOIN user_forum uf on u.id = uf.user_id JOIN forum f ON f.id = uf.forum_id WHERE f.shortname = ?";
        final StringBuilder query = new StringBuilder(source);
        if (since != null) {
            query.append(" AND u.id >= ").append(since);
        }
        query.append(" ORDER BY u.name ").append(order);
        if (limit != -1) {
            query.append(" LIMIT ").append(limit);
        }
        final List<User> users = template.query(query.toString(), userMapper, forum);
        return users.stream().map(this::details).collect(Collectors.toList());
    }
}
