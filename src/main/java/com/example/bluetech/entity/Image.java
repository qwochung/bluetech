package com.example.bluetech.entity;

import com.example.bluetech.constant.Status;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("image")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Image  extends  BaseEntity {
    String url;
    String type;
    String key;




    public static Image create(String url, String key) {
        Image image = new Image();
        image.setUrl(url);
        image.setKey(key);
        image.setCreatedAt(System.currentTimeMillis());
        image.setStatus(Status.ACTIVE);

        return image;


    }

}
