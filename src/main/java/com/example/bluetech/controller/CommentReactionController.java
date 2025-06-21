package com.example.bluetech.controller;

import com.example.bluetech.constant.ReactionType;
import com.example.bluetech.dto.respone.Response;
import com.example.bluetech.entity.CommentReaction;
import com.example.bluetech.service.CommentReactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/comments/{commentId}/reactions")
@RequiredArgsConstructor
public class CommentReactionController {
    private final CommentReactionService commentReactionService;

    @PostMapping
    public Response reactToComment(@PathVariable String commentId,
                                   @RequestParam ReactionType type,
                                   @RequestParam String userId) {
        CommentReaction reaction = commentReactionService.reactToComment(commentId, userId, type);
        if (reaction == null) {
            return Response.builder("Reaction removed").build();
        }
        return Response.builder(reaction).build();
    }

    @GetMapping("/counts")
    public Response getReactionCounts(@PathVariable String commentId) {
        Map<ReactionType, Integer> counts = commentReactionService.getReactionCounts(commentId);
        return Response.builder(counts).build();
    }

    @GetMapping("/user/{userId}")
    public Response getUserReaction(@PathVariable String commentId, @PathVariable String userId) {
        Optional<ReactionType> userReaction = commentReactionService.getUserReaction(commentId, userId);
        return Response.builder(userReaction.orElse(null)).build();
    }
}