package com.example.bluetech.entity;

import com.example.bluetech.constant.InviteType;
import com.example.bluetech.constant.Status;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("invite")
@JsonInclude(JsonInclude.Include.NON_NULL)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Invite extends BaseEntity {
    @DBRef
    User inviter;
    @DBRef
    User invitee;
    InviteType inviteType;
    String message;



}
