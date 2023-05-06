package com.team.tools;

import com.team.entity.Facility;
import com.team.entity.Rent;
import com.team.mapper.FacilityMapper;
import com.team.mapper.ProjectMapper;
import com.team.mapper.RentMapper;
import com.team.mapper.UserMapper;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.security.PublicKey;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ServiceHelper {

    @Resource
    private FacilityMapper facilityMapper;

    @Resource
    private RentMapper rentMapper;

    @Resource
    private UserMapper userMapper;
    @Resource
    private ProjectMapper projectMapper;

    /**
     * 计算某日新注册人数
     * @param time
     * @return
     */
    public int oneDayNewPeopleNumber(@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime time){
        return userMapper.selectNewUsers(time, LocalDateTime.of(time.toLocalDate(), LocalTime.MAX));
    }

    /**
     * 计算某日预约人数
     * @param time
     * @return
     */
    public int oneDayRentPeople(@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime time){
        return rentMapper.selectRentPeopleNumber(time, LocalDateTime.of(time.toLocalDate(), LocalTime.MAX));
    }

    /**
     * 计算i天各自营业额
     * @param time
     * @return
     */
    public List<Double> getMoney(@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime time, int day){
        List<Double> money = new ArrayList<>();
        //获取30天前日期
        LocalDateTime days = time.minusDays(day-1);
        while(days.isBefore(time) || days.equals(time)){
            Integer i = rentMapper.selectDayMoney(days, days.plusDays(1));
            if(i == null){
                i = 0;
            }
            money.add((double)i);
            days = days.plusDays(1);
        }
        return money;
    }

    /**
     * 计算i日增长率
     * @param money
     * @param day
     * @return
     */
    public List<Double> getIncrease(List<Double> money, int day){
        //i日增长率
        List<Double> increase = new ArrayList<>();
        int i = 30-day;
        while(i <= 29){
            if(money.get(i-1) == 0){
                increase.add((double) 0);
                i++;
                continue;
            }
            increase.add((double) (money.get(i)/money.get(i-1)));
            i++;
        }
        return increase;
    }


    /**
     * 计算当日满场率
     */
    public Map<String, Object> todayAttendanceRate(@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime time){
        Map<String, Object> resultMap = new HashMap<>();
        List<Double> rates = new ArrayList<>();
        List<Map<String, Object>> total = new ArrayList<>();
        List<Map<String, Object>> facilities= facilityMapper.selectAllFacility();
        for (Map<String, Object> facility: facilities){
            String facilityName = (String) facility.get("name");
            Integer rentsNum = rentMapper.selectTotalUsedFacilityNumber(time, LocalDateTime.of(time.toLocalDate(), LocalTime.MAX), facilityName);
            Map<String, Object> used = new HashMap<>();
            used.put("name", facilityName);
            Integer occupancy = (Integer) (rentsNum / ((Integer) facility.get("capacity") * (Integer) facility.get("num")))*100;
            if(occupancy > 100) occupancy=100;
            used.put("occupancy", occupancy);
        }
        return resultMap;
    }

    //计算某时间段某设施剩余人数
    public Integer residualNumber(LocalDateTime startTime, LocalDateTime endTime, String facilityName){
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
        startTime = startTime.withYear(9999).withMonth(12).withDayOfMonth(31);
        endTime = endTime.withYear(9999).withMonth(12).withDayOfMonth(31);
        Integer weekLessonNum = projectMapper.usedWeekNumberOfFacility(facilityName, startTime, endTime, weekDay);
        //计算该时段课程人数
        return activityNum+lessonNum+weekLessonNum;
    }

}
