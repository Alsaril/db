package ru.mail.park.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import ru.mail.park.DAO.ForumDAO;
import ru.mail.park.DAO.PostDAO;
import ru.mail.park.DAO.ThreadDAO;
import ru.mail.park.DAO.UserDAO;
import ru.mail.park.Utility;
import ru.mail.park.model.Forum;
import ru.mail.park.model.Post;
import ru.mail.park.model.Thread;
import ru.mail.park.model.User;
import ru.mail.park.request.PostCreateRequest;
import ru.mail.park.request.PostUpdateRequest;
import ru.mail.park.response.CommonResponse;
import ru.mail.park.response.SimpleResponse;

import java.util.List;

@RestController
@RequestMapping(produces = "application/json; charset=utf-8")
public class PostController {

    private final PostDAO postDAO;
    private final UserDAO userDAO;
    private final ThreadDAO threadDAO;
    private final ForumDAO forumDAO;

    public PostController(PostDAO postDAO, UserDAO userDAO, ThreadDAO threadDAO, ForumDAO forumDAO) {
        this.postDAO = postDAO;
        this.userDAO = userDAO;
        this.threadDAO = threadDAO;
        this.forumDAO = forumDAO;
    }

    @RequestMapping(path = "db/api/post/create", method = RequestMethod.POST)
    public ResponseEntity create(@RequestBody String body) {
        final PostCreateRequest pcr = Utility.j2o(body, PostCreateRequest.class);
        if (pcr == null) {
            return SimpleResponse.INVALID_REQUEST.response;
        }
        if (!pcr.isValid()) {
            return SimpleResponse.BAD_REQUEST.response;
        }
        final User user = userDAO.fromEmail(pcr.user);
        final Thread thread = threadDAO.get(pcr.thread, false, false);
        final Forum forum = forumDAO.fromShortName(pcr.forum, false);
        if (user == null || thread == null || forum == null) {
            return SimpleResponse.NOT_FOUND.response;
        }
        final Post parent = StringUtils.isEmpty(pcr.parent) ? null : postDAO.get(pcr.parent, false, false, false);
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
        return CommonResponse.OK(post);
    }

    @RequestMapping(path = "db/api/post/details", method = RequestMethod.GET)
    public ResponseEntity details(@RequestParam("post") String strPost, @RequestParam(required = false) List<String> related) {
        if (StringUtils.isEmpty(strPost)) {
            return SimpleResponse.INVALID_REQUEST.response;
        }
        final int postId;
        try {
            postId = Integer.parseInt(strPost);
        } catch (NumberFormatException e) {
            return SimpleResponse.BAD_REQUEST.response;
        }
        final Post<?, ?, ?> post = postDAO.get(postId, Utility.contains(related, "user"), Utility.contains(related, "thread"), Utility.contains(related, "forum"));
        if (post == null) {
            return SimpleResponse.NOT_FOUND.response;
        }
        return CommonResponse.OK(post);
    }

    @RequestMapping(path = "db/api/post/update", method = RequestMethod.POST)
    public ResponseEntity update(@RequestBody String body) {
        final PostUpdateRequest pur = Utility.j2o(body, PostUpdateRequest.class);
        if (pur == null) {
            return SimpleResponse.INVALID_REQUEST.response;
        }
        if (!pur.isValid()) {
            return SimpleResponse.BAD_REQUEST.response;
        }
        final Post<?, ?, ?> post = postDAO.update(pur.post, pur.message);
        if (post == null) {
            return SimpleResponse.NOT_FOUND.response;
        }
        return CommonResponse.OK(post);
    }
}
