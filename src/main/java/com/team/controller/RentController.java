package com.team.controller;

import com.team.entity.User;
import com.team.mapper.FacilityMapper;
import com.team.mapper.RentMapper;
import com.team.mapper.UserMapper;
import com.team.service.RentService;
import com.team.service.UserService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.Map;

//@RestController
//@RequestMapping("facility/{fid}/{aid}")
//public class RentController {
//
//    @Resource
//    private UserService userService;
//    @Resource
//    private RentService rentService;
//    @Resource
//    private UserMapper userMapper;
//    @Resource
//    private FacilityMapper facilityMapper;
//    @Resource
//    private RentMapper rentMapper;
//
//
//
//    /**
//     * 用户预约进入的时间并选择人数
//     */
//    @PostMapping()
//    public Map<String, Object> rentFacility(@PathVariable Integer fid, @PathVariable Integer aid, @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime time, Integer limit, Integer num, HttpSession session){
//        return rentService.rentFacility(fid, aid, time, limit, num, session);
//    }
//
//}
