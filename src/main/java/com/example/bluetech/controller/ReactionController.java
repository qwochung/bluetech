package com.example.bluetech.controller;

import com.example.bluetech.constant.ReactionType;
import com.example.bluetech.dto.Response;
import com.example.bluetech.entity.Reaction;
import com.example.bluetech.service.ReactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/posts/{postId}/reactions")
@RequiredArgsConstructor
public class ReactionController {
    private final ReactionService reactionService;

    @PostMapping
    public Response reactToPost(@PathVariable String postId, @RequestParam ReactionType type, @RequestParam String userId) {
        Reaction reaction = reactionService.reactToPost(postId, userId, type);
        return Response.builder(reaction).build();
    }


}
