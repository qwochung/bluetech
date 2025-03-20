package com.example.bluetech.service.imp;

import com.example.bluetech.constant.ErrorCode;
import com.example.bluetech.constant.InviteType;
import com.example.bluetech.constant.Status;
import com.example.bluetech.entity.Invite;
import com.example.bluetech.entity.User;
import com.example.bluetech.exceptions.AppException;
import com.example.bluetech.repository.InviteRepository;
import com.example.bluetech.service.InviteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InviteServiceImpl implements InviteService {

    @Autowired
    private InviteRepository inviteRepository;

    @Override
    public Invite save(Invite invite) {
        return inviteRepository.save(invite);
    }

    @Override
    public Invite add(Invite invite) {
        if (invite.getInvitee() == null || invite.getInviter() ==null || invite.getInviteType() == null) {
            throw new AppException(ErrorCode.BAD_REQUEST);
        }

        List<Invite> invitedList = findByInviterInviteeAndInviteType(
                invite.getInviter(), invite.getInvitee(), invite.getInviteType());

        invitedList.forEach(i -> {
            if (i.getInviteType() == invite.getInviteType() &&
                    i.getStatus().equals(Status.PENDING) || i.getStatus().equals(Status.ACCEPTED)) {
                throw new AppException(ErrorCode.BAD_REQUEST);
            }
        });

        invite.setStatus(Status.PENDING);

        return inviteRepository.save(invite);
    }

    @Override
    public List<Invite> findByInviterAndType(User inviter, InviteType inviteType) {
        List<Invite> invites = inviteRepository.findByInviterAndInviteType(inviter, inviteType);
        return invites;
    }

    @Override
    public List<Invite> findByInviteeAndType(User invitee, InviteType inviteType) {
        List<Invite> invites = inviteRepository.findByInviteeAndInviteType(invitee, inviteType);
        return invites;
    }

    @Override
    public List<Invite> findAll() {
        return inviteRepository.findAll();
    }

    @Override
    public List<Invite> findByInviterInviteeAndInviteType(User inviter, User invitee, InviteType inviteType) {
        List<Invite> invites = inviteRepository.findByInviterAndInviteeAndInviteType(inviter, invitee, inviteType);
        return invites;
    }

    @Override
    public Optional<Invite> findById(String id) {
        return inviteRepository.findById(id);
    }

    @Override
    public List<Invite> findByInvitee(User invitee) {
        List<Invite> invites = inviteRepository.findByInvitee(invitee);
        return invites;
    }

    @Override
    public Invite revokeInvite(Invite invite) {
//        NOTE: Need check user id == inviter
         if ( !invite.getStatus().equals(Status.PENDING)) {
            throw new AppException(ErrorCode.BAD_REQUEST);
        }
        invite.setStatus(Status.REVOKE);
        invite.setUpdatedAt(System.currentTimeMillis());

        invite = inviteRepository.save(invite);
        return invite;
    }

    @Override
    public Invite acceptInvite(Invite invite) {
        // NOTE: Need check user id == invitee
        if ( !invite.getStatus().equals(Status.PENDING)) {
            throw new AppException(ErrorCode.BAD_REQUEST);
        }
        invite.setStatus(Status.ACCEPTED);
        invite.setUpdatedAt(System.currentTimeMillis());

        invite = inviteRepository.save(invite);
        return invite;
    }

    @Override
    public Invite declineInvite(Invite invite) {
        // NOTE: Need check user id == invitee
        if ( !invite.getStatus().equals(Status.PENDING)) {
            throw new AppException(ErrorCode.BAD_REQUEST);
        }
        invite.setStatus(Status.DECLINED);
        invite.setUpdatedAt(System.currentTimeMillis());

        invite = inviteRepository.save(invite);
        return invite;
    }
}
