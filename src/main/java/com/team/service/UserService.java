package com.team.service;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.SecureUtil;
import com.team.entity.Card;
import com.team.entity.Project;
import com.team.entity.Rent;
import com.team.entity.User;
import com.team.mapper.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService {

    @Resource
    private UserMapper userMapper;
    @Resource
    private MailService mailService;
    @Resource
    private CardMapper cardMapper;
    @Resource
    private ProjectMapper projectMapper;
    @Resource
    private FacilityMapper facilityMapper;
    @Resource
    private RentMapper rentMapper;

    @Value("${manager.email}")
    private String email;

    @Value("${manager.password}")
    private String password;

    /**
     * 注册账号
     */
    @Transactional
    public Map<String, Object> createAccount(User user) {
        //根据邮箱查询用户
       List<User> userList = userMapper.selectUserByEmail(user.getEmail());
       Map<String, Object> resultMap = new HashMap<>();
       if (userList == null || userList.isEmpty()) {
           // 雪花算法生成确认码
           String confirmCode = IdUtil.getSnowflake(1, 1).nextIdStr();
           // 盐
           String salt = RandomUtil.randomString(6);
           // 加密密码: 原始密码 + 盐
           String md5Pwd = SecureUtil.md5(user.getPassword() + salt);
           // 激活生效时间
           LocalDateTime ldt = LocalDateTime.now().plusDays(1);
           // 初始化账号信息
           user.setSalt(salt);
           user.setPassword(md5Pwd);
           user.setConfirmCode(confirmCode);
           user.setActivationTime(ldt);
           user.setIsValid((byte) 0);
           //新增账号
           int result = userMapper.insertUser(user);
           if (result > 0) {
               //发送邮件
               String activationUrl = "http://localhost:8080/user/activation?confirmCode="+confirmCode;
               mailService.sendMailForActivationAccount(activationUrl, user.getEmail());
               resultMap.put("code", 200);
               resultMap.put("message", "Register successfully, please go to the mailbox for account activation!");
               resultMap.put("confirmCode", confirmCode);
           } else {
               resultMap.put("code", 400);
               resultMap.put("message", "Registration failed!");
           }
       }else{
           resultMap.put("code", 400);
           resultMap.put("message", "The user has registered!");
       }
       return resultMap;
    }

    /**
     * 登录账号
     */
    public Map<String, Object> loginAccount(User user, String status) {
        Map<String, Object> resultMap = new HashMap<>();
        //判断是否已有用户登录
        if(status != null){
            if (status.equals("login")){
                resultMap.put("ercode", 400);
                resultMap.put("message", "Existing users log in!");
                return resultMap;
            }else if (status.equals("manager")){
                resultMap.put("ercode", 300);
                resultMap.put("message", "Administrator has logged in!");
                return resultMap;
            }
        }
        if (user.getEmail().equals(email) && user.getPassword().equals(password)){
            resultMap.put("ercode", 200);
            resultMap.put("message", "Administrator login successfully!");
            resultMap.put("user", user);
            resultMap.put("status", "manager");
            return resultMap;
        }
        // 根据邮箱查询用户
        List<User> userList = userMapper.selectUserByEmail(user.getEmail());
        // 查询不到结果，返回：该用户不存在或未激活
        if (userList == null || userList.isEmpty()) {
            resultMap.put("ercode", 401);
            resultMap.put("message", "The user does not exist or is not activated!");
            return resultMap;
        }
        // 查询到多个结果：返回：账号异常，请联系管理员
        if (userList.size() > 1) {
            resultMap.put("ercode", 402);
            resultMap.put("message", "Account is abnormal, please contact the administrator!");
            return resultMap;
        }
        // 查询到一个用户， 进行密码比对
        User u = userList.get(0);
        // 用户输入的密码和盐进行加密
        String md5Pwd = SecureUtil.md5(user.getPassword() + u.getSalt());
        // 密码不一致，返回：用户名或密码错误
        if (!u.getPassword().equals(md5Pwd)) {
            resultMap.put("ercode", 403);
            resultMap.put("message", "Wrong user name or password!");
            return resultMap;
        }
        resultMap.put("status","login");
        resultMap.put("ercode", 201);
        resultMap.put("message", "Login successful!");
        resultMap.put("user", u);
        return resultMap;
    }

    public Map<String, Object> logoutAccount(String status){
        Map<String, Object> resultMap = new HashMap<>();
        if (status.equals("login")){
            //登出成功
            resultMap.put("code", 200);
            resultMap.put("message", "Successfully log out!");
            resultMap.put("status", "Logout");
            return resultMap;
        }else {
            //登出失败
            resultMap.put("code", 400);
            resultMap.put("message", "Something Failure!");
            return resultMap;
        }
    }

    /**
     * 激活账号
     * @param confirmCode
     * @return
     */
    @Transactional
    public Map<String, Object> activationAccound(String confirmCode) {
        Map<String, Object> resultMap = new HashMap<>();
        //根据确认码查询用户
        User user = userMapper.selectUserByConfirmCode(confirmCode);
        if (user == null){
            resultMap.put("code", 400);
            resultMap.put("message", "Code error!");
            return resultMap;
        }
        // 根据邮箱查询用户
        List<User> userList = userMapper.selectUserByEmail(user.getEmail());
        if (userList == null || userList.isEmpty()) {
            //判断激活时间是否超时
            boolean after = LocalDateTime.now().isAfter(user.getActivationTime());
            if(after){
                resultMap.put("code", 400);
                resultMap.put("message", "The link has failed, please register again!");
                return resultMap;
            }
            //根据确认码查询用户并修改状态值为1（可用）
            int result = userMapper.updateUserByConfirmCode(confirmCode);
            if (result > 0){
                resultMap.put("code", 200);
                resultMap.put("message", "Active!");
            }else{
                resultMap.put("code", 400);
                resultMap.put("message", "Activation fails!");
            }
            userMapper.deleteRepeatUser(user.getEmail());
        }else{
            resultMap.put("code", 400);
            resultMap.put("message", "The user has registered!");
        }
        return resultMap;
    }

    public Map<String, Object> book(String facility, Map<String, Object> map, HttpSession session) {
        Map<String, Object> resultMap = new HashMap<>();
        //获取activity
        String activityName = (String) map.get("activity");
        //获取项目类型
        Integer type = (Integer) map.get("lesson");
        //获取项目名称
        String projectName =(String) map.get("name");
        //获取时间
        String time = (String) map.get("time");
        //获取订课数量
        Integer lessonNumber;
        if(type == 1){
            lessonNumber = (Integer) map.get("total_course");
        }
        //获取人头
        Integer peopleNumber = (Integer) map.get("total_people");
        resultMap.put("code", 200);
        resultMap.put("message", "Book successfully");
        resultMap.put("facility", map);
        return resultMap;
    }

    public Map<String, Object> chargeMoney(Map<String, Object> map, String email) {
        Map<String, Object> resultMap = new HashMap<>();
        User user = userMapper.selectOneUserByEmail(email);
        if(user == null){
            resultMap.put("code", 401);
            resultMap.put("message", "Please log in first!");
        }else{
            Integer money = (Integer) map.get("money");
            if(money <= 0){
                resultMap.put("code", 400);
                resultMap.put("message", "Incorrect top-up amount!");
            }else{
                resultMap.put("code", 200);
                resultMap.put("message", "Charge successfully");
                userMapper.updateUserMoney(userMapper.selectUserMoneyByEmail(user.getEmail())+money, user.getEmail());
            }
        }
        return resultMap;
    }

    public Map<String, Object> inMember(Map<String, Object> map, String email) {
        Map<String, Object> resultMap = new HashMap<>();
        Integer cid = (Integer) map.get("cid");
        User u = userMapper.selectOneUserByEmail(email);
        Integer money = u.getMoney();
        Card card = cardMapper.selectCardByCid(cid);
        if(card.getMoney()>money){
            resultMap.put("code", 400);
            resultMap.put("message", "The balance is insufficient, please recharge first!");
            return resultMap;
        }
        if(cardMapper.selectCardNumOfUser(cid, u.getId()) > 0){
            LocalDateTime time = cardMapper.selectCardTimeOfUser(cid, u.getId());
            LocalDateTime endTime = time.plusDays(card.getTime());
            cardMapper.updateUserCard(cid, u.getId(), endTime);
            userMapper.updateUserMember(money-card.getMoney(), email);
            resultMap.put("message", "Successful renewal");
        }else{
            LocalDateTime endTime = LocalDateTime.now().withMinute(0).withSecond(0).plusDays(card.getTime()).plusHours(1);
            cardMapper.insertCardOfUser(cid, u.getId(), endTime);
            userMapper.updateUserMember(money-card.getMoney(), email);
            resultMap.put("message", "Successful purchase");
        }
        resultMap.put("code", 200);
        return resultMap;
    }

    //计算某时间段某设施剩余人数
    Integer residualNumber(LocalDateTime startTime, LocalDateTime endTime, String facilityName){
        //计算该时段活动人数
        Integer activityNum = rentMapper.usedNumberOfFacility(facilityName, startTime, endTime);
        String day = startTime.getDayOfWeek().toString();
        Integer lessonNum = projectMapper.usedNumberOfFacility(facilityName, startTime, endTime);
        Integer weekDay = 0;
        switch (day) {
            case "MONDAY" -> weekDay = 1;
            case "TUESDAY" -> weekDay = 2;
            case "WEDNESDAY" -> weekDay = 3;
            case "THURSDAY" -> weekDay = 4;
            case "FRIDAY" -> weekDay = 5;
            case "SATURDAY" -> weekDay = 6;
            case "SUNDAY" -> weekDay = 7;
        }
        startTime = startTime.withDayOfYear(9999).withMonth(12).withDayOfMonth(31);
        endTime = endTime.withDayOfYear(9999).withMonth(12).withDayOfMonth(31);
        Integer weekLessonNum = projectMapper.usedWeekNumberOfFacility(facilityName, startTime, endTime, weekDay);
        //计算该时段课程人数
        return activityNum+lessonNum+weekLessonNum;
    }

    public Map<String, Object> bookActivity(Map<String, Object> map, String email) {
        Map<String, Object> resultMap = new HashMap<>();
        Integer pid = (Integer) map.get("pid");
        Project project = projectMapper.selectProjectByPid(pid);
        String facility = project.getFacility();
        Integer isLesson = project.getIsLesson();
        //小时单价
        Integer oneMoney = project.getMoney();
        //持续时间
        Integer duration = (Integer) map.get("duration");
        //购买票数
        Integer num = (Integer) map.get("num");
        //总价
        Integer money = duration*num*oneMoney;
        //设施容量
        Integer capacity = facilityMapper.selectCapacity(facility);
        //设置时区
        ZoneId zoneId = ZoneId.systemDefault();
        //项目开始时间
        LocalDateTime startTime;
        //项目结束时间
        LocalDateTime endTime;
        //获取时间
        SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            startTime = LocalDateTime.ofInstant(formatter.parse((String) map.get("startTime")).toInstant(), zoneId);
            endTime = startTime.plusHours(duration);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        Integer usedCapacity = residualNumber(startTime, endTime, facility);
        if (capacity >= (usedCapacity+num)){
            User user = userMapper.selectOneUserByEmail(email);
            Integer userMoney = user.getMoney();
            int discount=10;
            for (Integer cid: cardMapper.selectCidById(user.getId())){
                Integer now = cardMapper.getDiscount(cid);
                if(discount > now){
                    discount = now;
                }
            }
            money = money*discount/10;
            if(userMoney >= money){
                userMapper.updateUserMoney(userMoney-money, email);
                Rent r = new Rent();
                LocalDateTime now = LocalDateTime.now();
                r.setRentTime(now);
                r.setMoney(money);
                r.setTime(startTime);
                r.setIsLesson(isLesson);
                r.setEmail(email);
                r.setLimitTime(endTime);
                r.setNum(num);
                r.setPid(pid);
                r.setFacility(facility);
                String orderNumber = user.getId().toString() + money + pid + num+
                        now.getYear() + now.getMonth() + now.getDayOfMonth() + now.getHour() + now.getMinute()+
                        now.getSecond();
                r.setOrderNumber(orderNumber);
                rentMapper.insertRent(r);
                resultMap.put("code", 200);
                resultMap.put("message", "Successful appointment!");
            }else{
                resultMap.put("code", 401);
                resultMap.put("message", "Balance is insufficient, please recharge it first!");
            }
        }else{
            resultMap.put("code", 400);
            resultMap.put("message", "The capacity of the facility has been exceeded!");
        }
        return resultMap;
    }

    public Map<String, Object> bookLesson(Map<String, Object> map, String email) {
        Map<String, Object> resultMap = new HashMap<>();
        Integer pid = (Integer) map.get("pid");
        Project project = projectMapper.selectProjectByPid(pid);
        String facility = project.getFacility();
        LocalDateTime startTime = LocalDateTime.of(LocalDate.now(), project.getStartTime().toLocalTime());
        LocalDateTime endTime = LocalDateTime.of(LocalDate.now(), project.getEndTime().toLocalTime());
        Integer isLesson = project.getIsLesson();
        Integer oneMoney = project.getMoney();
        Integer num = (Integer) map.get("num");
        Integer money = num*oneMoney;
        Integer allCapacity = project.getCapacity();
        Integer usedCapacity = rentMapper.numOfProject(pid);
        if(allCapacity > usedCapacity){
            User user = userMapper.selectOneUserByEmail(email);
            Integer userMoney = user.getMoney();
            int discount=10;
            for (Integer cid: cardMapper.selectCidById(user.getId())){
                Integer now = cardMapper.getDiscount(cid);
                if(discount > now){
                    discount = now;
                }
            }
            money = money*discount/10;
            if(userMoney >= money){
                userMapper.updateUserMoney(userMoney-money, email);
                Rent r = new Rent();
                LocalDateTime now = LocalDateTime.now();
                r.setRentTime(now);
                r.setMoney(money);
                r.setTime(startTime);
                r.setIsLesson(isLesson);
                r.setEmail(email);
                r.setLimitTime(endTime);
                r.setNum(num);
                r.setPid(pid);
                r.setFacility(facility);
                String orderNumber = user.getId().toString() + money + pid + num+
                        now.getYear() + now.getMonth() + now.getDayOfMonth() + now.getHour() + now.getMinute()+
                        now.getSecond();
                r.setOrderNumber(orderNumber);
                rentMapper.insertRent(r);
                resultMap.put("code", 200);
                resultMap.put("message", "Successful appointment!");
            }else{
                resultMap.put("code", 401);
                resultMap.put("message", "Balance is insufficient, please recharge it first!");
            }
        }else {
            resultMap.put("code", 400);
            resultMap.put("message", "The capacity of the lesson has been exceeded!");
        }
        return resultMap;
    }

    public Map<String, Object> getOrder(String orderNumber) {
        Map<String, Object> resultMap = new HashMap<>();
        Integer rid = rentMapper.getRidByOrder(orderNumber);
        if (rid == null){
            resultMap.put("code", 400);
            resultMap.put("message", "The order does not exist!");
        }else{
            rentMapper.updateBookStatus(orderNumber);
            resultMap.put("code", 200);
            resultMap.put("message", "Successfully!");
        }
        return resultMap;
    }

    public Map<String, Object> orderDetail(Integer rid) {
        Map<String, Object> resultMap = new HashMap<>();
        Rent r = rentMapper.selectRentByRid(rid);
        if (r == null){
            resultMap.put("code", 400);
            resultMap.put("message", "The order is too old and has been deleted!");
        }else{
            mailService.sendMailForOrder(r, r.getEmail());
            resultMap.put("code", 200);
            resultMap.put("message", "Register successfully, please go to the mailbox for account activation!");
        }
        return resultMap;
    }

    public Map<String, Object> deleteOrder(Integer rid) {
        Map<String, Object> resultMap = new HashMap<>();
        Rent r = rentMapper.selectRentByRid(rid);
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(r.getTime().plusHours(2))){
            resultMap.put("code", 400);
            resultMap.put("message", "It exceeds the refundable time!");
        }else{
            Integer money = r.getMoney();
            User u = userMapper.selectOneUserByEmail(r.getEmail());
            userMapper.updateUserMoney(u.getMoney()+money, u.getEmail());
            rentMapper.updateDeleteOrder(r.getRid());
            resultMap.put("code", 200);
            resultMap.put("message", "Cancel successfully!");
        }
        return resultMap;
    }
}
