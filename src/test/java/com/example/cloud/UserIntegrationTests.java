package com.example.cloud;


import com.example.cloud.api.model.User;
import com.example.cloud.api.repository.UserRepository;
import com.mashape.unirest.http.exceptions.UnirestException;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;
import org.postgresql.shaded.com.ongres.scram.common.bouncycastle.base64.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserIntegrationTest {
    @Autowired
    private UserRepository userRepository;


    @BeforeAll
    public static void initialize() throws UnirestException {
        CloudApplication.main(new String[]{});

    }


    @Test
    @Order(1)
    public void createUserTest(){
        String requestBody = "{\"first_name\": \"khatnaa\"," +
                "              \"last_name\": \"bat\", " +
                "              \"username\": \"khatna_doe@example.com\", " +
                "               \"password\": \"haha\"}";

        // When
        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("http://localhost:8080/v2/user")
                .then()
                .statusCode(201);


    }


    @Test
    @Order(2)
    public void getUserTest(){

        String username = "khatna_doe@example.com";
        String password = "haha";
        //provide user/pass from above
        String credentials = username + ":" + password;
        User user = userRepository.findByUsername(username).get();
        user.setVerified(true);
        userRepository.save(user);

        //create auth heeader
        byte[] encodedCreds = Base64.encode(credentials.getBytes());

        String authHeader = "Basic " + new String(encodedCreds);

        // Then
        given()
                .header("Authorization", authHeader)
                .accept(ContentType.JSON)
                .when()
                .get("http://localhost:8080/v2/user/self")
                .then()
                .assertThat()
                .statusCode(200)
                .body("first_name", equalTo("khatnaa"))
                .body("last_name", equalTo("bat"))
                .body("username", equalTo("khatna_doe@example.com"));

    }


    @Test
    @Order(3)
    public void updateUserTest(){

        String username = "khatna_doe@example.com";
        String password = "haha";
        //provide user/pass from above
        String credentials = username + ":" + password;

        //create auth heeader
        byte[] encodedCreds = Base64.encode(credentials.getBytes());

        String authHeader = "Basic " + new String(encodedCreds);
        //separate these 2 and flow same

        String requestBody = "{\"first_name\": \"kha\"}";

        // When
        given()
                .header("Authorization",authHeader)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .put("http://localhost:8080/v2/user/self")
                .then()
                .statusCode(204);
    }

    @Test
    @Order(4)
    public void getAfterUpdateTest(){
        String username = "khatna_doe@example.com";
        String password = "haha";
        //provide user/pass from above
        String credentials = username + ":" + password;

        //create auth heeader
        byte[] encodedCreds = Base64.encode(credentials.getBytes());

        String authHeader = "Basic " + new String(encodedCreds);

        // Then
        given()
                .header("Authorization", authHeader)
                .when()
                .get("http://localhost:8080/v2/user/self")
                .then()
                .statusCode(200)
                .body("first_name", equalTo("kha"))
                .body("last_name", equalTo("bat"))
                .body("username", equalTo("khatna_doe@example.com"));
    }

}
