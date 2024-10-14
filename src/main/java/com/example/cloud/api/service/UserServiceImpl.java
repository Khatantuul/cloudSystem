package com.example.cloud.api.service;

import com.example.cloud.api.model.User;
import com.example.cloud.api.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private static Logger logger = LoggerFactory.getLogger("jsonLogger");


    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Optional<User> findById(String id) {
        return userRepository.findById(id);
    }

    @Override
    public User saveUser(User user) {
        user.setPassword(this.passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public User updateUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(String id) {
        userRepository.deleteById(id);
    }

    @Override
    public boolean setVerified(String email, String token) {
        User user = userRepository.findByUsername(email).get();
        LocalDateTime currentDate = Instant.ofEpochMilli(new Date().getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        if(user.getToken().equals(token)){
            if(user.getExpirationTime().isAfter(currentDate)){
                user.setVerified(true);
                userRepository.save(user);
                return true;
            }
            else {
                logger.error("Email verification link expired");
            }
        }

        return false;

    }


}
