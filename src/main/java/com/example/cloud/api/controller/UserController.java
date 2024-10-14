package com.example.cloud.api.controller;


import com.example.cloud.api.PubSubConfig;
import com.example.cloud.api.model.User;
import com.example.cloud.api.service.UserService;
import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutureCallback;
import com.google.api.core.ApiFutures;
import com.google.api.gax.rpc.ApiException;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.gson.Gson;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/v2/user")
public class UserController {

    private final UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    private static Logger logger = LoggerFactory.getLogger("jsonLogger");

    @Autowired
    private PubSubConfig publisher;
    private final Gson gson;

    public UserController(UserService userService, Gson gson
    ) {
        this.userService = userService;
        this.gson = gson;
    }

    @PostMapping(consumes = "application/json")
    public ResponseEntity<?> saveUser(@RequestBody Map<String, String> requestBody){
        logger.debug("UserController class and saveUser method");
        logger.info("Creating user process started..!");
        String firstname = requestBody.get("first_name");
        String lastname = requestBody.get("last_name");
        String password = requestBody.get("password");
        String username = requestBody.get("username");

        if((password == null || password.isEmpty()) || (firstname == null || firstname.isEmpty()) || (lastname == null || lastname.isEmpty()) ||
                (username == null || username.isEmpty())){
            logger.error("Creating user needs all details provided!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        try{
            if(validEmail(username) == false){
                logger.error("Invalid email address!");
                throw new Exception("Invalid email!");
            }
            //if user with username exists, 400
            Optional<User> user = userService.findByUsername(username);
            if (!user.isEmpty()){
                logger.error("User already exists!");
                throw new Exception("User already exists");
            }
            User newUser = userService.saveUser(new User(firstname,lastname,password,username));
            logger.info("User is created in the database!");
            publisher.publishWithErrorHandlerExample(newUser.getUsername(), newUser.getFirst_name());
            return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
        }catch (Exception e){
            logger.error("Creating user request failed / invalid!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }


    private boolean validEmail(String username){
        logger.debug("UserController class and validEmail method");

        String EMAIL_REGEX =
                "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(EMAIL_REGEX);

        Matcher matcher = pattern.matcher(username);
        if(matcher.matches()){
            return true;
        }
        else {
            logger.error("Email is invalid");
            return false;
        }
    }



    @GetMapping("/self")
    public ResponseEntity<?> getUser(@RequestHeader("Authorization") String authHeader) {
        logger.debug("UserController class and getUser method");

        String username = null;
        String password = null;

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
        headers.add(HttpHeaders.PRAGMA,"no-cache");
        headers.add("X-Content-Type-Options", "nosniff");

        if(authHeader != null && authHeader.startsWith("Basic ")){
            String authHeaderTrimmed = authHeader.substring("Basic ".length()).trim();
            byte[] decodedAuthHeader = Base64.getDecoder().decode(authHeaderTrimmed);
            String hhh = new String(decodedAuthHeader);
            String[] usernamePassCombo = hhh.split(":");
            username = usernamePassCombo[0];
            password = usernamePassCombo[1];
        }

        if(username!= null || password != null){
            logger.info("Trying to authenticate user...!");
            if(isUserVerified(username)){
                if (authenticateUser(username, password)) {
                    logger.info("User authentication success...!");
                    Optional<User> user = userService.findByUsername(username);
                    logger.info("Returning the user details...!");
                    return ResponseEntity.status(200).headers(headers).body(user);
                } else {
                    logger.error("User authentication failed...!");
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).headers(headers).build();
                }
            }else{
                return ResponseEntity.status(HttpStatus.FORBIDDEN).headers(headers).build();
            }

        }else{
            logger.error("No authentication credentials were given..!");
            return ResponseEntity.status(401).body("No credentials provided");
        }


    }

    private boolean authenticateUser(String username, String password) {
        logger.debug("UserController class and authenticateUser method");
        Optional<User> user = userService.findByUsername(username);
        return user.map(userp -> passwordEncoder.matches(password, userp.getPassword())).orElse(false);

    }

    private boolean isUserVerified(String username){
        Optional<User> user = userService.findByUsername(username);

        if(user.get().isVerified()){
            return true;
        }
        return false;
    }


    private void update(User user, String firstname, String lastname, String newPassword){
        logger.debug("UserController class and update method");

        if(firstname != null && !firstname.isEmpty()){
            user.setFirst_name(firstname);
        }
        if(lastname != null && !lastname.isEmpty()){
            user.setLast_name(lastname);
        }

        if(newPassword != null && !newPassword.isEmpty()){
            String hashedNewPassword = passwordEncoder.encode(newPassword);
            user.setPassword(hashedNewPassword);
        }
    }


    private boolean updateValid(Map<String,String> requestBody){
        logger.debug("UserController class and updateValid method");

        Set<String> fieldsToInclude = Set.of("first_name","last_name","password","username");
        for (String key: requestBody.keySet()){
            if(!fieldsToInclude.contains(key)){
                logger.error("Invalid field sent for updating!");
                return false;
            }
        }
        return true;
    }

    @PutMapping("/self")
    public ResponseEntity<?> updateUser(@RequestBody Map<String, String> requestBody,@RequestHeader("Authorization") String authHeader){
        logger.debug("UserController class and updateUser method");

        String username = null;
        String password = null;

        String firstname = null;
        String lastname = null;
        String newPassword = null;


        if(requestBody.containsKey("first_name")){
            firstname = requestBody.get("first_name");
        }
        if(requestBody.containsKey("last_name")){
            lastname = requestBody.get("last_name");
        }
        if(requestBody.containsKey("password")){
            newPassword = requestBody.get("password");
        }

        if((newPassword != null && newPassword.isEmpty()) || (firstname != null && firstname.isEmpty()) || (lastname != null && lastname.isEmpty())){
            logger.error("Fields for update must not be empty!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }


        if(authHeader != null && authHeader.startsWith("Basic ")){
            String authHeaderTrimmed = authHeader.substring("Basic ".length()).trim();
            byte[] decodedAuthHeader = Base64.getDecoder().decode(authHeaderTrimmed);
            String hhh = new String(decodedAuthHeader);
            String[] usernamePassCombo = hhh.split(":");
            username = usernamePassCombo[0];
            password = usernamePassCombo[1];
        }

        try {
            Optional<User> userOptional = userService.findByUsername(username);
            User user = userOptional.get();

            if (isUserVerified(username)) {
                if (authenticateUser(username, password)) {
                    if (updateValid(requestBody)) {

                        update(user, firstname, lastname, newPassword);
                        userService.updateUser(user);
                        return ResponseEntity.status(204).build();
                    } else {
                        logger.error("Attempt to update unauthorized fields");
                        throw new Exception("Attempt to update unauthorized fields");
                    }
                } else {
                    logger.error("User authentication failed!");
                    throw new Exception("Bad request");
                }
            }else{
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }catch (Exception e){
            logger.error("Updating user details failed..!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verifyToken(@RequestParam  String email, @RequestParam String token) {

        try {
            if(userService.setVerified(email,token)){
                return ResponseEntity.ok("Email verification for " + email + " succeeded");
            }else{
                return ResponseEntity.status(HttpStatus.GONE).body("Verification link expired");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

}
