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
    public Map<String, Object> createAccount(@RequestBody Map<String, Object> map){
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

    /**
     * 设施页面
     */
    @GetMapping("facilities")
    public @ResponseBody Map<String, Object> facilityPage(){
        Map<String, Object> resultMap = new HashMap<>();
        List<Map<String, Object>> facilities = facilityMapper.selectAllFacility();
        resultMap.put("code", 200);
        resultMap.put("facilities", facilities);
        return resultMap;
    }

    @GetMapping("lessons")
    public @ResponseBody Map<String, Object> lessonPage(){
        Map<String, Object> resultMap = new HashMap<>();
        Map<String, Object> facilities = new HashMap<>();
        List<Map<String, Object>> lessons = projectMapper.selectAllLessons();
        for(Map<String, Object> lesson: lessons){
            String facility = (String) lesson.get("facility");
            if(!facilities.containsKey(facility)){
                facilities.put(facility, new HashMap<>());
            }
            String activity = (String) lesson.get("activity");
            Map<String, Object> activities = (Map<String, Object>) facilities.get(facility);
            if (!activities.containsKey(activity)){
                activities.put(activity, new ArrayList<>());
            }
            String weekday = (String) lesson.get("dayOfWeek");
            String[] dayOfWeek= weekday.split(",");
            lesson.put("dayOfWeek", dayOfWeek);
            List<Map<String, Object>> realLessons = (List<Map<String, Object>>) activities.get(activity);
            realLessons.add(lesson);
        }
        resultMap.put("code", 200);
        resultMap.put("lessons", facilities);
        return resultMap;
    }

}
