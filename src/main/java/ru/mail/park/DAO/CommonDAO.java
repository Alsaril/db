package ru.mail.park.DAO;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;

@Service
@Transactional
public class CommonDAO {
    private static final String[] ALL_TABLES = {"forum", "thread", "post", "user", "follow", "thread_vote", "post_vote"};
    private static final String[] DATA_TABLES = {"forum", "thread", "post", "user"};
    private final JdbcTemplate template;

    public CommonDAO(JdbcTemplate template) {
        this.template = template;
    }

    public void clear() {
        template.execute("SET FOREIGN_KEY_CHECKS = 0");
        for (String s : ALL_TABLES) {
            template.execute("TRUNCATE TABLE " + s);
        }
        template.execute("SET FOREIGN_KEY_CHECKS = 1");
    }

    public HashMap<String, Integer> status() {
        HashMap<String, Integer> result = new HashMap<>();
        for (String s : DATA_TABLES) {
            result.put(s, template.queryForObject("SELECT COUNT(*) FROM " + s, Integer.class));
        }
        return result;
    }
}
