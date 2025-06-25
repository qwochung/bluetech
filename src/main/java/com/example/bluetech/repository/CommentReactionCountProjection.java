package com.example.bluetech.repository;

import com.example.bluetech.constant.ReactionType;

public interface CommentReactionCountProjection {
    ReactionType getReactionType();
    int getCount();
}