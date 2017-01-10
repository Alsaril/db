package ru.mail.park.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import ru.mail.park.DAO.ForumDAO;
import ru.mail.park.DAO.UserDAO;
import ru.mail.park.Utility;
import ru.mail.park.model.Forum;
import ru.mail.park.model.User;
import ru.mail.park.request.ForumCreateRequest;
import ru.mail.park.response.CommonResponse;
import ru.mail.park.response.ResponseCode;
import ru.mail.park.response.SimpleResponse;

import java.util.List;


@RestController
public class ForumController {

    private ForumDAO forumDAO;
    private UserDAO userDAO;

    public ForumController(ForumDAO forumDAO, UserDAO userDAO) {
        this.forumDAO = forumDAO;
        this.userDAO = userDAO;
    }

    @RequestMapping(path = "db/api/forum/create", method = RequestMethod.POST)
    public ResponseEntity create(@RequestBody String body) {
        final ForumCreateRequest fcr = Utility.j2o(body, ForumCreateRequest.class);
        if (fcr == null) {
            return new SimpleResponse(ResponseCode.INVALID_REQUEST).response();
        }
        if (!fcr.isValid()) {
            return new SimpleResponse(ResponseCode.BAD_REQUEST).response();
        }
        final User user = userDAO.fromEmail(fcr.user);
        if (user == null) {
            return new SimpleResponse(ResponseCode.NOT_FOUND).response();
        }
        final Forum forum = forumDAO.create(fcr.name, fcr.shortName, user);
        return new CommonResponse<>(ResponseCode.OK, forum).response();
    }

    @RequestMapping(path = "db/api/forum/details", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    public ResponseEntity details(@RequestParam(name = "forum") String name,
                                  @RequestParam(name = "related", required = false) List<String> related) {
        if (StringUtils.isEmpty(name)) {
            return new SimpleResponse(ResponseCode.BAD_REQUEST).response();
        }
        final Forum<?> forum = forumDAO.fromShortName(name, Utility.contains(related, "user"));
        if (forum == null) {
            return new SimpleResponse(ResponseCode.NOT_FOUND).response();
        }
        return new CommonResponse<>(ResponseCode.OK, forum).response();
    }
}
