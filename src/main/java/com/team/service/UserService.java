package com.team.service;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.SecureUtil;
import com.team.entity.User;
import com.team.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService {

    @Resource
    private UserMapper userMapper;
    @Resource
    private MailService mailService;

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
     *
     * @param user
     * @return
     */
    public Map<String, Object> loginAccount(User user, HttpSession session) {
        Map<String, Object> resultMap = new HashMap<>();
        String status = (String) session.getAttribute("status");
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
            session.setAttribute("status", "manager");
            resultMap.put("ercode", 200);
            resultMap.put("message", "Administrator login successfully!");
            resultMap.put("user", user);
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
        //将user对象存入session里
        session.setAttribute("user",u);
        //将status状态设置为login登录并且存入session
        session.setAttribute("status","login");
        resultMap.put("ercode", 201);
        resultMap.put("message", "Login successful!");
        resultMap.put("user", u);
        return resultMap;
    }

    public Map<String, Object> logoutAccount(HttpSession session){
        Map<String, Object> resultMap = new HashMap<>();
        String status = (String) session.getAttribute("status");
        if(status == null){
            //登出错误
            resultMap.put("code", 400);
            resultMap.put("message", "No account login currently!");
            return resultMap;
        }
        if (status.equals("login")){
            //登出成功
            session.invalidate();
            resultMap.put("code", 200);
            resultMap.put("message", "Successfully log in!");
            return resultMap;
        }else {
            //登出失败
            resultMap.put("code", 400);
            resultMap.put("message", "Failure!");
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

    public Map<String, Object> chargeMoney(Map<String, Object> map, HttpSession session) {
        Map<String, Object> resultMap = new HashMap<>();
        User user = (User) session.getAttribute("user");
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

}
