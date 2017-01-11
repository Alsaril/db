package ru.mail.park.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import ru.mail.park.DAO.ForumDAO;
import ru.mail.park.DAO.ThreadDAO;
import ru.mail.park.DAO.UserDAO;
import ru.mail.park.Utility;
import ru.mail.park.model.Forum;
import ru.mail.park.model.Thread;
import ru.mail.park.model.User;
import ru.mail.park.request.ThreadCreateRequest;
import ru.mail.park.response.CommonResponse;
import ru.mail.park.response.ResponseCode;
import ru.mail.park.response.SimpleResponse;

import java.util.List;

@RestController
@RequestMapping(produces = "application/json; charset=utf-8")
public class ThreadController {
    private ThreadDAO threadDAO;
    private ForumDAO forumDAO;
    private UserDAO userDAO;

    public ThreadController(ThreadDAO threadDAO, ForumDAO forumDAO, UserDAO userDAO) {
        this.threadDAO = threadDAO;
        this.forumDAO = forumDAO;
        this.userDAO = userDAO;
    }

    @RequestMapping(path = "db/api/thread/create", method = RequestMethod.POST)
    public ResponseEntity create(@RequestBody String body) {
        final ThreadCreateRequest tcr = Utility.j2o(body, ThreadCreateRequest.class);
        if (tcr == null) {
            return new SimpleResponse(ResponseCode.INVALID_REQUEST).response();
        }
        if (!tcr.isValid()) {
            return new SimpleResponse(ResponseCode.BAD_REQUEST).response();
        }
        final Forum forum = forumDAO.fromShortName(tcr.forum, false);
        final User user = userDAO.fromEmail(tcr.user);
        if (forum == null || user == null) {
            return new SimpleResponse(ResponseCode.NOT_FOUND).response();
        }
        final Thread thread = threadDAO.create(forum, user, tcr.title, tcr.date, tcr.message, tcr.slug, tcr.isClosed, tcr.isDeleted);
        return new CommonResponse<>(ResponseCode.OK, thread).response();
    }

    @RequestMapping(path = "db/api/thread/details", method = RequestMethod.GET)
    public ResponseEntity details(@RequestParam("thread") String strThread, @RequestParam(required = false) List<String> related) {
        if (StringUtils.isEmpty(strThread)) {
            return new SimpleResponse(ResponseCode.INVALID_REQUEST).response();
        }
        final int threadId;
        try {
            threadId = Integer.parseInt(strThread);
        } catch (NumberFormatException e) {
            return new SimpleResponse(ResponseCode.BAD_REQUEST).response();
        }
        final Thread thread = threadDAO.get(threadId, Utility.contains(related, "user"), Utility.contains(related, "forum"));
        if (thread == null) {
            return new SimpleResponse(ResponseCode.NOT_FOUND).response();
        }
        return new CommonResponse<>(ResponseCode.OK, thread).response();
    }
}
