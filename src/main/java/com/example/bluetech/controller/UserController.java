package com.example.bluetech.controller;

import com.example.bluetech.dto.Response;
import com.example.bluetech.entity.User;
import com.example.bluetech.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/user")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "", method = RequestMethod.POST)
    public Response createUser(@RequestBody User user, HttpServletRequest request) throws JsonProcessingException {
        User u = userService.add(user, request);
        return Response.builder(u).build();
    }
}
