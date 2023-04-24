package com.team.controller;

import com.team.entity.User;
import com.team.mapper.UserMapper;
import com.team.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("user")
public class UserController {

    @Resource
    private UserService userService;
    @Resource
    private UserMapper userMapper;

    /**
     * 注册账号
     */
    @PostMapping("create")
    public Map<String, Object> createAccount(@RequestBody User map){
        return userService.createAccount(map);
    }

    /**
     * 登录账号
     */
    @PostMapping("login/{status}")
    public Map<String, Object> loginAccount(@RequestBody User user, @PathVariable String status) {
        return userService.loginAccount(user, status);
    }

    /**
     * 登出账号
     */
    @PostMapping("logout")
    public Map<String, Object> logoutAccount(HttpSession session) {
        return userService.logoutAccount(session);
    }

    /**
     * 激活账号
     */
    @GetMapping("activation")
    public Map<String, Object> activationAccont(String confirmCode) {
        return userService.activationAccound(confirmCode);
    }

    @PostMapping("facility/{facility}")
    public @ResponseBody Map<String, Object> book(@PathVariable String facility, @RequestBody Map<String, Object> map, HttpSession session){
        return userService.book(facility, map, session);
    }



}
