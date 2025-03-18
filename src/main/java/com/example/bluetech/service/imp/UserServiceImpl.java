package com.example.bluetech.service.imp;

import com.example.bluetech.constant.ErrorCode;
import com.example.bluetech.constant.Status;
import com.example.bluetech.entity.Address;
import com.example.bluetech.entity.User;
import com.example.bluetech.exceptions.AppException;
import com.example.bluetech.repository.UserRepository;
import com.example.bluetech.service.AddressService;
import com.example.bluetech.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AddressService addressService;

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
            String ip = getClientId(request);
            Address address = addressService.addAddressByIp(ip);
            user.setAddress(address);
        }

        Address address= addressService.save(user.getAddress());
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




    private String getClientId(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        if (ip == null || ip.isEmpty()) {
            throw new AppException(ErrorCode.BAD_REQUEST);
        }
        return ip;
    }



}
