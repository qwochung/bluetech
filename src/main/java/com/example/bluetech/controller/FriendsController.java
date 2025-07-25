package com.example.bluetech.controller;

import com.example.bluetech.dto.request.FriendSuggestionRequest;
import com.example.bluetech.dto.respone.FriendSuggestionResponse;
import com.example.bluetech.dto.respone.Response;
import com.example.bluetech.entity.Friends;
import com.example.bluetech.service.FriendsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PostMapping("/suggestions")
    public Response suggestFriends(@RequestBody FriendSuggestionRequest request) {
        List<FriendSuggestionResponse> suggestions = friendsService.suggestFriends(request);
        return Response.builder(suggestions).build();
    }

}
