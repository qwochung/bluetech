package com.example.bluetech.entity;
import com.example.bluetech.constant.ReactionType;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("comment_reaction")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentReaction {
    @Id
    String id;

    String commentId;

    String userId;

    ReactionType reactionType;

    long createdAt;
}