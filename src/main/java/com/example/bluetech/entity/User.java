package com.example.bluetech.entity;


import lombok.Data;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Document("user")
public class User extends BaseEntity {
     String userName;
     String password;
     String email;   
     String phone;
     LocalDate dateOfBirth;
     String gender;
     String avatarUrl;

     @DBRef
     Address address;

     @Transient
     Integer noOfFollow;
     @Transient
     Integer noOfFollowing;

    
}
