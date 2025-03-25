package com.example.bluetech.repository;

import com.example.bluetech.constant.InviteType;
import com.example.bluetech.entity.Invite;
import com.example.bluetech.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InviteRepository extends MongoRepository<Invite, String> {

    List<Invite> findByInviterAndInviteeAndInviteType(User inviter, User invitee, InviteType inviteType);

    List<Invite> findByInviterAndInviteType(User inviter, InviteType inviteType);

    List<Invite> findByInviteeAndInviteType(User invitee, InviteType inviteType);

    List<Invite> findByInvitee(User invitee);
}
