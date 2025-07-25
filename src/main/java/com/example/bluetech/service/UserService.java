package com.example.bluetech.service;

import com.example.bluetech.dto.respone.FriendWithMutualInfo;
import com.example.bluetech.entity.Invite;
import com.example.bluetech.entity.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;


public interface UserService {
    User save(User user);
    User add(User user, HttpServletRequest request) throws JsonProcessingException;
    User add(User user);
    Optional<User> findById(String  id);
    Optional<User> findByUserName(String userName);
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByUserName(String userName);
    User update(String id ,  User user);
    User updateAvatar(String id, MultipartFile file);
    void deActivate(String id);
    void activate(String id);
    List<User> findAll();


//    Invite Action
    Invite sendInvite(String userId,  Invite invite);
    Invite revokeInvite(String userId,  String inviteId);
    Invite acceptInvite(String userId,  String inviteId);
    Invite declineInvite(String userId,  String inviteId);
    List<Invite> getPendingInvite(String userId);

    List<User> findFriendByUserId(String id);

    void updateVerificationToken(String token, String id);

//    UserDetailsService userDetailsService();
    List<FriendWithMutualInfo> findFriendsWithMutualInfo(String userId);
    List<User> findMutualFriends(String userId1, String userId2);
}
