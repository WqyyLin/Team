package com.team.controller;

import com.team.entity.Activity;
import com.team.entity.Facility;
import com.team.entity.Project;
import com.team.entity.User;
import com.team.mapper.*;
import com.team.service.MailService;
import com.team.service.ManagerService;
import com.team.service.UserService;
import com.team.tools.ServiceHelper;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("user/manager")
public class ManagerController {

    @Resource
    private ManagerService managerService;
    @Resource
    private MailService mailService;
    @Resource
    private UserMapper userMapper;
    @Resource
    private FacilityMapper facilityMapper;
    @Resource
    private CardMapper cardMapper;
    @Resource
    private ActicityMapper acticityMapper;
    @Resource
    private ProjectMapper projectMapper;
    @Resource
    private ServiceHelper serviceHelper;
    @Resource
    private UserService userService;
    @Resource
    private RentMapper rentMapper;

    /**
     * 管理员主页面
     */
    @GetMapping("{status}")
    public Map<String, Object> managerPage(@PathVariable String status){
        return managerService.managerPage(status);
    }

    @GetMapping("card")
    public @ResponseBody Map<String, Object> memberPage(){
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("code", 200);
        resultMap.put("vipCard", cardMapper.selectAllMemberCard());
        return resultMap;
    }

    /**
     * 设施管理页面
     */
    @GetMapping("facilities")
    public List<Map<String, Object>> facilitiesInformation(){
        return managerService.facilitiesInformation();
    }

    @PostMapping("facilities")
    public @ResponseBody Map<String, Object> addActivityProject(@RequestBody Map<String, Object> map){
        return managerService.addActivityProject(map);
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
    @PutMapping("facilities")
    public @ResponseBody Map<String, Object> changeFacility(@RequestBody Map<String, Object> map){
        return managerService.changeFacility(map);
    }

    /**
     * 删除设施
     */
    @DeleteMapping("facilities")
    public Map<String, Object> deleteFacility(@RequestBody Map<String, Object> map){
        return managerService.deleteFacility(map);
    }

    @PostMapping("facilities/stop")
    public Map<String, Object> stopFacility(@RequestBody Map<String, Object> map){
        return managerService.stopFacility(map);
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
     * 登录账号
     */
    @PostMapping("login/{status}")
    public Map<String, Object> loginAccount(@RequestBody User user, @PathVariable String status) {
        return managerService.loginAccount(user, status);
    }

    /**
     * 添加卡片
     */
    @PostMapping("card/{status}")
    public @ResponseBody Map<String, Object> addCard(@RequestBody Map<String, Object> map, @PathVariable String status){
        return managerService.addCard(map, status);
    }

    @DeleteMapping("card")
    public @ResponseBody Map<String, Object> deleteCard(@RequestBody Map<String, Object> map){
        return managerService.deleteCard(map);
    }

    @PutMapping("card")
    public @ResponseBody Map<String, Object> changeCard(@RequestBody Map<String, Object> map){
        return managerService.changeCard(map);
    }

    @PostMapping("card/stop")
    public @ResponseBody Map<String, Object> stopCard(@RequestBody Map<String, Object> map){
        return managerService.stopCard(map);
    }

    @PostMapping("book/email/{rid}")
    public @ResponseBody Map<String, Object> orderDetail(@PathVariable Integer rid, @RequestBody Map<String, Object> map){
        return managerService.orderDetail(rid, map);
    }

    @PostMapping("book")
    public @ResponseBody Map<String, Object> bookInfo(@RequestBody Map<String, Object> map) {
        Map<String, Object> resultMap = new HashMap<>();
        String facility = (String) map.get("facility");
        List<Map<String, Object>> activities = acticityMapper.selectActivityMap(facility);
        for (Map<String, Object> activity : activities) {
            activity.remove("money");
            activity.remove("description");
            activity.remove("aid");
            String activityName = (String) activity.get("name");
            List<Map<String, Object>> projects = projectMapper.selectAllProjectOfOneActivity(facility, activityName);
            if ((Integer) activity.get("isLesson") == 0) {
                for (Map<String, Object> project : projects) {
                    project.remove("dayOfWeek");
                    project.remove("activity");
                    project.remove("isWeekly");
                    project.remove("description");
                    DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    String startTime = df.format((LocalDateTime) project.get("startTime")).replace(" ", "-");
                    String endTime = df.format((LocalDateTime) project.get("endTime")).replace(" ", "-");
                    project.put("time", startTime + "--->" + endTime);
                    project.remove("startTime");
                    project.remove("endTime");
                    project.remove("facility");
                    project.remove("isLesson");
                    project.remove("pid");

                }
                activity.put("projects", projects);
            } else {
                for (Map<String, Object> project : projects) {
                    String[] week = ((String) project.get("dayOfWeek")).split(",");
                    String trueWeek = "";
                    for (String w : week) {
                        switch (w) {
                            case "1" -> trueWeek = trueWeek + "MON ";
                            case "2" -> trueWeek = trueWeek + "TUE ";
                            case "3" -> trueWeek = trueWeek + "WED ";
                            case "4" -> trueWeek = trueWeek + "THU ";
                            case "5" -> trueWeek = trueWeek + "FRI ";
                            case "6" -> trueWeek = trueWeek + "SAT ";
                            case "7" -> trueWeek = trueWeek + "SUN ";
                        }
                    }
                    project.remove("dayOfWeek");
                    project.remove("activity");
                    project.remove("isWeekly");
                    project.remove("description");
                    project.remove("facility");
                    project.remove("isLesson");
                    project.remove("pid");
                    SimpleDateFormat df = new SimpleDateFormat("KKaa",Locale.ENGLISH);
                    ZonedDateTime zdt = LocalDateTime.now().atZone(ZoneId.systemDefault());
                    String startTime = df.format(Date.from(((LocalDateTime) project.get("startTime")).atZone(ZoneId.systemDefault()).toInstant()));
                    String endTime = df.format(Date.from(((LocalDateTime) project.get("endTime")).atZone(ZoneId.systemDefault()).toInstant()));
                    project.put("time", trueWeek + startTime + " - " + endTime);
                    project.remove("startTime");
                    project.remove("endTime");
                }
                activity.put("projects", projects);
            }
        }
        resultMap.put("activities", activities);
        return resultMap;
    }

    @PostMapping("surplus")
    public @ResponseBody Map<String, Object> residualNumber(@RequestBody Map<String, Object> map) {
        Map<String, Object> resultMap = new HashMap<>();
        String facilityName = (String) map.get("sitename");
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startTime = LocalDateTime.parse((String)map.get("starttime"), df) ;
        LocalDateTime endTime = LocalDateTime.parse((String)map.get("endtime"), df);
        Integer used = serviceHelper.residualNumber(startTime, endTime, facilityName);
        Integer all = facilityMapper.selectCapacity(facilityName);
        if(all > used){
            resultMap.put("code", 200);
            resultMap.put("activities", all - used);
        }else{
            resultMap.put("code", 400);
            resultMap.put("message", "The current facility is full");
        }
        return resultMap;
    }

    @PostMapping("facility/book")
    public @ResponseBody Map<String, Object> book(@RequestBody Map<String, Object> map) {
        Map<String, Object> resultMap = new HashMap<>();
        String facilityName = (String) map.get("sitename");
        String activityName = (String) map.get("activity");
        String name = (String) map.get("name");
        Integer isLesson = Integer.parseInt((String) map.get("lesson"));
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startTime = LocalDateTime.parse((String) map.get("startTime"), df);
        LocalDateTime endTime = LocalDateTime.parse((String) map.get("endTime"), df);
        Integer duration = Math.toIntExact(Duration.between(startTime, endTime).toHours());
        Integer pid = projectMapper.selectPid(name, facilityName, activityName, isLesson);
        Map<String, Object> target = new HashMap<>();
        target.put("pid", pid);
        if (isLesson == 0){
            target.put("startTime", df.format(startTime));
            target.put("duration", duration);
            target.put("num", Integer.parseInt((String) map.get("total_people")));
            resultMap = userService.bookActivity(target, null);
        }else{
            target.put("num", Integer.parseInt((String) map.get("total_course")));
            target.put("people", Integer.parseInt((String) map.get("total_people")));
            resultMap = userService.bookLesson(target, null);
        }
        String email = (String) map.get("email");
        if (email!=null){
            mailService.sendMailForOrder(rentMapper.selectRentByRid(rentMapper.getRidByOrder((String) resultMap.get("orderNumber"))), email);
        }
        return resultMap;
    }

    @GetMapping("all")
    public @ResponseBody Map<String, Object> allInformation(){
        return managerService.allInformation();
    }

    @GetMapping("rate")
    public @ResponseBody Map<String, Object> todayAttendanceRate(){
        return serviceHelper.todayAttendanceRate(LocalDateTime.of(LocalDate.now(), LocalTime.MIN));
    }

    @PostMapping("time")
    public @ResponseBody Map<String, Object> validTime(@RequestBody Map<String, Object> map){
        Map<String, Object> resultMap = new HashMap<>();
        String facilityName = (String) map.get("Sitename");
        Integer duration = (Integer) map.get("Last");
        Integer capacity = (Integer) map.get("min_place");
        Integer totalCapacity = facilityMapper.selectCapacity(facilityName);
        Facility facility = facilityMapper.selectAllFacilityOfOneName(facilityName).get(0);
        LocalTime startTimeOfFacility = facility.getStartTime();
        LocalTime endTimeOfFacility = facility.getEndTime();
        List<LocalTime> timeList = new ArrayList<>();
        LocalDate now = LocalDate.now().plusDays(1);
        LocalDate end = now.plusDays(3);
        List<Map<String, Object>> mapList = new ArrayList<>();
        while (startTimeOfFacility.isBefore(endTimeOfFacility)){
            timeList.add(startTimeOfFacility);
            startTimeOfFacility = startTimeOfFacility.plusHours(1);
        }
        while (now.isBefore(end)){
            for (LocalTime time: timeList){
                LocalDateTime startTime = LocalDateTime.of(now, time);
                LocalDateTime endTime = startTime.plusHours(duration);
                Integer used = serviceHelper.residualNumber(startTime, endTime, facilityName);
                if (used + capacity <= totalCapacity){
                    Map<String, Object> t = new HashMap<>();
                    t.put("starttime", startTime);
                    t.put("endtime", endTime);
                    mapList.add(t);
                }
            }
            now = now.plusDays(1);
        }
        if (mapList.isEmpty()){
            resultMap.put("code", 400);
            resultMap.put("message", "No effective time!");
        }else{
            resultMap.put("code", 200);
            resultMap.put("time", mapList);
            resultMap.put("message", "Successfully!");
        }
        return resultMap;
    }

    @DeleteMapping("projects")
    public @ResponseBody Map<String, Object> deleteProjects(@RequestBody Map<String, Object> map){
        Map<String, Object> resultMap = new HashMap<>();
        Integer pid = (Integer) map.get("pid");
        Integer num = rentMapper.usedNumberOfProject(pid, LocalDateTime.now(), LocalDateTime.of(9999, 12, 31, 23, 59, 59));
        if (num > 0){
            resultMap.put("code", 400);
            resultMap.put("message", "This facility cannot be deleted due to an existing appointment!");
        }else{
            projectMapper.deleteProjectByPid(pid);
            resultMap.put("code", 200);
            resultMap.put("message", "Successfully!");
        }
        return resultMap;
    }

    @PutMapping("projects")
    public @ResponseBody Map<String, Object> stopProjects(@RequestBody Map<String, Object> map){
        Map<String, Object> resultMap = new HashMap<>();
        Integer pid = (Integer) map.get("pid");
        Project project = projectMapper.selectByPid(pid);
        if (project.getValid() == 0) {
            project.setValid(1);
            projectMapper.stopProject(project);
        }else if (project.getValid() == 1){
            project.setValid(0);;
            projectMapper.stopProject(project);
        }
        resultMap.put("code",200);
        resultMap.put("message","Change Successfully");
        return resultMap;
    }

    @PostMapping("allTime")
    public @ResponseBody Map<String, Object> allTime(@RequestBody Map<String, Object> map){
        Map<String, Object> resultMap = new HashMap<>();
        String start = (String) map.get("starttime");
        String end = (String) map.get("endtime");
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startTime = LocalDateTime.parse(start, df);
        LocalDateTime endTime = LocalDateTime.parse(end, df);
        List<Map<String, Object>> facilities = facilityMapper.selectAllFacility();
        List<Map<String, Object>> mapList = new ArrayList<>();
        for (Map<String, Object> facility: facilities){
            String facilityName = (String) facility.get("name");
            LocalDateTime facilityStart = LocalDateTime.of(startTime.toLocalDate(), ((Time) facility.get("startTime")).toLocalTime());
            LocalDateTime facilityEnd = LocalDateTime.of(startTime.toLocalDate(), ((Time) facility.get("endTime")).toLocalTime());
            if (!(startTime.isAfter(facilityEnd) || endTime.isBefore(facilityStart))){
                Map<String, Object> use = new HashMap<>();
                use.put("name", facilityName);
                DateTimeFormatter df2 = DateTimeFormatter.ofPattern("HH:mm:ss");
                if (startTime.isEqual(facilityStart)){
                    use.put("starttime", df2.format(startTime));
                }else if (startTime.isAfter(facilityStart)){
                    use.put("starttime", df2.format(startTime));
                }else {
                    use.put("starttime", df2.format(facilityStart));
                }
                if (endTime.isEqual(facilityEnd)){
                    use.put("endtime", df2.format(endTime));
                }else if (endTime.isBefore(facilityEnd)){
                    use.put("endtime", df2.format(endTime));
                }else {
                    use.put("endtime", df2.format(facilityEnd));
                }
                mapList.add(use);
            }
        }
        resultMap.put("all", mapList);
        return resultMap;
    }

    @PostMapping("facilityTime")
    public @ResponseBody Map<String, Object> facilityTime(@RequestBody Map<String, Object> map){
        Map<String, Object> resultMap = new HashMap<>();
        String facility = (String) map.get("facility");
        String start = (String) map.get("starttime");
        String end = (String) map.get("endtime");
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startTime = LocalDateTime.parse(start, df);
        LocalDateTime endTime = LocalDateTime.parse(end, df);
        List<Map<String, Object>> projects = projectMapper.selectAll();
        List<Map<String, Object>> mapList = new ArrayList<>();
        for (Map<String, Object> project: projects){
            Integer isLesson = (Integer) project.get("isLesson");
            List<String> startTimeList = new ArrayList<>();
            List<String> endTimeList = new ArrayList<>();
            Map<String, Object> use = new HashMap<>();
            if (isLesson == 0){
                LocalDateTime projectStart = LocalDateTime.of(startTime.toLocalDate(), ((LocalDateTime) project.get("startTime")).toLocalTime());
                LocalDateTime projectEnd = LocalDateTime.of(startTime.toLocalDate(), ((LocalDateTime) project.get("endTime")).toLocalTime());
                if (!(startTime.isAfter(projectEnd) || endTime.isBefore(projectStart))){
                    DateTimeFormatter df2 = DateTimeFormatter.ofPattern("HH:mm:ss");
                    if (startTime.isEqual(projectStart)){
                        startTimeList.add(df2.format(startTime));
                    }else if (startTime.isAfter(projectStart)){
                        startTimeList.add(df2.format(startTime));
                    }else {
                        startTimeList.add(df2.format(projectStart));
                    }
                    if (endTime.isEqual(projectEnd)){
                        endTimeList.add(df2.format(endTime));
                    }else if (endTime.isBefore(projectEnd)){
                        endTimeList.add(df2.format(endTime));
                    }else {
                        endTimeList.add(df2.format(projectEnd));
                    }
                }
            }else {
                Integer isWeekly = (Integer) project.get("isWeekly");
                if (isLesson == 1 && isWeekly == 1){
                    String day = startTime.getDayOfWeek().toString();
                    switch (day) {
                        case "MONDAY" -> day = "1";
                        case "TUESDAY" -> day = "2";
                        case "WEDNESDAY" -> day = "3";
                        case "THURSDAY" -> day = "4";
                        case "FRIDAY" -> day = "5";
                        case "SATURDAY" -> day = "6";
                        case "SUNDAY" -> day = "7";
                    }
                    if (projectMapper.validLesson(day, (Integer) project.get("pid")) > 0){
                        LocalDateTime projectStart = LocalDateTime.of(startTime.toLocalDate(), ((LocalDateTime) project.get("startTime")).toLocalTime());
                        LocalDateTime projectEnd = LocalDateTime.of(startTime.toLocalDate(), ((LocalDateTime) project.get("endTime")).toLocalTime());
                        if (!(startTime.isAfter(projectEnd) || endTime.isBefore(projectStart))){
                            DateTimeFormatter df2 = DateTimeFormatter.ofPattern("HH:mm:ss");
                            if (startTime.isEqual(projectStart)){
                                startTimeList.add(df2.format(startTime));
                            }else if (startTime.isAfter(projectStart)){
                                startTimeList.add(df2.format(startTime));
                            }else {
                                startTimeList.add(df2.format(projectStart));
                            }
                            if (endTime.isEqual(projectEnd)){
                                endTimeList.add(df2.format(endTime));
                            }else if (endTime.isBefore(projectEnd)){
                                endTimeList.add(df2.format(endTime));
                            }else {
                                endTimeList.add(df2.format(projectEnd));
                            }
                        }
                    }
                }else{
                    LocalDateTime projectStart = (LocalDateTime) project.get("startTime");
                    LocalDateTime projectEnd = (LocalDateTime) project.get("endTime");
                    if (!(startTime.isAfter(projectEnd) || endTime.isBefore(projectStart))){
                        DateTimeFormatter df2 = DateTimeFormatter.ofPattern("HH:mm:ss");
                        if (startTime.isEqual(projectStart)){
                            startTimeList.add(df2.format(startTime));
                        }else if (startTime.isAfter(projectStart)){
                            startTimeList.add(df2.format(startTime));
                        }else {
                            startTimeList.add(df2.format(projectStart));
                        }
                        if (endTime.isEqual(projectEnd)){
                            endTimeList.add(df2.format(endTime));
                        }else if (endTime.isBefore(projectEnd)){
                            endTimeList.add(df2.format(endTime));
                        }else {
                            endTimeList.add(df2.format(projectEnd));
                        }
                    }
                }
            }
            if (!startTimeList.isEmpty()){
                use.put("name", (String) project.get("name"));
                use.put("activity", project.get("activity"));
                use.put("starttime", startTimeList);
                use.put("endtime", endTimeList);
                mapList.add(use);
            }
        }
        resultMap.put("all", mapList);
        return resultMap;
    }

    @PutMapping("facilityPhoto/{email}")
    public @ResponseBody Map<String, Object> facilityPicture(HttpServletRequest request, @PathVariable String email) throws IOException {
        Map<String, Object> resultMap = new HashMap<>();
        User user = userMapper.selectOneUserByEmail(email);
        if (user == null){
            resultMap.put("code", 400);
            resultMap.put("message", "Please log in first");
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
            userMapper.updateUserHeadPhoto("/picture/"+user.getId()+".png", user.getEmail());
            resultMap.put("code", 200);
            resultMap.put("message", "Upload successfully!");
        }
        return resultMap;
    }

    @PostMapping("picture/facility/{name}")
    public void facilityPicture(@RequestParam("file") MultipartFile picture, @PathVariable String name) throws IOException {
        Facility facility = facilityMapper.selectAllFacilityOfOneName(name).get(0);
        String filename = facility.getName()+".png";
        String directory = "./";
        Path filePath = Paths.get(directory, filename);
        Files.createDirectories(filePath.getParent());
        Files.write(filePath, picture.getBytes());
        facility.setPicture(directory + "facility" + filename);
        facilityMapper.setPicture(facility);
    }

    @PostMapping("picture/activity/{aid}")
    public void activityPicture(@RequestParam("file") MultipartFile picture, @PathVariable Integer aid) throws IOException {
        Activity activity = acticityMapper.selectActivityByAid(aid);
        String filename = activity.getName()+".png";
        String directory = "./";
        Path filePath = Paths.get(directory, filename);
        Files.createDirectories(filePath.getParent());
        Files.write(filePath, picture.getBytes());
        activity.setPicture(directory + "facility" + filename);
        acticityMapper.setPicture(activity);
    }


}
