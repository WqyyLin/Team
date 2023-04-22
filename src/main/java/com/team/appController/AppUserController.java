package com.team.appController;

import com.team.entity.User;
import com.team.mapper.ActicityMapper;
import com.team.mapper.FacilityMapper;
import com.team.mapper.ProjectMapper;
import com.team.mapper.UserMapper;
import com.team.service.RentService;
import com.team.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("app/user")
public class AppUserController {


    @Resource
    private RentService rentService;
    @Resource
    private FacilityMapper facilityMapper;
    @Resource
    private ProjectMapper projectMapper;
    @Resource
    private ActicityMapper acticityMapper;

    @Resource
    private UserService userService;
    @Resource
    private UserMapper userMapper;

    /**
     * 注册账号
     */
    @PostMapping("create")
    public @ResponseBody  Map<String, Object> createAccount(@RequestBody User map){
        return userService.createAccount(map);
    }

    /**
     * 登录账号
     */
    @PostMapping("login")
    public Map<String, Object> loginAccount(@RequestBody User user, HttpSession session) {
        return userService.loginAccount(user, session);
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

    @PostMapping("charge")
    public Map<String, Object> chargeMoney(@RequestBody Map<String, Object> map, HttpSession session){
        return userService.chargeMoney(map, session);
    }

}
