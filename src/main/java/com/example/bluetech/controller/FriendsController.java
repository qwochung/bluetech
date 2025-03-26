package com.example.bluetech.controller;

import com.example.bluetech.service.FriendsService;
 import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/friend")
public class FriendsController {
    private FriendsService friendsService;



}
