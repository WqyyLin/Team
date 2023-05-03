package com.team.appController;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.team.entity.Facility;
import com.team.entity.User;
import com.team.mapper.FacilityMapper;
import com.team.mapper.UserMapper;
import com.team.service.ManagerService;
import org.apache.ibatis.annotations.Delete;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("app/user/manager")
public class AppManagerController {

    @Resource
    private ManagerService managerService;
    @Resource
    private UserMapper userMapper;
    @Resource
    private FacilityMapper facilityMapper;

    /**
     * 管理员主页面
     */
    @GetMapping()
    public Map<String, Object> managerPage(HttpSession session){
        return managerService.managerPage(session);
    }

    /**
     * 添加卡片
     */
    @PostMapping("card/{status}")
    public @ResponseBody Map<String, Object> addCard(@RequestBody Map<String, Object> map, @PathVariable String status){
        return managerService.addCard(map, status);
    }

    /**
     * 设施管理页面
     */
    @GetMapping("facilities")
    public List<Map<String, Object>> facilitiesInformation(){
        return managerService.facilitiesInformation();
    }

    @PostMapping("facilities")
    public @ResponseBody Map<String, Object> addActivityLesson(@RequestBody Map<String, Object> map){
        return managerService.addActivityLesson(map);
    }

    /**
     * 添加新设施
     */
    @PostMapping("facilities/add")
    public Map<String, Object> addFacility(@RequestBody Map<String, Object> map){
        return managerService.addFacility(map);
    }

    /**
     * 修改设施
     */
//    @PutMapping("facilities")
//    public Map<String, Object> changeFacility(Integer fid, String name, Integer capacity){
//        return managerService.changeFacility(fid, name, capacity);
//    }

    /**
     * 删除设施
     */
    @DeleteMapping("facilities")
    public Map<String, Object> deleteFacility(@RequestBody Map<String, Object> map){
        return managerService.deleteFacility(map);
    }

    /**
     * 用户管理页面
     */
    @GetMapping("users")
    public @ResponseBody Map<String, Object> usersInformation(){
        return managerService.usersInformation();
    }

    /**
     * 重置用户密码
     */
    @PostMapping("users/{email}")
    public @ResponseBody Map<String, Object> resetUserPassword(@PathVariable String email){
        return managerService.resetUserPassword(email);
    }

    /**
     * 删除用户
     */
    @DeleteMapping("users")
    public Map<String, Object> deleteUser(@RequestBody Map<String, Object> map){
        return managerService.deleteUser(map);
    }

    /**
     * 修改用户信息
     */
    @PutMapping("users")
    public Map<String, Object> changeUserInfo(@RequestBody Map<String, Object> map){
        return managerService.changeUserInfo(map);
    }

    /**
     * 用户预约信息界面
     */
    @GetMapping("users/{email}/rents")
    public Map<String, Object> userRentInfo(@PathVariable String email){
        return managerService.userRentInfo(email);
    }

    /**
     * 删除某预约信息
     */
    @DeleteMapping("users/{email}/rents")
    public Map<String, Object> deleteRent(@RequestBody Map<String, Object> map, @PathVariable String email){
        return managerService.deleteRent(map, email);
    }

    /**
     * 修改预约信息
     */
    @PutMapping("users/{email}/rents")
    public Map<String, Object> changeRent(@RequestBody Map<String, Object> map, @PathVariable String email) {
        return managerService.changeRent(map, email);
    }

    /**
     * 管理员登出
     */
    @PostMapping("logout")
    public Map<String, Object> managerLogout(HttpSession session){
        return managerService.managerLogout(session);
    }


}

