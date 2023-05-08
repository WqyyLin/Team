package com.team.appController;

import com.team.entity.*;
import com.team.mapper.*;
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
import java.sql.Time;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("app")
public class AppSystemController {

    @Resource
    private RentService rentService;
    @Resource
    private FacilityMapper facilityMapper;
    @Resource
    private CardMapper cardMapper;
    @Resource
    private ProjectMapper projectMapper;
    @Resource
    private  RentMapper rentMapper;
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
        List<Map<String, Object>> facilities = facilityMapper.selectAllValidFacility();
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

    @GetMapping("wallet/{email}")
    public @ResponseBody Map<String, Object> walletPage(@PathVariable String email) {
        User u = userMapper.selectOneUserByEmail(email);
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

    @PutMapping("profilePhoto/{email}")
    public @ResponseBody Map<String, Object> userHeadPicture(HttpServletRequest request, @PathVariable String email) throws IOException{
        Map<String, Object> resultMap = new HashMap<>();
        User user = userMapper.selectOneUserByEmail(email);
        if (user == null){
            resultMap.put("code", 400);
            resultMap.put("message", "Please log in first");
            System.out.println(111111);
        }else{
            List<MultipartFile> files = ((MultipartHttpServletRequest) request).getFiles("file");
            if (!new File("/", "picture").exists()){
                new File("/", "picture").mkdirs();
            }
            File file1 = new File("/picture/" + user.getId()+".png");
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
            userMapper.updateUserHeadPhoto("/picture/"+user.getId()+"png", user.getEmail());
            resultMap.put("code", 200);
            resultMap.put("message", "Upload successfully!");
            resultMap.put("picture", "/picture/"+user.getId()+"png");
        }
        return resultMap;
    }

    @GetMapping("activity/{facility}")
    public @ResponseBody Map<String, Object> activityPage(@PathVariable String facility){
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("code", 200);
        List<Map<String, Object>> activities = acticityMapper.selectActivitiesByFacility(facility);
        for (Map<String, Object> activity: activities){
            String activityName = (String) activity.get("name");
            List<Map<String,Object>> projects = projectMapper.selectAllProjectOfOneActivity(facility, activityName);
            activity.put("projects", projects);
        }
        resultMap.put("activity", activities);
        return resultMap;
    }

    @GetMapping("member")
    public @ResponseBody Map<String, Object> memberPage(){
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("code", 200);
        resultMap.put("vip", cardMapper.selectAllMemberCard());
        return resultMap;
    }

    @GetMapping("code/{email}")
    public @ResponseBody Map<String, Object> codePage(@PathVariable String email){
        Map<String, Object> resultMap = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();
        String orderNumber = (String) rentMapper.selectOrderByEmail(now, email).get(0).get("orderNumber");
        resultMap.put("code", 200);
        resultMap.put("order", orderNumber);
        return resultMap;
    }

    @GetMapping("order/{email}")
    public @ResponseBody Map<String, Object> orderPage(@PathVariable String email){
        Map<String, Object> resultMap = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();
        resultMap.put("code", 200);
        resultMap.put("order", rentMapper.selectOrderByEmail(now,email));
        return resultMap;
    }
    @GetMapping("user/book/activity/{aid}")
    public @ResponseBody Map<String, Object> activityProject(@PathVariable Integer aid){
        Map<String, Object> resultMap = new HashMap<>();
        Activity activity = acticityMapper.selectActivityByAid(aid);
        String facility = activity.getFacility();
        String activityName = activity.getName();
        List<Map<String,Object>> projects = projectMapper.selectAllProjectOfOneActivity(facility, activityName);
        resultMap.put("code", 200);
        resultMap.put("project", projects);
        return resultMap;
    }

    @GetMapping("user/book/lesson/{aid}")
    public @ResponseBody Map<String, Object> lessonProject(@PathVariable Integer aid){
        Map<String, Object> resultMap = new HashMap<>();
        Activity activity = acticityMapper.selectActivityByAid(aid);
        String facility = activity.getFacility();
        String activityName = activity.getName();
        List<Map<String,Object>> projects = projectMapper.selectAllProjectOfOneActivity(facility, activityName);
        resultMap.put("code", 200);
        resultMap.put("project", projects);
        return resultMap;
    }

    @GetMapping("lessonInfo/{pid}")
    public @ResponseBody Map<String, Object> getLessonInfo(@PathVariable Integer pid){
        Map<String, Object> resultMap = new HashMap<>();
        Project project = projectMapper.selectProjectByPid(pid);
        resultMap.put("code", 200);
        resultMap.put("project", project);
        return resultMap;
    }

    @GetMapping("user/book/activity")
    public @ResponseBody Map<String, Object> allProjects(){
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("code", 200);
        resultMap.put("project", projectMapper.select());
        return resultMap;
    }

    @GetMapping("user/book/lesson")
    public @ResponseBody Map<String, Object> allLessons(){
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("code", 200);
        resultMap.put("lesson", projectMapper.selectAllLessons());
        return resultMap;
    }

    @GetMapping("order/lesson/{email}")
    public @ResponseBody Map<String, Object> lessonOrder(@PathVariable String email){
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("code", 200);
        resultMap.put("lesson", rentMapper.selectLessonOrder(email));
        return resultMap;
    }

    @GetMapping("calendar/{email}")
    public @ResponseBody Map<String, Object> calendarInfo(@PathVariable String email){
        Map<String, Object> resultMap = new HashMap<>();
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime now = LocalDateTime.now();
        List<Map<String, Object>> all = new ArrayList<>();
        List<Rent> rents = rentMapper.selectValidOrder(email);
        for (Rent rent: rents){
            Map<String, Object> one = new HashMap<>();
            LocalDateTime startTime = rent.getTime();
            one.put("date", df.format(startTime));
            if (Duration.between(now, startTime).toDays() >= 1){
                one.put("type", "success");
            }else if(Duration.between(now, startTime).toMinutes() <= 30){
                one.put("type", "error");
            }else {
                one.put("type", "warning");
            }
            Project project = projectMapper.selectProjectByPid(rent.getPid());
            one.put("content", project.getName());
            if (rent.getIsLesson() == 1){
                one.put("name", "Lesson");
            }else if (rent.getIsLesson() == 0){
                one.put("name", "Activity");
            }else{
                one.put("name", "Special");
            }
            one.put("orderNumber", rent.getOrderNumber());
            all.add(one);
        }
        resultMap.put("code", 200);
        resultMap.put("all", all);
        return resultMap;
    }

}

