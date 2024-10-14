package com.example.cloud;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CloudApplication {

    public static void main(String[] args) {

        SpringApplication.run(CloudApplication.class, args);
        Logger logger = LoggerFactory.getLogger("jsonLogger");
        logger.debug("Debug message");
    }


}
