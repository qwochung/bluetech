package com.example.bluetech.repository;

import com.example.bluetech.constant.ReactionType;

public interface ReactionCountProjection {
    ReactionType getReactionType();
    int getCount();
}
