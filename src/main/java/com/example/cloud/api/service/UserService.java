package com.example.cloud.api.service;


import com.example.cloud.api.model.User;

import java.util.List;
import java.util.Optional;


public interface UserService {
    List<User> findAllUsers();
    Optional<User> findByUsername(String username);
    Optional<User> findById(String id);
    User saveUser(User user);
    User updateUser(User user);
    void deleteUser(String id);

    boolean setVerified(String email, String token);
}
