package ru.mail.park.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import ru.mail.park.DAO.PostDAO;
import ru.mail.park.DAO.UserDAO;
import ru.mail.park.Utility;
import ru.mail.park.model.ExtendedUser;
import ru.mail.park.model.Post;
import ru.mail.park.model.User;
import ru.mail.park.request.FollowRequest;
import ru.mail.park.request.UserCreateRequest;
import ru.mail.park.request.UserUpdateRequest;
import ru.mail.park.response.CommonResponse;
import ru.mail.park.response.SimpleResponse;

import java.util.List;

@RestController
public class UserController {

    private final UserDAO userDAO;
    private final PostDAO postDAO;

    public UserController(UserDAO userDAO, PostDAO postDAO) {
        this.userDAO = userDAO;
        this.postDAO = postDAO;
    }

    @RequestMapping(path = "db/api/user/create", method = RequestMethod.POST)
    public ResponseEntity create(@RequestBody String body) {
        final UserCreateRequest ucr = Utility.j2o(body, UserCreateRequest.class);
        if (ucr == null) {
            return SimpleResponse.INVALID_REQUEST.response;
        }
        if (!ucr.isValid()) {
            return SimpleResponse.BAD_REQUEST.response;
        }
        final User user = userDAO.create(ucr.username, ucr.about, ucr.name, ucr.email, ucr.isAnonymous);
        if (user == null) {
            return SimpleResponse.USER_EXISTS.response;
        }
        return CommonResponse.OK(user);
    }

    @RequestMapping(path = "db/api/user/updateProfile", method = RequestMethod.POST)
    public ResponseEntity update(@RequestBody String body) {
        final UserUpdateRequest uur = Utility.j2o(body, UserUpdateRequest.class);
        if (uur == null) {
            return SimpleResponse.INVALID_REQUEST.response;
        }
        if (!uur.isValid()) {
            return SimpleResponse.BAD_REQUEST.response;
        }
        final ExtendedUser user = userDAO.update(uur.email, uur.name, uur.about);
        if (user == null) {
            return SimpleResponse.NOT_FOUND.response;
        }
        return CommonResponse.OK(user);
    }

    @RequestMapping(path = "db/api/user/details", method = RequestMethod.GET)
    public ResponseEntity details(@RequestParam(name = "user") String email) {
        if (StringUtils.isEmpty(email)) {
            return SimpleResponse.BAD_REQUEST.response;
        }
        final ExtendedUser user = userDAO.details(email);
        if (user == null) {
            return SimpleResponse.NOT_FOUND.response;
        }
        return CommonResponse.OK(user);
    }


    @RequestMapping(path = "db/api/user/follow", method = RequestMethod.POST)
    public ResponseEntity follow(@RequestBody String body) {
        return commonFollow(true, body);
    }

    @RequestMapping(path = "db/api/user/unfollow", method = RequestMethod.POST)
    public ResponseEntity unfollow(@RequestBody String body) {
        return commonFollow(false, body);
    }

    @RequestMapping(path = "db/api/user/listFollowing", method = RequestMethod.GET)
    public ResponseEntity following(@RequestParam(name = "user") String email,
                                    @RequestParam(name = "limit", required = false) String limit,
                                    @RequestParam(name = "order", required = false) String order,
                                    @RequestParam(name = "since_id", required = false) String since) {
        return commonFollowList(true, email, limit, order, since);
    }

    @RequestMapping(path = "db/api/user/listFollowers", method = RequestMethod.GET)
    public ResponseEntity followers(@RequestParam(name = "user") String email,
                                    @RequestParam(name = "limit", required = false) String limit,
                                    @RequestParam(name = "order", required = false) String order,
                                    @RequestParam(name = "since_id", required = false) String since) {
        return commonFollowList(false, email, limit, order, since);
    }

    @RequestMapping(path = "db/api/user/listPosts", method = RequestMethod.GET)
    public ResponseEntity lisPosts(@RequestParam(name = "user") String email,
                                   @RequestParam(name = "limit", required = false) String strLimit,
                                   @RequestParam(name = "order", required = false) String order,
                                   @RequestParam(name = "since", required = false) String since) {
        if (StringUtils.isEmpty(email)) {
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

        final List<Post<?, ?, ?>> posts = postDAO.listPosts(email, limit, since, order);
        if (posts == null) {
            return SimpleResponse.NOT_FOUND.response;
        }
        return CommonResponse.OK(posts);
    }

    private ResponseEntity commonFollow(boolean type, String body) {
        final FollowRequest fr = Utility.j2o(body, FollowRequest.class);
        if (fr == null) {
            return SimpleResponse.INVALID_REQUEST.response;
        }
        if (!fr.isValid()) {
            return SimpleResponse.BAD_REQUEST.response;
        }
        final ExtendedUser user = type ? userDAO.follow(fr.follower, fr.followee) : userDAO.unfollow(fr.follower, fr.followee);
        if (user == null) {
            return SimpleResponse.NOT_FOUND.response;
        }
        return CommonResponse.OK(user);
    }

    private ResponseEntity commonFollowList(boolean type, String email, String strLimit, String order, String strSince) {
        if (StringUtils.isEmpty(email)) {
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

        int since = -1;
        if (!StringUtils.isEmpty(strSince)) {
            try {
                since = Integer.parseInt(strSince);
            } catch (NumberFormatException e) {
                return SimpleResponse.BAD_REQUEST.response;
            }
        }

        final List<ExtendedUser> users = type ? userDAO.listFollowing(email, limit, since, order) : userDAO.listFollowers(email, limit, since, order);
        if (users == null) {
            return SimpleResponse.NOT_FOUND.response;
        }
        return CommonResponse.OK(users);
    }
}
