package com.team.service;


import com.team.entity.Activity;
import com.team.entity.Facility;
import com.team.entity.Rent;
import com.team.entity.User;
import com.team.mapper.ActicityMapper;
import com.team.mapper.FacilityMapper;
import com.team.mapper.RentMapper;
import com.team.mapper.UserMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RentService {

    @Resource
    private FacilityMapper facilityMapper;
    @Resource
    private ActicityMapper acticityMapper;
    @Resource
    private RentMapper rentMapper;

    /**
     * 用户预约
     */
//    public Map<String, Object> rentFacility(Integer fid, Integer aid, LocalDateTime time, Integer limit, Integer num, HttpSession session){
//        Map<String, Object> resultMap = new HashMap<>();
//        //确认登录状态
//        String status = (String) session.getAttribute("status");
//        if(status == null){
//            //用户未登录
//            resultMap.put("code", 400);
//            resultMap.put("message", "请先进行登录");
//            return resultMap;
//        }
//        //得到登录用户
//        User user = (User) session.getAttribute("user");
//        //得到当前用户选择的设施
//        Facility facility = facilityMapper.selectFacilityByFid(fid);
//        Integer capacity = facility.getCapacity();
//        //当前设施该时间的使用量
//        Integer now = rentMapper.selectUsedFacilityNumber(fid, time, time.plusHours(limit).minusSeconds(1));
//        if (now >= capacity){
//            //设施容量不够
//            resultMap.put("code", 400);
//            resultMap.put("message", "该设施容量以达到上限");
//            return resultMap;
//        }
//        if (now + num > capacity){
//            //预约数量超额
//            resultMap.put("code", 400);
//            resultMap.put("message", "预约数量过多，请重新选择");
//            return resultMap;
//        }
//        //得到当前用户id
//        Integer id = user.getId();
//        //建立预约关系
//        Rent rent = new Rent();
//        rent.setAid(aid);
//        rent.setFid(fid);
//        rent.setId(id);
//        rent.setTime(time);
//        rent.setLimitTime(time.plusHours(limit));
//        rent.setRentTime(LocalDateTime.now());
//        //得到用户当前选择活动
//        Activity activity = acticityMapper.selectActivityByAid(aid);
//        rent.setMoney(activity.getMoney());
//        int i = 0;
//        for(; i < num; i++){
//            rentMapper.insertRentInformation(rent);
//        }
//        if(i == num) {
//            //预约成功
//            resultMap.put("code", 200);
//            resultMap.put("message", "预约成功");
//        }else{
//            //预约失败
//            resultMap.put("code", 400);
//            resultMap.put("message", "预约失败");
//        }
//        return resultMap;
//    }

    /**
     * 每天删除30天前的预约信息
     * @return 0
     */
    public void rent30AgoDelete(){
        //得到30天前时间
        LocalDateTime timeAgo = LocalDateTime.of(LocalDate.now(), LocalTime.MAX).minusDays(30);
        //得到所有预约信息
        List<Rent> rentList = rentMapper.selectAll();
        //单个判断预约是否超时
        for(Rent r: rentList){
            //得到用户预约的时间
            LocalDateTime time = r.getLimitTime();
            //判断预约时间是否合法
            if (timeAgo.isAfter(time)) {
                //预约时间不合法,直接删除
                rentMapper.deleteRentByRid(r.getRid());
            }
        }
    }

    public void stopRentRestart(){
        //得到当前时间
        LocalDateTime timeNow = LocalDateTime.now();
        //得到目标
        LocalDateTime timeTarget = LocalDateTime.of(LocalDate.MAX.withYear(9999),LocalTime.MIN);
        facilityMapper.restartFacility(timeTarget, timeNow);
    }

}
