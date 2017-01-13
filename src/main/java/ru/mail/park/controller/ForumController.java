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
import ru.mail.park.request.ForumCreateRequest;
import ru.mail.park.response.CommonResponse;
import ru.mail.park.response.SimpleResponse;

import java.util.List;


@RestController
@RequestMapping(produces = "application/json; charset=utf-8")
public class ForumController {

    private final ForumDAO forumDAO;
    private final UserDAO userDAO;
    private final PostDAO postDAO;
    private final ThreadDAO threadDAO;

    public ForumController(ForumDAO forumDAO, UserDAO userDAO, PostDAO postDAO, ThreadDAO threadDAO) {
        this.forumDAO = forumDAO;
        this.userDAO = userDAO;
        this.postDAO = postDAO;
        this.threadDAO = threadDAO;
    }

    @RequestMapping(path = "db/api/forum/create", method = RequestMethod.POST)
    public ResponseEntity create(@RequestBody String body) {
        final ForumCreateRequest fcr = Utility.j2o(body, ForumCreateRequest.class);
        if (fcr == null) {
            return SimpleResponse.INVALID_REQUEST.response;
        }
        if (!fcr.isValid()) {
            return SimpleResponse.BAD_REQUEST.response;
        }
        final User user = userDAO.fromEmail(fcr.user);
        if (user == null) {
            return SimpleResponse.NOT_FOUND.response;
        }
        final Forum forum = forumDAO.create(fcr.name, fcr.shortName, user);
        return CommonResponse.OK(forum);
    }

    @RequestMapping(path = "db/api/forum/details", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    public ResponseEntity details(@RequestParam(name = "forum") String name,
                                  @RequestParam(name = "related", required = false) List<String> related) {
        if (StringUtils.isEmpty(name)) {
            return SimpleResponse.BAD_REQUEST.response;
        }
        final Forum<?> forum = forumDAO.fromShortName(name, Utility.contains(related, "user"));
        if (forum == null) {
            return SimpleResponse.NOT_FOUND.response;
        }
        return CommonResponse.OK(forum);
    }

    @RequestMapping(path = "db/api/forum/listPosts", method = RequestMethod.GET)
    public ResponseEntity listPosts(@RequestParam(name = "forum") String forum,
                                    @RequestParam(name = "limit", required = false) String strLimit,
                                    @RequestParam(name = "order", required = false) String order,
                                    @RequestParam(name = "since", required = false) String since,
                                    @RequestParam(name = "related", required = false) List<String> related) {
        if (StringUtils.isEmpty(forum)) {
            return SimpleResponse.BAD_REQUEST.response;
        }

        int limit = -1;
        if (!StringUtils.isEmpty(strLimit)) {
            try {
                limit = Integer.parseInt(strLimit);
            } catch (NumberFormatException e) {
                return SimpleResponse.BAD_REQUEST.response;
            }
        }

        if (StringUtils.isEmpty(order)) {
            order = "desc";
        }
        if (!order.equals("desc") && !order.equals("asc")) {
            return SimpleResponse.BAD_REQUEST.response;
        }

        if (StringUtils.isEmpty(since)) {
            since = null;
        }

        final List<Post<?, ?, ?>> posts = postDAO.forumListPosts(forum, limit, since, order, Utility.contains(related, "user"), Utility.contains(related, "thread"), Utility.contains(related, "forum"));
        if (posts == null) {
            return SimpleResponse.NOT_FOUND.response;
        }
        return CommonResponse.OK(posts);
    }

    @RequestMapping(path = "db/api/forum/listThreads", method = RequestMethod.GET)
    public ResponseEntity listThreads(@RequestParam(name = "forum") String forum,
                                      @RequestParam(name = "limit", required = false) String strLimit,
                                      @RequestParam(name = "order", required = false) String order,
                                      @RequestParam(name = "since", required = false) String since,
                                      @RequestParam(name = "related", required = false) List<String> related) {
        if (StringUtils.isEmpty(forum)) {
            return SimpleResponse.BAD_REQUEST.response;
        }

        int limit = -1;
        if (!StringUtils.isEmpty(strLimit)) {
            try {
                limit = Integer.parseInt(strLimit);
            } catch (NumberFormatException e) {
                return SimpleResponse.BAD_REQUEST.response;
            }
        }

        if (StringUtils.isEmpty(order)) {
            order = "desc";
        }
        if (!order.equals("desc") && !order.equals("asc")) {
            return SimpleResponse.BAD_REQUEST.response;
        }

        if (StringUtils.isEmpty(since)) {
            since = null;
        }

        final List<Thread<?, ?>> threads = threadDAO.forumListThreads(forum, limit, since, order, Utility.contains(related, "user"), Utility.contains(related, "forum"));
        if (threads == null) {
            return SimpleResponse.NOT_FOUND.response;
        }
        return CommonResponse.OK(threads);
    }

    @RequestMapping(path = "db/api/forum/listUsers", method = RequestMethod.GET)
    public ResponseEntity listUsers(@RequestParam(name = "forum") String forum,
                                    @RequestParam(name = "limit", required = false) String strLimit,
                                    @RequestParam(name = "order", required = false) String order,
                                    @RequestParam(name = "since_id", required = false) String since) {
        if (StringUtils.isEmpty(forum)) {
            return SimpleResponse.BAD_REQUEST.response;
        }

        int limit = -1;
        if (!StringUtils.isEmpty(strLimit)) {
            try {
                limit = Integer.parseInt(strLimit);
            } catch (NumberFormatException e) {
                return SimpleResponse.BAD_REQUEST.response;
            }
        }

        if (StringUtils.isEmpty(order)) {
            order = "desc";
        }
        if (!order.equals("desc") && !order.equals("asc")) {
            return SimpleResponse.BAD_REQUEST.response;
        }

        if (StringUtils.isEmpty(since)) {
            since = null;
        }

        final List<User> users = userDAO.forumListUsers(forum, limit, since, order);
        if (users == null) {
            return SimpleResponse.NOT_FOUND.response;
        }
        return CommonResponse.OK(users);
    }
}
