package ru.mail.park.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import ru.mail.park.DAO.UserDAO;
import ru.mail.park.Utility;
import ru.mail.park.model.ExtendedUser;
import ru.mail.park.model.User;
import ru.mail.park.request.UserCreateRequest;
import ru.mail.park.request.UserUpdateRequest;
import ru.mail.park.response.CommonResponse;
import ru.mail.park.response.ResponseCode;
import ru.mail.park.response.SimpleResponse;

import java.util.List;

@RestController
public class UserController {

    private UserDAO userDAO;

    public UserController(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @RequestMapping(path = "db/api/user/create", method = RequestMethod.POST)
    public ResponseEntity create(@RequestBody String body) {
        final UserCreateRequest ucr = Utility.j2o(body, UserCreateRequest.class);
        if (ucr == null) {
            return new SimpleResponse(ResponseCode.INVALID_REQUEST).response();
        }
        if (!ucr.isValid()) {
            return new SimpleResponse(ResponseCode.BAD_REQUEST).response();
        }
        final User user = userDAO.create(ucr.username, ucr.about, ucr.name, ucr.email, ucr.isAnonymous);
        if (user == null) {
            return new SimpleResponse(ResponseCode.USER_EXISTS).response();
        }
        return new CommonResponse<>(ResponseCode.OK, user).response();
    }

    @RequestMapping(path = "db/api/user/update", method = RequestMethod.POST)
    public ResponseEntity update(@RequestBody String body) {
        final UserUpdateRequest uur = Utility.j2o(body, UserUpdateRequest.class);
        if (uur == null) {
            return new SimpleResponse(ResponseCode.INVALID_REQUEST).response();
        }
        if (!uur.isValid()) {
            return new SimpleResponse(ResponseCode.BAD_REQUEST).response();
        }
        final ExtendedUser user = userDAO.update(uur.email, uur.name, uur.about);
        if (user == null) {
            return new SimpleResponse(ResponseCode.NOT_FOUND).response();
        }
        return new CommonResponse<>(ResponseCode.OK, user).response();
    }

    @RequestMapping(path = "db/api/user/details", method = RequestMethod.GET)
    public ResponseEntity details(@RequestParam(name = "user") String email) {
        if (StringUtils.isEmpty(email)) {
            return new SimpleResponse(ResponseCode.BAD_REQUEST).response();
        }
        final ExtendedUser user = userDAO.details(email);
        if (user == null) {
            return new SimpleResponse(ResponseCode.NOT_FOUND).response();
        }
        return new CommonResponse<>(ResponseCode.OK, user).response();
    }

    @RequestMapping(path = "db/api/user/listFollowing", method = RequestMethod.GET)
    public ResponseEntity following(@RequestParam(name = "user") String email,
                                    @RequestParam(name = "limit", required = false) String strLimit,
                                    @RequestParam(name = "order", required = false) String order,
                                    @RequestParam(name = "since_id", required = false) String strSince) {
        if (StringUtils.isEmpty(email)) {
            return new SimpleResponse(ResponseCode.BAD_REQUEST).response();
        }

        int limit = -1;
        if (!StringUtils.isEmpty(strLimit)) {
            try {
                limit = Integer.parseInt(strLimit);
            } catch (NumberFormatException e) {
                return new SimpleResponse(ResponseCode.BAD_REQUEST).response();
            }
        }

        if (StringUtils.isEmpty(order)) {
            order = "desc";
        }
        if (!order.equals("desc") && !order.equals("asc")) {
            return new SimpleResponse(ResponseCode.BAD_REQUEST).response();
        }

        int since = -1;
        if (!StringUtils.isEmpty(strSince)) {
            try {
                since = Integer.parseInt(strSince);
            } catch (NumberFormatException e) {
                return new SimpleResponse(ResponseCode.BAD_REQUEST).response();
            }
        }

        final List<ExtendedUser> users = userDAO.listFollowing(email, limit, since, order);

        if (users == null) {
            return new SimpleResponse(ResponseCode.NOT_FOUND).response();
        }
        return new CommonResponse<>(ResponseCode.OK, users).response();
    }

    @RequestMapping(path = "db/api/user/listFollowers", method = RequestMethod.GET)
    public ResponseEntity followers(@RequestParam(name = "user") String email,
                                    @RequestParam(name = "limit", required = false) String strLimit,
                                    @RequestParam(name = "order", required = false) String order,
                                    @RequestParam(name = "since_id", required = false) String strSince) {
        if (StringUtils.isEmpty(email)) {
            return new SimpleResponse(ResponseCode.BAD_REQUEST).response();
        }

        int limit = -1;
        if (!StringUtils.isEmpty(strLimit)) {
            try {
                limit = Integer.parseInt(strLimit);
            } catch (NumberFormatException e) {
                return new SimpleResponse(ResponseCode.BAD_REQUEST).response();
            }
        }

        if (StringUtils.isEmpty(order)) {
            order = "desc";
        }
        if (!order.equals("desc") && !order.equals("asc")) {
            return new SimpleResponse(ResponseCode.BAD_REQUEST).response();
        }

        int since = -1;
        if (!StringUtils.isEmpty(strSince)) {
            try {
                since = Integer.parseInt(strSince);
            } catch (NumberFormatException e) {
                return new SimpleResponse(ResponseCode.BAD_REQUEST).response();
            }
        }

        final List<ExtendedUser> users = userDAO.listFollowers(email, limit, since, order);

        if (users == null) {
            return new SimpleResponse(ResponseCode.NOT_FOUND).response();
        }
        return new CommonResponse<>(ResponseCode.OK, users).response();
    }
}
