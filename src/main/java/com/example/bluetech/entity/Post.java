package com.example.bluetech.entity;

import com.example.bluetech.constant.AccessMode;
import com.example.bluetech.constant.OwnerType;
import com.example.bluetech.constant.ReactionType;
import com.example.bluetech.constant.Status;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@Document("post")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Post extends BaseEntity {

    String textContent;
    String ownerId;
    OwnerType ownerType;
    AccessMode accessMode = AccessMode.PUBLIC;


    @DBRef
    List<HashTag> hashTags;
    @DBRef
    List<Image> image;

    @Transient
    int noOfReactions;
    @Transient
    int noOfComments;
    @Transient
    int noOfViews;

    public Post update(Post post) {
        if (post.getTextContent() != null) {
            this.textContent = post.getTextContent();
        }
        if (post.getAccessMode() != null) {
            this.accessMode = post.getAccessMode();
        }
        if (post.getHashTags() != null) {
            this.hashTags = post.getHashTags();
        }
        if (post.getImage() != null) {
            this.image = post.getImage();
        }
        this.setUpdatedAt(System.currentTimeMillis());

        return this;
    }


}
