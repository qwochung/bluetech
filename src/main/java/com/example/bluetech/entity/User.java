package com.example.bluetech.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
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
     @JsonIgnore
     String password;
     String email;   
     String phone;
     LocalDate dateOfBirth;
     String gender;
     String avatarUrl;

     @DBRef
     @JsonIgnore
     Address address;

     @Transient
     Integer noOfFollow;
     @Transient
     Integer noOfFollowing;


     public void update(User user) {
          if (user.getDateOfBirth() != null) {
               this.dateOfBirth = user.getDateOfBirth();
          }
          if (user.getGender() != null) {
               this.gender = user.getGender();
          }
          if (user.getPhone() != null) {
               this.phone = user.getPhone();
          }
     }
}
