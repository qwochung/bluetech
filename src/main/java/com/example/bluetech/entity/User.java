package com.example.bluetech.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Document("user")
public class User extends BaseEntity   {
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
