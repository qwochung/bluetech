package com.example.bluetech.service.imp;

import com.example.bluetech.constant.ErrorCode;
import com.example.bluetech.constant.Status;
import com.example.bluetech.dto.respone.FriendWithMutualInfo;
import com.example.bluetech.entity.*;
import com.example.bluetech.exceptions.AppException;
import com.example.bluetech.repository.ImageRepository;
import com.example.bluetech.repository.UserRepository;
import com.example.bluetech.service.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;


@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AddressService addressService;

    @Autowired
    private InviteService inviteService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private FriendsService friendsService;

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public User add(User user, HttpServletRequest request) throws JsonProcessingException {

        if (user.getUserName() == null || user.getPassword() == null || user.getEmail() == null) {
            throw new AppException(ErrorCode.BAD_REQUEST);
        }
        if (existsByUserName(user.getUserName())) {
            throw new AppException(ErrorCode.USERNAME_EXISTED);
        }
        if (existsByEmail(user.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }

        if (user.getAddress() == null) {
            String ip = getClientIp(request);
            Address address = addressService.addAddressByIp(ip);
            user.setAddress(address);
        }
        Address address = addressService.add(user.getAddress());

        user.setCreatedAt(System.currentTimeMillis());
        user = userRepository.save(user);

        return user;
    }


    @Override
    public User add(User user) {

        if (user.getUserName() == null || user.getPassword() == null || user.getEmail() == null) {
            throw new AppException(ErrorCode.BAD_REQUEST);
        }
        if (existsByUserName(user.getUserName())) {
            throw new AppException(ErrorCode.USERNAME_EXISTED);
        }
        if (existsByEmail(user.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }
        user.setCreatedAt(System.currentTimeMillis());
        user = userRepository.save(user);

        return user;
    }

    @Override
    public Optional<User> findById(String id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> findByUserName(String username) {
        return userRepository.findByUserName(username);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByUserName(String username) {
        return userRepository.existsByUserName(username);
    }

    @Override
    public User update(String id, User user) {
        User userToUpdate = userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
        userToUpdate.update(user);
        return userRepository.save(userToUpdate);
    }

    @Override
    public User updateAvatar(String id, MultipartFile file){
        User userToUpdate = userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
        Image imageToUpdate = imageService.add(file);
        userToUpdate.setAvatarUrl(imageToUpdate.getUrl());
        return userRepository.save(userToUpdate);
    }

    @Override
    public void deActivate(String id) {
        User user = userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
        user.setStatus(Status.INACTIVE);
        userRepository.save(user);
    }

    @Override
    public void activate(String id) {
        User user = userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
        user.setStatus(Status.ACTIVE);
        userRepository.save(user);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }





//    Invite Action
    @Override
    public Invite sendInvite(String userId, Invite invite) {
        if (!userId.equals(invite.getInviter().getId())) {
            throw new AppException(ErrorCode.BAD_REQUEST);
        }
        User inviter = userRepository.findById(invite.getInviter().getId()).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
        User invitee = userRepository.findById(invite.getInvitee().getId()).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));

        inviter.setCreatedAt(System.currentTimeMillis());
        Invite i = inviteService.add(invite);
        return i;
    }

    @Override
    public Invite revokeInvite(String userId, String invite) {
        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
        Invite inv = inviteService.findById(invite).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
        if (!userId.equals(inv.getInviter().getId())) {
            throw new AppException(ErrorCode.BAD_REQUEST);
        }

        inv = inviteService.revokeInvite(inv);
        return inv;
    }

    @Override
    public Invite acceptInvite(String userId, String invite) {
        Invite inv = inviteService.findById(invite).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
        User invitee = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
        User inviter =  userRepository.findById(inv.getInviter().getId()).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));

        if (!userId.equals(inv.getInvitee().getId())) {
            throw new AppException(ErrorCode.BAD_REQUEST);
        }

        inv = inviteService.acceptInvite(inv);
        Friends friends = friendsService.add(inviter.getId(), invitee.getId());

        return inv;
    }


      @Override
    public Invite declineInvite(String userId, String invite) {
        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
        Invite inv = inviteService.findById(invite).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
        if (!userId.equals(inv.getInvitee().getId())) {
            throw new AppException(ErrorCode.BAD_REQUEST);
        }

        inv = inviteService.declineInvite(inv);
        return inv;
    }

    @Override
    public List<Invite> getPendingInvite(String userId) {
        User invitee = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
        List<Invite> invites = inviteService.findByInvitee(invitee);
        return invites;
    }

    @Override
    public List<User> findFriendByUserId(String userId) {
        List<String> friendIds = friendsService.findFriendIdByUserId(userId);
        List<User> friends = userRepository.findAllById(friendIds);
        return friends;
    }


    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        if (ip == null || ip.isEmpty()) {
            throw new AppException(ErrorCode.BAD_REQUEST);
        }
        return ip;
    }

//    public UserDetailsService userDetailsService() {
//        return new UserDetailsService() {
//            @Override
//            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//                return (UserDetails) userRepository.findByEmail(username)
//                        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
//            }
//        };
//    }

    @Override
    public void updateVerificationToken(String token, String id) {
        Optional<User> user = userRepository.findById(id);
        user.ifPresent(u -> {
            u.setVerificationToken(token);
            userRepository.save(u);
        });
    }

    @Override
    public List<FriendWithMutualInfo> findFriendsWithMutualInfo(String userId) {
        List<String> friendIds = friendsService.findFriendIdByUserId(userId);
        if (friendIds.isEmpty()) {
            return new ArrayList<>();
        }

        List<User> friends = userRepository.findAllById(friendIds);
        Map<String, Integer> mutualCounts = friendsService.countMutualFriendsForMultipleUsers(userId, friendIds);
        Map<String, List<String>> mutualFriendsMap = friendsService.findMutualFriendsForMultipleUsers(userId, friendIds);

        Set<String> allMutualFriendIds = mutualFriendsMap.values().stream()
                .flatMap(List::stream)
                .filter(id -> !id.equals(friendIds))
                .collect(Collectors.toSet());

        Map<String, User> mutualFriendUsers;
        if (!allMutualFriendIds.isEmpty()) {
            List<User> mutualUsers = userRepository.findAllById(allMutualFriendIds);
            mutualFriendUsers = mutualUsers.stream()
                    .collect(Collectors.toMap(User::getId, user -> user));
        } else {
            mutualFriendUsers = new HashMap<>();
        }

        return friends.stream()
                .map(friend -> {
                    String friendId = friend.getId();
                    int mutualCount = mutualCounts.getOrDefault(friendId, 0);
                    List<String> mutualIds = mutualFriendsMap.getOrDefault(friendId, new ArrayList<>())
                            .stream()
                            .filter(id -> !id.equals(friendId))
                            .collect(Collectors.toList());
                    List<User> topMutualFriends = mutualIds.stream()
                            .limit(2)
                            .map(mutualFriendUsers::get)
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList());

                    return FriendWithMutualInfo.fromUser(friend, mutualCount, topMutualFriends);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<User> findMutualFriends(String userId1, String userId2) {
        User user1 = userRepository.findById(userId1)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
        User user2 = userRepository.findById(userId2)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
        List<String> mutualFriendIds = friendsService.findMutualFriends(userId1, userId2);
        List<User> mutualFriends = userRepository.findAllById(mutualFriendIds);
        return mutualFriends;
    }


}
