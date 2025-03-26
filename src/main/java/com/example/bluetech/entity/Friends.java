package com.example.bluetech.entity;

 import com.example.bluetech.constant.Status;
 import com.fasterxml.jackson.annotation.JsonIgnore;
 import com.fasterxml.jackson.annotation.JsonInclude;
 import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
 import org.springframework.data.annotation.Transient;
 import org.springframework.data.mongodb.core.mapping.Document;


@Document("friend")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Friends extends  BaseEntity{
    String user1;
    String user2;



    public static Friends  create (String user1, String user2) {
        Friends friends = new Friends();
        friends.setUser1(user1);
        friends.setUser2(user2);
        friends.setStatus(Status.ACTIVE);

        return friends;
    }
}
