package ru.mail.park.controller;

import ru.mail.park.DAO.PostDAO;
import ru.mail.park.DAO.UserDAO;


public class PostController {

    private final PostDAO postDAO;
    private final UserDAO userDAO;

    public PostController(PostDAO postDAO, UserDAO userDAO) {
        this.postDAO = postDAO;
        this.userDAO = userDAO;
    }

    /*@RequestMapping(path = "db/api/post/create", method = RequestMethod.POST)
    public ResponseEntity create(@RequestBody String body) {
        final PostCreateRequest pcr = Utility.j2o(body, PostCreateRequest.class);
        if (pcr == null) {
            return new SimpleResponse(SimpleResponse.INVALID_REQUEST).response();
        }
        if (!pcr.isValid()) {
            return new SimpleResponse(SimpleResponse.BAD_REQUEST).response();
        }
        final User user = userDAO.fromEmail(pcr.user);
        final Thread thread = threadDAO.get(pcr.thread);
        final Forum forum = forumDAO.fromShortName(pcr.forum);
        if (user == null || thread == null || forum == null) {
            return new SimpleResponse(SimpleResponse.NOT_FOUND).response();
        }
        final Thread parent = pcr.parent == null ? null : threadDAO.get(pcr.parent);
        final Post post = postDAO.create(forum,
                thread,
                user,
                pcr.date,
                pcr.message,
                parent,
                pcr.isApproved,
                pcr.isHighlighted,
                pcr.isEdited,
                pcr.isSpam,
                pcr.isDeleted);
        return new CommonResponse<>(SimpleResponse.OK, post).response();
    }*/
}
