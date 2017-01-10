package ru.mail.park.DAO;

import org.springframework.jdbc.core.JdbcTemplate;

public class PostDAO {
    private final JdbcTemplate template;

    public PostDAO(JdbcTemplate template) {
        this.template = template;
    }

    /*public Post create(Forum forum,
                       Thread thread,
                       User user,
                       String date,
                       String message,
                       Thread parent,
                       boolean isApproved,
                       boolean isHighlighted,
                       boolean isEdited,
                       boolean isSpam,
                       boolean isDeleted) {
        final KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            template.update(connection -> {
                final String query = "INSERT INTO post (parent_id, isApproved, isHighlighted, isEdited, isSpam, isDeleted, date, thread_id, message, user_id, forum_id) VALUES (?,?,?,?,?,?,?,?,?,?,?);";
                final PreparedStatement pst = connection.prepareStatement(query,
                        Statement.RETURN_GENERATED_KEYS);
                if (parent != null) pst.setInt(1, parent.id);
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
        return new Post(keyHolder.getKey().intValue(), date, thread.id, message, user.id, forum.id, parent.id, isApproved, isHighlighted, isEdited, isSpam);
    }*/
}
