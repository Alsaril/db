package ru.mail.park.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.mail.park.DAO.UserDAO;
import ru.mail.park.Utility;
import ru.mail.park.model.ExtendedUser;
import ru.mail.park.model.User;
import ru.mail.park.request.UserCreateRequest;
import ru.mail.park.request.UserDetailsRequest;
import ru.mail.park.request.UserUpdateRequest;
import ru.mail.park.response.CommonResponse;
import ru.mail.park.response.ResponseCode;
import ru.mail.park.response.SimpleResponse;

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

    @RequestMapping(path = "db/api/user/details", method = RequestMethod.POST)
    public ResponseEntity update(@RequestBody String body) {
        final UserDetailsRequest udr = Utility.j2o(body, UserDetailsRequest.class);
        if (udr == null) {
            return new SimpleResponse(ResponseCode.INVALID_REQUEST).response();
        }
        if (!udr.isValid()) {
            return new SimpleResponse(ResponseCode.BAD_REQUEST).response();
        }
        final ExtendedUser user = userDAO.details(udr.email);
        if (user == null) {
            return new SimpleResponse(ResponseCode.NOT_FOUND).response();
        }
        return new CommonResponse<>(ResponseCode.OK, user).response();
    }
}
