package ru.mail.park.controller;


import org.springframework.web.bind.annotation.RestController;
import ru.mail.park.DAO.ForumDAO;


@RestController
public class ForumController {

    private ForumDAO forumDAO;

    public ForumController(ForumDAO forumDAO) {
        this.forumDAO = forumDAO;
    }

    /*@RequestMapping(path = "db/api/forum/create", method = RequestMethod.POST)
    public ResponseEntity login(@RequestBody String body) {
        final ForumCreateRequest fcr = Utility.j2o(body, ForumCreateRequest.class);
        if (fcr == null) {
            return new SimpleResponse(ResponseCode.INVALID_REQUEST).response();
        }
        if (!fcr.isValid()) {
            return new SimpleResponse(ResponseCode.BAD_REQUEST).response();
        }
        final Forum f = forumDAO.create(fcr);
        return new CommonResponse(ResponseCode.OK, f).response();
    }*/
}
