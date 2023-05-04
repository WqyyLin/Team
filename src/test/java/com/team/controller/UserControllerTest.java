package com.team.controller;

import cn.hutool.json.JSONObject;
import com.team.entity.User;
import com.team.mapper.UserMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserControllerTest {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String urlPrefix = "http://localhost:8080/user";

    @BeforeEach
    void setUp() {
        System.out.println("setUp");
    }

    @AfterEach
    void tearDown(){
        System.out.println("tearDown");
    }

    @Test
    void existUserLogin() {
        String url = urlPrefix + "/login/";
        User user = new User();
        user.setEmail("fusisun@gmail.com");
        user.setPassword("12345678");
        String status = "login";
        Map<String, Object> result = restTemplate.postForObject(url + status, user, JSONObject.class);
        assertEquals((Integer) result.get("ercode"), 400);
        assertEquals((String) result.get("message"), "Existing users log in!");
    }

    @Test
    void existManagerLogin() {
        String url = urlPrefix + "/login/";
        User user = new User();
        user.setEmail("root@root.root");
        user.setPassword("root");
        String status = "manager";
        Map<String, Object> result = restTemplate.postForObject(url + status, user, JSONObject.class);
        assertEquals((Integer) result.get("ercode"), 300);
        assertEquals((String) result.get("message"), "Administrator has logged in!");
    }

    @Test
    void userLogin() {
        String url = urlPrefix + "/login/";
        User user = new User();
        user.setEmail("root@root.root");
        user.setPassword("root");
        String status = "logout";
        Map<String, Object> result = restTemplate.postForObject(url + status, user, JSONObject.class);
        assertEquals( (Integer) result.get("ercode"), 200);
        assertEquals( result.get("message"), "Administrator login successfully!");
        assertEquals( result.get("status"), "manager");
    }

    @Test
    void notExistUserLogin() {
        String url = urlPrefix + "/login/";
        User user = new User();
        user.setEmail("root@root.oot");
        user.setPassword("root");
        String status = "logout";
        Map<String, Object> result = restTemplate.postForObject(url + status, user, JSONObject.class);
        assertEquals((Integer) result.get("ercode"), 401);
        assertEquals((String) result.get("message"), "The user does not exist or is not activated!");
    }

    @Test
    void wrongUserPasswordLogin() {
        String url = urlPrefix + "/login/";
        User user = new User();
        user.setEmail("fusisun@gmail.com");
        user.setPassword("root");
        String status = "logout";
        Map<String, Object> result = restTemplate.postForObject(url + status, user, JSONObject.class);
        assertEquals((Integer) result.get("ercode"), 403);
        assertEquals((String) result.get("message"), "Wrong user name or password!");
    }

    @Test
    void UserLogin() {
        String url = urlPrefix + "/login/";
        User user = new User();
        user.setEmail("fusisun@gmail.com");
        user.setPassword("12345678");
        String status = "logout";
        Map<String, Object> result = restTemplate.postForObject(url + status, user, JSONObject.class);
        assertEquals((Integer) result.get("ercode"), 201);
        assertEquals((String) result.get("message"), "Login successful!");
        assertEquals(result.get("status"), "login");
    }

    @Test
    void wrongLogout(){
        String url = urlPrefix + "/logout/";
        String status = "logout";
        Map<String, Object> result = restTemplate.postForObject(url + status, null, JSONObject.class);
        assertEquals((Integer) result.get("code"), 400);
        assertEquals((String) result.get("message"), "Something Failure!");
    }

    @Test
    void logout(){
        String url = urlPrefix + "/logout/";
        String status = "login";
        Map<String, Object> result = restTemplate.postForObject(url + status, null, JSONObject.class);
        assertEquals((Integer) result.get("code"), 200);
        assertEquals((String) result.get("message"), "Successfully log out!");
        assertEquals(result.get("status"), "Logout");
    }

    @Test
    void  wrongCreateUser(){
        String url = urlPrefix + "/create";
        User user = new User();
        user.setEmail("fusisun@gmail.com");
        user.setPassword("123456");
        Map<String, Object> result = restTemplate.postForObject(url, user, JSONObject.class);
        assertEquals((Integer) result.get("code"), 400);
        assertEquals((String) result.get("message"), "The user has registered!");
    }

    @Test
    void  createUser(){
        String url = urlPrefix + "/create";
        User user = new User();
        user.setName("Lin");
        user.setEmail("123@123.123");
        user.setPassword("123456");
        Map<String, Object> result = restTemplate.postForObject(url, user, JSONObject.class);
        assertEquals((Integer) result.get("code"), 200);
        assertEquals((String) result.get("message"), "Register successfully, please go to the mailbox for account activation!");
        String confirmCode = (String) result.get("confirmCode");
//        url = urlPrefix + "/activation";
//        Map<String, Object> result = restTemplate.postForObject(url, user, JSONObject.class);
//        assertEquals((Integer) result.get("code"), 200);
//        assertEquals((String) result.get("message"), "Register successfully, please go to the mailbox for account activation!");
    }

}
