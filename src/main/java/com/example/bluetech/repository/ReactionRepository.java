package com.example.bluetech.repository;

import com.example.bluetech.entity.Reaction;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ReactionRepository extends MongoRepository<Reaction, String> {

    Optional<Reaction> findByPostIdAndUserId(String postId, String userId);


    boolean existsByPostIdAndUserId(String postId, String userId);


    void deleteByPostIdAndUserId(String postId, String userId);

    @Aggregation(pipeline = {
            "{ $match: { postId: ?0 } }",
            "{ $group: { _id: '$reactionType', count: { $sum: 1 } } }",
            "{ $project: { reactionType: '$_id', count: 1, _id: 0 } }"
    })
    List<ReactionCountProjection> countReactionsByPostId(String postId);

    @Query(value = "{'postId': ?0, 'userId': ?1}", fields = "{'reactionType': 1}")
    Optional<Reaction> findReactionTypeByPostIdAndUserId(String postId, String userId);
    long countByPostId(String postId);

}
