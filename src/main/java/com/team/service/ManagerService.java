package com.team.service;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.SecureUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.team.entity.Activity;
import com.team.entity.Facility;
import com.team.entity.Project;
import com.team.entity.Rent;
import com.team.mapper.*;
import com.team.tools.ServiceHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class ManagerService {

    @Resource
    private FacilityMapper facilityMapper;

    @Resource
    private ActicityMapper acticityMapper;

    @Resource
    private ProjectMapper projectMapper;

    @Resource
    private RentMapper rentMapper;
    @Resource
    private UserMapper userMapper;

    @Resource
    private ServiceHelper serviceHelper;

    @Value("${manager.email}")
    private String email;

    @Value("${manager.password}")
    private String password;

    /**
     * 返回管理员页面信息
     */
    public Map<String, Object> managerPage(HttpSession session){
        Map<String, Object> resultMap = new HashMap<>();
        String status = (String) session.getAttribute("status");
        if(status == null){
            resultMap.put("code", 400);
            resultMap.put("message", "请先进行登录验证");
            return resultMap;
        }else if (status.equals("login")){
            resultMap.put("code", 400);
            resultMap.put("message", "您没有访问权限");
            return resultMap;
        }
        //获取当日时间
        LocalDateTime time = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        //新注册人数
        resultMap.put("newPeople", serviceHelper.oneDayNewPeopleNumber(time.plusDays(1)));
        //今日预约人数
        resultMap.put("rentPeople", serviceHelper.oneDayRentPeople(time));
        //30天营业额
        List<Double> money = serviceHelper.getMoney(time,30);
        resultMap.put("money", money);
        //30天总营业额
        double sum = 0;
        for(Double num :money){
            sum += num;
        }
        resultMap.put("thirtyMoney", sum);
        //今日营业额
        resultMap.put("todayMoney", money.get(29));
        //七日增长率
        resultMap.put("increase", serviceHelper.getIncrease(money, 7));
        //当日满场率
        resultMap.put("rate", serviceHelper.todayAttendanceRate(time));
        return resultMap;
    }


    /**
     * 展示所有场馆信息
     */
    public Map<String, Object> facilitiesInformation(){
        Map<String, Object> resultMap = new HashMap<>();
        List<Map<String, Object>> facilities = facilityMapper.selectAllFacility();
        for (Map<String, Object> facility: facilities){
            facility.put("activity", acticityMapper.selectActivityName((String) facility.get("name")));
            Time startTime = (Time) facility.get("starttime");
            Time endTime = (Time) facility.get("endtime");
            facility.put("starttime", startTime.toLocalTime().getHour());
            facility.put("endtime", endTime.toLocalTime().getHour());
        }
        resultMap.put("groundName", facilities);
        return resultMap;
    }

    /**
     * 添加activity的project
     */
    public Map<String, Object> addActivityLesson(Map<String, Object> map) {
        Map<String, Object> resultMap = new HashMap<>();
        //获取设施
        String facilityName = (String) map.get("Sitename");
        //判断该设施是否存在
        if(facilityMapper.selectIsAvailable(facilityName) < 1){
            resultMap.put("code", 400);
            resultMap.put("message", "Illegal facility");
            return resultMap;
        }
        boolean isAddActivity;
        //是否添加新活动
        if(map.get("Add_activity") != null){
            isAddActivity = (boolean) map.get("Add_activity");
        }else{
            isAddActivity = false;
        }
        //添加新活动
        String activityName = (String) map.get("Activity_name");
        //获取项目编号
        Integer isLesson = (Integer) map.get("Lesson");
        if(isAddActivity){
            if(acticityMapper.selectIsAvailable(activityName, facilityName, isLesson) >= 1){
                resultMap.put("code", 401);
                resultMap.put("message", "Illegal activity");
                return resultMap;
            }
            Activity newActivity = new Activity();
            newActivity.setFacility(facilityName);
            newActivity.setIsLesson(isLesson);
            newActivity.setName(activityName);
            acticityMapper.insertActivity(newActivity);
        }else{
            if(acticityMapper.selectIsAvailable(activityName, facilityName, isLesson) < 1){
                resultMap.put("code", 402);
                resultMap.put("message", "Illegal activity");
                return resultMap;
            }
        }
        String lessonName = (String) map.get("Name");
        if(projectMapper.selectIsAvailable(lessonName, facilityName,activityName) >= 1){
            resultMap.put("code", 404);
            resultMap.put("message", "The project already exists");
            return resultMap;
        }
        Project project = new Project();
        project.setName(lessonName);
        //设置时区
        ZoneId zoneId = ZoneId.systemDefault();
        //项目名称
        project.setActivity(activityName);
        project.setFacility(facilityName);
        project.setIsLesson(isLesson);
        //项目价格
        Integer price = (Integer) map.get("Price");
        project.setMoney(price);
        //是否为周期性项目
        int isWeekly;
        if(!((Boolean) map.get("Weekly"))){
            isWeekly = 0;
        }else {
            isWeekly = 1;
        }
        project.setIsWeekly(isWeekly);
        //项目开始时间
        LocalDateTime startTime;
        //项目结束时间
        LocalDateTime endTime;
        //获取时间
        SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
//            startTime = formatter.parse((String) map.get("Starttime"));
//            endTime = formatter.parse((String) map.get("Endtime"));
            startTime = LocalDateTime.ofInstant(formatter.parse((String) map.get("Starttime")).toInstant(), zoneId);
            endTime = LocalDateTime.ofInstant(formatter.parse((String) map.get("Endtime")).toInstant(), zoneId);
            if(isWeekly == 1) {
                startTime = LocalDateTime.of(LocalDate.now().plusDays(7), startTime.toLocalTime());
                endTime = LocalDateTime.of(LocalDate.now().plusDays(7), endTime.toLocalTime());
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        project.setStartTime(startTime);
        project.setEndTime(endTime);
        //项目描述
        String description = (String) map.get("Describetion");
        project.setDescription(description);
        List<Integer> weekTime = (List<Integer>) map.get("Weekly_Time");
        //项目预留容量
        Integer capacity = (Integer) map.get("Reserve_place");
        //当前设施所有项目
        List<Map<String, Object>> projects = projectMapper.selectAllProjectOfOneFacility(facilityName);
        //可使用容量
        Integer usedCapacity = 0;
        for(Map<String, Object> p: projects){
            LocalDateTime firstTime = (LocalDateTime) p.get("startTime");
            LocalDateTime lastTime = (LocalDateTime) p.get("endTime");
            Integer isPWeekly = (Integer) p.get("isWeekly");
            //判断时间是否重合
            if(isPWeekly == 1){
                if(!(lastTime.toLocalTime().isBefore(startTime.toLocalTime()) || firstTime.toLocalTime().isAfter(endTime.toLocalTime()))) {
                    String[] week = ((String) p.get("dayOfWeek")).split(",");
                    List<Integer> trueWeek = new ArrayList<>();
                    for (String w: week){
                        trueWeek.add(Integer.parseInt(w));
                    }
                    if (isWeekly == 1){
                        if (Objects.equals(trueWeek, weekTime)){
                            usedCapacity += (Integer) p.get("capacity");
                        }
                    }else{
                        LocalDate tempStart = startTime.toLocalDate();
                        LocalDate tempEnd = endTime.toLocalDate();
                        while (!tempEnd.isBefore(tempStart)){
                            String day = tempStart.getDayOfWeek().toString();
                            int startOay = 0;
                            switch (day) {
                                case "MONDAY" -> startOay = 1;
                                case "TUESDAY" -> startOay = 2;
                                case "WEDNESDAY" -> startOay = 3;
                                case "THURSDAY" -> startOay = 4;
                                case "FRIDAY" -> startOay = 5;
                                case "SATURDAY" -> startOay = 6;
                                case "SUNDAY" -> startOay = 7;
                            }
                            System.out.println(startOay);
                            if (trueWeek.contains(startOay)){
                                usedCapacity += (Integer) p.get("capacity");
                                break;
                            }
                            tempStart = tempStart.plusDays(1);
                        }
                    }
                }
            }else{
                if(!(lastTime.isBefore(startTime.plusSeconds(1)) || firstTime.isAfter(endTime.plusSeconds(1)))) {
                    usedCapacity += (Integer) p.get("capacity");
                }
            }
        }
        usedCapacity = facilityMapper.selectCapacity(facilityName) - usedCapacity;
        System.out.println(usedCapacity);
        if(usedCapacity < capacity){
            resultMap.put("code", 403);
            resultMap.put("message", "Inadequate facility capacity");
            return resultMap;
        }
        project.setCapacity(capacity);
        StringBuilder weekDay = new StringBuilder();
        for(Integer day: weekTime){
            if(day == 1){
                weekDay.append("1,");
            }else if(day == 2){
                weekDay.append("2,");
            }else if(day == 3){
                weekDay.append("3,");
            }else if(day == 4){
                weekDay.append("4,");
            }else if(day == 5){
                weekDay.append("5,");
            }else if(day == 6){
                weekDay.append("6,");
            }else if(day == 7){
                weekDay.append("7,");
            }
        }
        String week = weekDay.toString();
        project.setWeekDay(week);
        projectMapper.insertProject(project);
        resultMap.put("code", 200);
        resultMap.put("message", "Add successfully");
        return resultMap;
    }

    /**
     * 展示所有用户信息
     */
    public Map<String, Object> usersInformation(){
        Map<String, Object> resultMap = new HashMap<>();
        List<Map<String, Object>> users = userMapper.selectAllUsers();
        resultMap.put("information", users);
        return resultMap;
    }

    /**
     * 展示单个用户所有预约信息
     */
    public Map<String, Object> userRentInfo(String email){
        Map<String, Object> resultMap = new HashMap<>();
        List<Rent> rents = rentMapper.selectRentsByEmail(email);
        resultMap.put("information", rents);
        return resultMap;
    }

    /**
     * 管理员登出
     */
    public Map<String, Object> managerLogout(HttpSession session){
        String status = (String) session.getAttribute("status");
        Map<String, Object> resultMap = new HashMap<>();
        if(status == null){
            //登出错误
            resultMap.put("code", 400);
            resultMap.put("message", "当前无账号登录");
            return resultMap;
        }
        if (status.equals("manager")){
            //登出成功
            session.invalidate();
            resultMap.put("code", 200);
            resultMap.put("message", "登出成功");
        }else {
            //登出失败
            resultMap.put("code", 400);
            resultMap.put("message", "登出失败");
        }
        return resultMap;
    }

    /**
     * 添加设施
     */
    public Map<String, Object> addFacility(Map<String, Object> maps){
        Map<String, Object> resultMap = new HashMap<>();
        Map<String, Object> siteInformation = (Map<String, Object>) maps.get("sitesinformation");
        List<Map<String, Object>> activities = (List<Map<String, Object>>) siteInformation.get("activity");
        Facility facility = new Facility();
        //获取设施名称
        String name = (String)siteInformation.get("name");
        facility.setName(name);
        //判断当前设施是否已经存在
        if(facilityMapper.selectIsAvailable(name) > 0){
            resultMap.put("code", 400);
            resultMap.put("message", "illegal facility");
            return resultMap;
        }
        //获取单个设施的容量
        Integer capacity = (Integer) siteInformation.get("max_cap");
        facility.setCapacity(capacity);
        //获取展示页面标题
        String title = (String) siteInformation.get("Ad_title");
        facility.setTitle(title);
        //获取设施详细描述
        String description = (String) siteInformation.get("Ad_description");
        facility.setDescription(description);
        //获取设施数量
        Integer facilitiesNumber = (Integer) siteInformation.get("site_num");
        //获取营业和歇业时间
        Integer start = (Integer) siteInformation.get("starttime");
        LocalTime startTime = LocalTime.of(start, 0, 0);
        facility.setStartTime(startTime);
        Integer end = (Integer) siteInformation.get("endtime");
        LocalTime endTime = LocalTime.of(end, 0, 0);
        facility.setEndTime(endTime);
        facility.setStopTime(LocalDateTime.of(9999, 12, 3, 0, 0, 0));
        //根据数量添加设施
        while(facilitiesNumber>0){
            facilityMapper.insertFacility(facility);
            facilitiesNumber--;
        }
        Integer unValidActivities = 0;
        for(Map<String, Object> map: activities){
            //获取活动名称
            String activityName = (String) map.get("name");
            Integer isLesson = (Integer) map.get("type");
            if(acticityMapper.selectIsAvailable(activityName, name, isLesson) >= 1){
                unValidActivities++;
                continue;
            }
            Activity activity = new Activity();
            activity.setName(activityName);
            activity.setFacility(name);
            activity.setIsLesson(isLesson);
            acticityMapper.insertActivity(activity);
        }
        if(unValidActivities != 0){
            resultMap.put("code", 401);
            resultMap.put("message", "Add successfully and duplicate activities have been filtered");
            return resultMap;
        }
        resultMap.put("code", 200);
        resultMap.put("message", "Add successfully");
        return resultMap;
    }

    /**
     * 修改设施
     */
//    public Map<String, Object> changeFacility(Integer fid, String name, Integer capacity){
//        Map<String, Object> resultMap = new HashMap<>();
//        Facility facility = facilityMapper.selectFacilityByFid(fid);
//        int nowCapacity = facility.getCapacity();
////        if(capacity < facility.getNow()){
////            resultMap.put("code", 400);
////            resultMap.put("message", "容量过小");
////            return resultMap;
////        }
//        int result = facilityMapper.updateFacility(fid, name, capacity);
//        if(result > 0){
//            resultMap.put("code", 200);
//            resultMap.put("message", "修改成功");
//        }else{
//            resultMap.put("code", 400);
//            resultMap.put("message", "修改失败");
//        }
//        return resultMap;
//    }

    /**
     * 删除设施
     */
    public Map<String, Object> deleteFacility(Map<String, Object> map){
        Map<String, Object> resultMap = new HashMap<>();
        // 得到需要删除设施名称
        String facilityName = (String) map.get("name");
        //得到当前时间
        LocalDateTime timeNow = LocalDateTime.now();
        //判断该类设施有无有效预约
        if(rentMapper.selectRentsByNameAndTime(facilityName, timeNow) > 0){
            resultMap.put("code", 400);
            resultMap.put("message", "The current facility still has user reservations. Reservations for this facility have been suspended");
            // 关闭设施预约
            facilityMapper.stopFacility(facilityName);
        }else{
            //删除所有数据库相关信息
            rentMapper.deleteRentsByName(facilityName);
            acticityMapper.deleteActivitiesByName(facilityName);
            facilityMapper.deleteFacilitiesByName(facilityName);
            resultMap.put("code", 200);
            resultMap.put("message", "Deletion completed");
        }
        return resultMap;
    }

    /**
     * 删除用户
     */
    public Map<String, Object> deleteUser(Map<String, Object> map){
        Map<String, Object> resultMap = new HashMap<>();
        //得到邮箱
        String email = (String) map.get("email");
        //得到当前时间
        LocalDateTime time = LocalDateTime.now();
        //得到该用户花销
        Integer money = rentMapper.selectMoneyByUserEmail(email, time);
        //删除该用户所有rent
        rentMapper.deleteRentsByUserEmail(email);
        //删除该用户
        userMapper.deleteUserByEmail(email);
        resultMap.put("code", 200);
        resultMap.put("message", "Deleted successfully");
        return resultMap;
    }

    /**
     * 修改用户信息
     */
    public Map<String, Object> changeUserInfo(Map<String, Object> map){
        Map<String, Object> resultMap = new HashMap<>();
        //得到用户邮箱
        String email = (String) map.get("email");
        //得到用户名
        String name = (String) map.get("name");
        //得到用户网银
        Integer money = (Integer) map.get("money");
        //得到密码
        String password = (String) map.get("password");
        // 盐
        String salt = RandomUtil.randomString(6);
        // 加密密码
        String md5Pwd = SecureUtil.md5(password + salt);
        userMapper.changeUserInfo(name, md5Pwd, money, salt,  email);
        resultMap.put("code", 200);
        resultMap.put("message", "Change successfully");
        return resultMap;
    }

    /**
     * 删除用户预约信息
     */
    public Map<String, Object> deleteRent(Map<String, Object> map, String email) {
        Map<String, Object> resultMap = new HashMap<>();
        //得到订单id
        Integer rid = (Integer) map.get("rid");
        //得到当前时间
        LocalDateTime timeNow = LocalDateTime.now();
        //得到订单失效时间
        LocalDateTime limitTime = rentMapper.selectRentTimeByRid(rid);
        if(limitTime == null){
            resultMap.put("code", 400);
            resultMap.put("message", "The order does not exist");
            return resultMap;
        }
        //判断订单是否失效
        if(limitTime.isBefore(timeNow)){
            //订单已经失效
            rentMapper.deleteRentByRid(rid);
            resultMap.put("code", 200);
            resultMap.put("message", "Deleted successfully");
        }else{
            //订单未失效，返还用户全款
            //得到订单价格
            Integer price = rentMapper.selectRentMoneyByRid(rid);
            //得到用户剩余网银
            Integer money = userMapper.selectUserMoneyByEmail(email);
            //更新网银信息
            userMapper.updateUserMoney(price+money, email);
            resultMap.put("code", 200);
            resultMap.put("message", "Deleted successfully and the money has been returned");
        }
        return resultMap;
    }

    /**
     * 改变用户预约信息
     */
    public Map<String, Object> changeRent(@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")Map<String, Object> map, String email) {
        Map<String, Object> resultMap = new HashMap<>();
        //得到订单id
        Integer rid = (Integer) map.get("rid");
        //得到期望设施名称
        String facilityName = (String) map.get("facility");
        //得到期望活动名称
        String activityName = (String) map.get("activity");
        //判断期望活动是否有效
        if(acticityMapper.selectActivityOfFacilityNumber(activityName, facilityName) <= 0){
            resultMap.put("code", 400);
            resultMap.put("message", "This activity is not part of the facility");
            return resultMap;
        }
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        //得到期望开始时间
        LocalDateTime startTime = LocalDateTime.parse((String) map.get("startTime"), df);
        //得到期望结束时间
        LocalDateTime endTime = LocalDateTime.parse((String) map.get("endTime"), df);
        //判断时间是否合法
        if(startTime.isAfter(endTime)){
            resultMap.put("code", 401);
            resultMap.put("message", "Time illegality");
            return resultMap;
        }
        //更新预约信息
        rentMapper.updateRentInfo(facilityName, activityName, startTime, endTime, rid);
        resultMap.put("code", 200);
        resultMap.put("message", "Change Successfully");
        return resultMap;
    }

    /**
     * 重置密码
     */
    public Map<String, Object> resetUserPassword(String email) {
        Map<String, Object> resultMap = new HashMap<>();
        String password = "12345678";
        // 盐
        String salt = RandomUtil.randomString(6);
        // 加密密码: 原始密码 + 盐
        String md5Pwd = SecureUtil.md5(password + salt);
        userMapper.resetUserPassword(md5Pwd, salt, email);
        resultMap.put("code", 200);
        resultMap.put("message", email+ "Reset Successfully");
        return resultMap;
    }

}
