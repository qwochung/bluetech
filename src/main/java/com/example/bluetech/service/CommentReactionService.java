package com.example.bluetech.service;

import com.example.bluetech.constant.ReactionType;
import com.example.bluetech.entity.CommentReaction;

import java.util.Map;
import java.util.Optional;

public interface CommentReactionService {
    CommentReaction reactToComment(String commentId, String userId, ReactionType reactionType);
    Map<ReactionType, Integer> getReactionCounts(String commentId);
    Optional<ReactionType> getUserReaction(String commentId, String userId);
}