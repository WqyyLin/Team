package com.team.tools;

import com.team.mapper.FacilityMapper;
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
import java.util.List;

@Service
public class ServiceHelper {

    @Resource
    private FacilityMapper facilityMapper;

    @Resource
    private RentMapper rentMapper;

    @Resource
    private UserMapper userMapper;

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

    //给错了可能，记得找刘瑞杰对应一下
    /**
     * 计算当日满场率
     */
    public Double todayAttendanceRate(@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime time){
        return (double)rentMapper.selectTotalUsedFacilityNumber(time, LocalDateTime.of(time.toLocalDate(), LocalTime.MAX))/ facilityMapper.selectFacilityNumber();
    }

}
