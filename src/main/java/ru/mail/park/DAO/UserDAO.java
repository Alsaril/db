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
                final String query = "INSERT INTO user (username, about, name, email, isAnonymous) VALUES (?,?,?,?,?);";
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
        final String query = "UPDATE user SET name = ?, about = ? WHERE email = ?;";
        if (template.update(query, name, about, email) == 0) {
            return null;
        }
        return details(email);
    }

    private User fromEmail(String email) {
        final User user;
        try {
            final String userQuery = "SELECT * FROM user WHERE email = ?";
            user = template.queryForObject(userQuery, userMapper, email);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
        return user;
    }

    public ExtendedUser details(String email) {
        return details(fromEmail(email));
    }

    public ExtendedUser details(User user) {
        if (user == null) return null;
        final String followersQuery = "SELECT u.email FROM user u JOIN follow f ON u.id = f.follower_id WHERE followee_id = ?";
        final List<String> followers = template.queryForList(followersQuery, String.class, user.id);
        final String followingQuery = "SELECT u.email FROM user u JOIN follow f ON u.id = f.followee_id WHERE follower_id = ?";
        final List<String> following = template.queryForList(followingQuery, String.class, user.id);
        final String subscrQuery = "SELECT thread_id FROM subscription WHERE user_id = ?";
        final List<String> subscriptions = template.queryForList(subscrQuery, String.class, user.id);
        return new ExtendedUser(user, followers, following, subscriptions);
    }

    public List<ExtendedUser> listFollowing(String email, int limit, int since, String order) {
        final User source = fromEmail(email);
        if (source == null) return null;
        StringBuffer query = new StringBuffer("SELECT u.id, u.username, u.about, u.name, u.email, u.isAnonymous FROM user u JOIN follow f ON u.id = f.followee_id WHERE follower_id = ?");
        if (since != -1) {
            query.append(" and u.id > ").append(since);
        }
        query.append(" ORDER BY 1 ").append(order);
        if (limit != -1) {
            query.append(" LIMIT ").append(limit);
        }
        final List<User> following = template.query(query.toString(), userMapper, source.id);
        return following.stream().map(this::details).collect(Collectors.toList());
    }
}
