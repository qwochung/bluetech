package com.example.bluetech.repository;

import com.example.bluetech.entity.CommentReaction;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CommentReactionRepository extends MongoRepository<CommentReaction, String> {

    Optional<CommentReaction> findByCommentIdAndUserId(String commentId, String userId);

    boolean existsByCommentIdAndUserId(String commentId, String userId);

    void deleteByCommentIdAndUserId(String commentId, String userId);

    @Aggregation(pipeline = {
            "{ $match: { commentId: ?0 } }",
            "{ $group: { _id: '$reactionType', count: { $sum: 1 } } }",
            "{ $project: { reactionType: '$_id', count: 1, _id: 0 } }"
    })
    List<CommentReactionCountProjection> countReactionsByCommentId(String commentId);

    @Query(value = "{'commentId': ?0, 'userId': ?1}", fields = "{'reactionType': 1}")
    Optional<CommentReaction> findReactionTypeByCommentIdAndUserId(String commentId, String userId);

    long countByCommentId(String commentId);

    List<CommentReaction> findByCommentId(String commentId);
}