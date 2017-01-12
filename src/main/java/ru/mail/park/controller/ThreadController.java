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
import ru.mail.park.request.*;
import ru.mail.park.response.CommonResponse;
import ru.mail.park.response.SimpleResponse;

import java.util.List;

@RestController
@RequestMapping(produces = "application/json; charset=utf-8")
public class ThreadController {
    private final ThreadDAO threadDAO;
    private final ForumDAO forumDAO;
    private final UserDAO userDAO;

    public ThreadController(ThreadDAO threadDAO, ForumDAO forumDAO, UserDAO userDAO) {
        this.threadDAO = threadDAO;
        this.forumDAO = forumDAO;
        this.userDAO = userDAO;
    }

    @RequestMapping(path = "db/api/thread/create", method = RequestMethod.POST)
    public ResponseEntity create(@RequestBody String body) {
        final ThreadCreateRequest tcr = Utility.j2o(body, ThreadCreateRequest.class);
        if (tcr == null) {
            return SimpleResponse.INVALID_REQUEST.response;
        }
        if (!tcr.isValid()) {
            return SimpleResponse.BAD_REQUEST.response;
        }
        final Forum forum = forumDAO.fromShortName(tcr.forum, false);
        final User user = userDAO.fromEmail(tcr.user);
        if (forum == null || user == null) {
            return SimpleResponse.NOT_FOUND.response;
        }
        final Thread<String, String> thread = threadDAO.create(forum, user, tcr.title, tcr.date, tcr.message, tcr.slug, tcr.isClosed, tcr.isDeleted);
        return CommonResponse.OK(thread);
    }

    @RequestMapping(path = "db/api/thread/details", method = RequestMethod.GET)
    public ResponseEntity details(@RequestParam("thread") String strThread, @RequestParam(required = false) List<String> related) {
        if (StringUtils.isEmpty(strThread)) {
            return SimpleResponse.INVALID_REQUEST.response;
        }
        final int threadId;
        try {
            threadId = Integer.parseInt(strThread);
        } catch (NumberFormatException e) {
            return SimpleResponse.BAD_REQUEST.response;
        }
        final Thread<?, ?> thread = threadDAO.get(threadId, Utility.contains(related, "user"), Utility.contains(related, "forum"));
        if (thread == null) {
            return SimpleResponse.NOT_FOUND.response;
        }
        return CommonResponse.OK(thread);
    }

    @RequestMapping(path = "db/api/thread/update", method = RequestMethod.POST)
    public ResponseEntity update(@RequestBody String body) {
        final ThreadUpdateRequest tur = Utility.j2o(body, ThreadUpdateRequest.class);
        if (tur == null) {
            return SimpleResponse.INVALID_REQUEST.response;
        }
        if (!tur.isValid()) {
            return SimpleResponse.BAD_REQUEST.response;
        }
        final Thread<?, ?> thread = threadDAO.update(tur.thread, tur.message, tur.slug);
        if (thread == null) {
            return SimpleResponse.NOT_FOUND.response;
        }
        return CommonResponse.OK(thread);
    }

    @RequestMapping(path = "db/api/thread/close", method = RequestMethod.POST)
    public ResponseEntity close(@RequestBody String body) {
        final ThreadStatusRequest tsr = Utility.j2o(body, ThreadStatusRequest.class);
        if (tsr == null) {
            return SimpleResponse.INVALID_REQUEST.response;
        }
        if (!threadDAO.close(tsr.thread)) {
            return SimpleResponse.NOT_FOUND.response;
        }
        return CommonResponse.OK(tsr);
    }

    @RequestMapping(path = "db/api/thread/open", method = RequestMethod.POST)
    public ResponseEntity open(@RequestBody String body) {
        final ThreadStatusRequest tsr = Utility.j2o(body, ThreadStatusRequest.class);
        if (tsr == null) {
            return SimpleResponse.INVALID_REQUEST.response;
        }
        if (!threadDAO.open(tsr.thread)) {
            return SimpleResponse.NOT_FOUND.response;
        }
        return CommonResponse.OK(tsr);
    }

    @RequestMapping(path = "db/api/thread/remove", method = RequestMethod.POST)
    public ResponseEntity remove(@RequestBody String body) {
        final ThreadStatusRequest tsr = Utility.j2o(body, ThreadStatusRequest.class);
        if (tsr == null) {
            return SimpleResponse.INVALID_REQUEST.response;
        }
        if (!threadDAO.remove(tsr.thread)) {
            return SimpleResponse.NOT_FOUND.response;
        }
        return CommonResponse.OK(tsr);
    }

    @RequestMapping(path = "db/api/thread/restore", method = RequestMethod.POST)
    public ResponseEntity restore(@RequestBody String body) {
        final ThreadStatusRequest tsr = Utility.j2o(body, ThreadStatusRequest.class);
        if (tsr == null) {
            return SimpleResponse.INVALID_REQUEST.response;
        }
        if (!threadDAO.restore(tsr.thread)) {
            return SimpleResponse.NOT_FOUND.response;
        }
        return CommonResponse.OK(tsr);
    }

    @RequestMapping(path = "db/api/thread/subscribe", method = RequestMethod.POST)
    public ResponseEntity subscribe(@RequestBody String body) {
        final ThreadSubscribeRequest tsr = Utility.j2o(body, ThreadSubscribeRequest.class);
        if (tsr == null) {
            return SimpleResponse.INVALID_REQUEST.response;
        }
        if (!tsr.isValid()) {
            return SimpleResponse.BAD_REQUEST.response;
        }
        if (!threadDAO.subscibe(tsr.user, tsr.thread)) {
            return SimpleResponse.NOT_FOUND.response;
        }
        return CommonResponse.OK(tsr);
    }

    @RequestMapping(path = "db/api/thread/unsubscribe", method = RequestMethod.POST)
    public ResponseEntity unsubscribe(@RequestBody String body) {
        final ThreadSubscribeRequest tsr = Utility.j2o(body, ThreadSubscribeRequest.class);
        if (tsr == null) {
            return SimpleResponse.INVALID_REQUEST.response;
        }
        if (!tsr.isValid()) {
            return SimpleResponse.BAD_REQUEST.response;
        }
        if (!threadDAO.unsubscibe(tsr.user, tsr.thread)) {
            return SimpleResponse.NOT_FOUND.response;
        }
        return CommonResponse.OK(tsr);
    }

    @RequestMapping(path = "db/api/thread/vote", method = RequestMethod.POST)
    public ResponseEntity vote(@RequestBody String body) {
        final ThreadVoteRequest tvr = Utility.j2o(body, ThreadVoteRequest.class);
        if (tvr == null) {
            return SimpleResponse.INVALID_REQUEST.response;
        }
        if (!tvr.isValid()) {
            return SimpleResponse.BAD_REQUEST.response;
        }
        final Thread<?, ?> thread = threadDAO.vote(tvr.thread, tvr.vote);
        if (thread == null) {
            return SimpleResponse.NOT_FOUND.response;
        }
        return CommonResponse.OK(thread);
    }

    @RequestMapping(path = "db/api/thread/list", method = RequestMethod.GET)
    public ResponseEntity list(@RequestParam(name = "user", required = false) String email,
                               @RequestParam(name = "forum", required = false) String forum,
                               @RequestParam(name = "limit", required = false) String strLimit,
                               @RequestParam(name = "order", required = false) String order,
                               @RequestParam(name = "since", required = false) String since) {
        if (StringUtils.isEmpty(email) && StringUtils.isEmpty(forum)) {
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

        final List<Thread<?, ?>> threads = threadDAO.listThreads(email, forum, limit, since, order);
        if (threads == null) {
            return SimpleResponse.NOT_FOUND.response;
        }
        return CommonResponse.OK(threads);
    }
}
