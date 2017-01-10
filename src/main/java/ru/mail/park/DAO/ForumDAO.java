package ru.mail.park.DAO;


import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
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

    public ForumDAO(JdbcTemplate template) {
        this.template = template;
    }


    public Forum<String> create(String name, String shortName, User user) {
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
            return fromShortName(shortName);
        }
        return new Forum<>(keyHolder.getKey().intValue(), name, shortName, user.email);
    }


    public Forum<String> fromShortName(String shortName) {
        try {
            return template.queryForObject(
                    "SELECT f.id AS id, f.name AS name, u.email AS email FROM forum f JOIN user u ON u.id = f.user_id WHERE f.shortname = ?",
                    (rs, i) -> {
                        final int id = rs.getInt("id");
                        final String name = rs.getString("name");
                        final String email = rs.getString("email");
                        return new Forum<>(id, name, shortName, email);
                    }, shortName);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}
