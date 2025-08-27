package com.pg30.webechannellingspringboot.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = "/user")
public class UserController {

    @RequestMapping(value = "/signin",method = RequestMethod.GET)
    public String userLogin() {
        return "signin";
    }

}
