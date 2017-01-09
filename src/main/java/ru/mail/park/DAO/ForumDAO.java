package ru.mail.park.DAO;


import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ForumDAO {
    private final JdbcTemplate template;

    public ForumDAO(JdbcTemplate template) {
        this.template = template;
    }

   /* public Forum create(ForumCreateRequest fcr) {
        try {
            template.update("INSERT INTO forum(name, shortname, ) VALUES ")
        }
    }*/
}
