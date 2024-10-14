package com.example.cloud.api.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

@RestController
public class HealthCheckController {
    private final JdbcTemplate jdbcTemplate;
    private static Logger logger = LoggerFactory.getLogger("jsonLogger");


    public HealthCheckController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/{wrong}")
    public ResponseEntity<Void> checkConnection(@PathVariable String wrong, @RequestParam Map<String, String> requestParams ){
        logger.debug("HealthCheckController class and checkConnection method");
        logger.info("Checking database connection!");
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
        headers.add(HttpHeaders.PRAGMA,"no-cache");
        headers.add("X-Content-Type-Options", "nosniff");

        if (!requestParams.isEmpty()) {
            logger.error("Request body has values, not allowed!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).headers(headers).build();
        }

        if (!wrong.equals("healthz")){
            logger.error("Wrong API endpoint!");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }else{

            try (Connection connection = jdbcTemplate.getDataSource().getConnection()) {
                logger.info("Connection successful!");
                return ResponseEntity.ok().headers(headers).build();
            } catch (SQLException e) {
                logger.error("Failed to establish database connection", e);
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
            }
        }
    }


    @RequestMapping(value = "/{wrong}", method = {RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.PATCH,
            RequestMethod.HEAD, RequestMethod.OPTIONS})
    public ResponseEntity<Void> prohibitMethods(@PathVariable String wrong) {
        logger.debug("HealthCheckController clas and prohibitMethods method");

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
        headers.add(HttpHeaders.PRAGMA,"no-cache");
        headers.add("X-Content-Type-Options", "nosniff");

        if (!wrong.equals("healthz")){
            logger.error("Wrong API endpoint!");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }else{
            logger.error("HTTP method not allowed!");
            return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).headers(headers).build();
        }
    }

    @RequestMapping(value = "/{wrong}",method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.PATCH,
            RequestMethod.OPTIONS, RequestMethod.HEAD}, consumes = {"application/json", "application/xml"})
    public ResponseEntity<Void> prohibitPayload(@PathVariable String wrong) {
        logger.debug("HealthCheckController clas and prohibitPayload method");

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
        headers.add(HttpHeaders.PRAGMA,"no-cache");
        headers.add("X-Content-Type-Options", "nosniff");

        if (!wrong.equals("healthz")){
            logger.error("Wrong API endpoint!");

            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }else{
            logger.error("Payload provided, not allowed!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).headers(headers).build();
        }
    }


}


