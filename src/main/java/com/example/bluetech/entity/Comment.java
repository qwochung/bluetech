package com.example.bluetech.entity;

import com.example.bluetech.constant.AccessMode;
import com.example.bluetech.constant.OwnerType;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("comment")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Comment extends BaseEntity {

    @NotBlank(message = "Content cannot be blank")
    @Size(max = 500, message = "Content must be less than 500 characters")
    String textContent;

    OwnerType ownerType;
    @DBRef
    User owner;


    String postId;
    @DBRef
    Image image;

//    @DBRef
    String parentId;

    @Transient
    int noOfReply;

    @Transient
    int noOfReactions;

    AccessMode accessMode = AccessMode.PUBLIC;

    public Comment update(Comment comment) {
        if (comment.getTextContent() != null) {
            this.textContent = comment.getTextContent();
        }
        if (comment.getImage() != null) {
            this.image = comment.getImage();
        }
        this.setUpdatedAt(System.currentTimeMillis());
        return this;
    }
}
