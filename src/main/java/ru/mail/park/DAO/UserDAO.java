package ru.mail.park.DAO;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mail.park.model.ExtendedUser;
import ru.mail.park.model.User;

import java.sql.PreparedStatement;
import java.sql.Statement;

@Service
@Transactional
public class UserDAO {
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

    public ExtendedUser details(String email) {

    }
}
