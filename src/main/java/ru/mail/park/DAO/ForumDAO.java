package ru.mail.park.DAO;


import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mail.park.model.Forum;
import ru.mail.park.model.User;

import java.sql.PreparedStatement;
import java.sql.Statement;

@Service
@Transactional
public class ForumDAO {
    private final JdbcTemplate template;
    private final UserDAO userDAO;

    public ForumDAO(JdbcTemplate template, UserDAO userDAO) {
        this.template = template;
        this.userDAO = userDAO;
    }

    public Forum<?> create(String name, String shortName, User user) {
        final KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            template.update(connection -> {
                final String query = "INSERT INTO forum (name, shortname, user_id) VALUES (?,?,?);";
                final PreparedStatement pst = connection.prepareStatement(query,
                        Statement.RETURN_GENERATED_KEYS);
                pst.setString(1, name);
                pst.setString(2, shortName);
                pst.setLong(3, user.id);
                return pst;
            }, keyHolder);
        } catch (DuplicateKeyException e) {
            return fromShortName(shortName, false);
        }
        return new Forum<>(keyHolder.getKey().intValue(), name, shortName, user.email);
    }

    private RowMapper<Forum<?>> forumMapper(boolean includeUser) {
        return (rs, i) -> {
            final int id = rs.getInt("id");
            final String name = rs.getString("name");
            final String email = rs.getString("email");
            final String shortName = rs.getString("shortname");
            if (!includeUser) {
                return new Forum<>(id, name, shortName, email);
            } else {
                return new Forum<>(id, name, shortName, userDAO.details(email));
            }
        };
    }

    public Forum<?> fromShortName(String shortName, boolean includeUser) {
        try {
            return template.queryForObject(
                    "SELECT f.id AS id, f.name AS name, u.email AS email, f.shortname AS shortname FROM forum f JOIN profile u ON u.id = f.user_id WHERE f.shortname = ?",
                    forumMapper(includeUser), shortName);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public Forum<?> get(int id, boolean includeUser) {
        try {
            return template.queryForObject(
                    "SELECT f.id AS id, f.name AS name, u.email AS email, f.shortname AS shortname FROM forum f JOIN profile u ON u.id = f.user_id WHERE f.id = ?",
                    forumMapper(includeUser), id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}
