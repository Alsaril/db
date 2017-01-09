package ru.mail.park.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.mail.park.DAO.CommonDAO;
import ru.mail.park.response.CommonResponse;
import ru.mail.park.response.ResponseCode;
import ru.mail.park.response.SimpleResponse;

@RestController
public class CommonController {

    private final CommonDAO commonDAO;

    public CommonController(CommonDAO commonDAO) {
        this.commonDAO = commonDAO;
    }

    @RequestMapping(path = "db/api/clear", method = RequestMethod.POST)
    public ResponseEntity clear() {
        commonDAO.clear();
        return new SimpleResponse(ResponseCode.OK).response();
    }

    @RequestMapping(path = "db/api/status", method = RequestMethod.GET)
    public ResponseEntity status() {
        return new CommonResponse<>(ResponseCode.OK, commonDAO.status()).response();
    }
}
