package com.example.bluetech.controller;

import com.example.bluetech.constant.ErrorCode;
import com.example.bluetech.dto.Response;
import com.example.bluetech.entity.User;
import com.example.bluetech.exceptions.AppException;
import com.example.bluetech.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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


    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Response getUser(@PathVariable("id") String id)  {
        User user = userService.findById(id).orElseThrow( ()-> new AppException(ErrorCode.NOT_FOUND));
        return Response.builder(user).build();
    }


    @RequestMapping(value = "/{id}/update", method = RequestMethod.PUT)
    public Response updateUser(@PathVariable("id") String id, @RequestBody User user, HttpServletRequest request)   {
        User userUpdate = userService.update(id, user);
        return Response.builder(userUpdate).build();
    }


    @RequestMapping(value = "/username", method = RequestMethod.POST)
    public Response getUserByUsername(@Param("username") String username)  {
        User user = userService.findByUserName(username).orElseThrow( ()-> new AppException(ErrorCode.NOT_FOUND));
        return Response.builder(user).build();
    }

    @RequestMapping(value = "/email", method = RequestMethod.POST)
    public Response getUserByEmail(@Param("email") String email)  {
        User user = userService.findByEmail(email).orElseThrow( ()-> new AppException(ErrorCode.NOT_FOUND));
        return Response.builder(user).build();
    }

    @RequestMapping(value = "/username/exists", method = RequestMethod.POST)
    public Response existsByUsername(@Param("username") String username)  {
        boolean exists = userService.existsByUserName(username);
        return Response.builder(exists).build();
    }

    @RequestMapping(value = "/email/exists", method = RequestMethod.POST)
    public Response existsByEmail(@Param("email") String email)  {
        boolean exists = userService.existsByEmail(email);
        return Response.builder(exists).build();
    }


    @RequestMapping(value = "/{id}/deactivate", method = RequestMethod.POST)
    public Response deactivateUser(@PathVariable("id") String id)  {
        userService.deActivate(id);
        return Response.builder("Deactivated").build();
    }



    @RequestMapping(value = "/{id}/activate", method = RequestMethod.POST)
    public Response activateUser(@PathVariable("id") String id)  {
        userService.activate(id);
        return Response.builder("Activated").build();
    }


    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public Response getAllUser()  {
        List<User> users = userService.findAll();
        return Response.builder(users).build();
    }
}
