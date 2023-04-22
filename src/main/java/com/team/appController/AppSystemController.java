package com.team.appController;

import com.team.entity.Activity;
import com.team.entity.Facility;
import com.team.entity.User;
import com.team.mapper.ActicityMapper;
import com.team.mapper.FacilityMapper;
import com.team.mapper.ProjectMapper;
import com.team.mapper.UserMapper;
import com.team.service.RentService;
import com.team.service.UserService;
import org.apache.logging.log4j.util.Base64Util;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@RestController
@RequestMapping("app")
public class AppSystemController {

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

    @GetMapping("wallet")
    public @ResponseBody Map<String, Object> walletPage(HttpSession session) {
        User u = (User) session.getAttribute("user");
        Map<String, Object> resultMap = new HashMap<>();
        if(u == null){
            resultMap.put("code", 400);
            resultMap.put("money", "Please log in first");
        }else{
            Integer money = userMapper.selectUserMoneyByEmail(u.getEmail());
            resultMap.put("code", 200);
            resultMap.put("money", money);
        }
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

}

