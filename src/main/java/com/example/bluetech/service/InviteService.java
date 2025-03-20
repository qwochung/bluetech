package com.example.bluetech.service;

import com.example.bluetech.constant.InviteType;
import com.example.bluetech.entity.Invite;
import com.example.bluetech.entity.User;

import java.util.List;
import java.util.Optional;

public interface InviteService {
    Invite save(Invite invite);
    Invite add(Invite invite);
    List<Invite> findByInviterAndType(User inviter, InviteType inviteType);
    List<Invite> findByInviteeAndType(User invitee, InviteType inviteType);
    List<Invite> findAll();
    List<Invite> findByInviterInviteeAndInviteType(User inviter, User invitee, InviteType inviteType);
    Optional<Invite> findById(String id);
    List<Invite> findByInvitee(User invitee);
    Invite revokeInvite(Invite invite);
    Invite acceptInvite(Invite invite);
    Invite declineInvite(Invite invite);


}
