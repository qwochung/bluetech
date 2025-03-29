package com.example.bluetech.controller;

import com.example.bluetech.dto.Response;
import com.example.bluetech.entity.Friends;
import com.example.bluetech.service.FriendsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/friend")
public class FriendsController {

    @Autowired
    private FriendsService friendsService;

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Response addFriend(@RequestParam("user1") String user1,
                              @RequestParam("user2") String user2){
        Friends newFriend = friendsService.add(user1, user2);
        return Response.builder(newFriend).build();
    }

    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public Response deleteFriend(@RequestParam("user1") String user1,
                                 @RequestParam("user2") String user2){
        try {
            friendsService.delete(user1,user2);
            return Response.builder("Deleted").build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
