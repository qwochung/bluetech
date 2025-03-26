package com.example.bluetech.service;

import com.example.bluetech.constant.ReactionType;
import com.example.bluetech.entity.Reaction;

import java.util.Map;
import java.util.Optional;

public interface ReactionService {
    Reaction reactToPost(String postId, String userId, ReactionType reactionType);
    Map<ReactionType, Integer> getReactionCounts(String postId);
    Optional<ReactionType> getUserReaction(String postId, String userId);
}
