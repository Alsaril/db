package ru.mail.park.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.mail.park.DAO.ForumDAO;
import ru.mail.park.DAO.UserDAO;
import ru.mail.park.Utility;
import ru.mail.park.model.Forum;
import ru.mail.park.model.User;
import ru.mail.park.request.ForumCreateRequest;
import ru.mail.park.response.CommonResponse;
import ru.mail.park.response.ResponseCode;
import ru.mail.park.response.SimpleResponse;


@RestController
public class ForumController {

    private ForumDAO forumDAO;
    private UserDAO userDAO;

    public ForumController(ForumDAO forumDAO, UserDAO userDAO) {
        this.forumDAO = forumDAO;
        this.userDAO = userDAO;
    }

    @RequestMapping(path = "db/api/forum/create", method = RequestMethod.POST)
    public ResponseEntity login(@RequestBody String body) {
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
}
