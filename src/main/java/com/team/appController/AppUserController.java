package com.team.appController;

import com.team.entity.User;
import com.team.mapper.ActicityMapper;
import com.team.mapper.FacilityMapper;
import com.team.mapper.ProjectMapper;
import com.team.mapper.UserMapper;
import com.team.service.RentService;
import com.team.service.UserService;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
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
    public @ResponseBody  Map<String, Object> createAccount(@RequestBody Map<String, Object> map){
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
    @PostMapping("logout/{status}")
    public Map<String, Object> logoutAccount(@PathVariable String status) {
        return userService.logoutAccount(status);
    }

    /**
     * 激活账号
     */
    @GetMapping("activation")
    public Map<String, Object> activationAccont(String confirmCode) {
        return userService.activationAccound(confirmCode);
    }

    @PostMapping("charge/{email}")
    public @ResponseBody Map<String, Object> chargeMoney(@RequestBody Map<String, Object> map, @PathVariable String email){
        return userService.chargeMoney(map, email);
    }

    @PostMapping("card/member/{email}")
    public @ResponseBody Map<String, Object> inMember(@RequestBody Map<String, Object> map, @PathVariable String email){
        return userService.inMember(map, email);
    }

    @PostMapping("book/activity/{email}")
    public @ResponseBody Map<String, Object> bookActivity(@RequestBody Map<String, Object> map, @PathVariable String email){
        return userService.bookActivity(map, email);
    }

    @PostMapping("book/lesson/{email}")
    public @ResponseBody Map<String, Object> bookLesson(@RequestBody Map<String, Object> map, @PathVariable String email){
        return userService.bookLesson(map, email);
    }

    @PostMapping("code/{orderNumber}")
    public @ResponseBody Map<String, Object> getOrder(@PathVariable String orderNumber){
        return userService.getOrder(orderNumber);
    }

    @PostMapping("order/details/{rid}")
    public @ResponseBody Map<String, Object> orderDetail(@PathVariable Integer rid){
        return userService.orderDetail(rid);
    }

    @DeleteMapping("order/{rid}")
    public @ResponseBody Map<String, Object> deleteOrder(@PathVariable Integer rid){
        return userService.deleteOrder(rid);
    }

}
