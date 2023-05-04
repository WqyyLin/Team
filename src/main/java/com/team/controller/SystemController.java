package com.team.controller;

import com.team.entity.Activity;
import com.team.entity.Facility;
import com.team.entity.User;
import com.team.mapper.ActicityMapper;
import com.team.mapper.FacilityMapper;
import com.team.service.RentService;
import com.team.service.UserService;
import org.apache.logging.log4j.util.Base64Util;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class SystemController {

    @Resource
    private RentService rentService;
    @Resource
    private FacilityMapper facilityMapper;
    @Resource
    private ActicityMapper acticityMapper;
    @Resource
    private UserService userService;

    /**
     * 主页
     */
    @GetMapping("mainpage")
    public @ResponseBody Map<String, Object> homePage(){
        Map<String, Object> resultMap = new HashMap<>();
        // 删除无时间限制场地预约信息,理想为每天结束时间删除
        resultMap.put("code", 200);
        resultMap.put("message", "主页");
        return resultMap;
    }

    /**
     * 用户注册界面
     */
    @GetMapping("user/create")
    public @ResponseBody Map<String, Object> userCreatePage(){
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("code", 200);
        resultMap.put("message", "注册页面");
        return resultMap;
    }

    @PostMapping("user/png")
    public void userHeadPicture(HttpServletRequest request) throws IOException{
        List<MultipartFile> files = ((MultipartHttpServletRequest) request).getFiles("file");
        File file1 = new File("h.png");
        System.out.println(file1.getAbsolutePath());
        for (MultipartFile file: files){
            try{
                byte [] bytes = file.getBytes();
                OutputStream out = new FileOutputStream(file1);
                out.write(bytes);
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 用户登录页面
     */
    @GetMapping("user/login")
    public @ResponseBody Map<String, Object> userLoginPage(){
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("code", 200);
        resultMap.put("message", "登录页面");
        return resultMap;
    }

    /**
     * 设施页面
     */
    @GetMapping("facility")
    public @ResponseBody Map<String, Object> rentPage(){
        Map<String, Object> resultMap = new HashMap<>();
        return resultMap;
    }


}
