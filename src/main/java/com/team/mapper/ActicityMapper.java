package com.team.mapper;

import com.team.entity.Activity;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

public interface ActicityMapper {

    /**
     * 查找单个设施对应所有活动
     */
    @Select("SELECT * FROM activity WHERE facility = #{facility}")
    List<Map<String, Object>> selectActivitiesByFacility(@Param("facility") String facility);

    @Select("SELECT count(name) FROM activity where name=#{name} and facility=#{facility} and isLesson=#{isLesson}")
    Integer selectIsAvailable(@Param("name") String name, @Param("facility") String facility, @Param("isLesson") Integer isLesson);

    @Select("SELECT name FROM activity WHERE facility = #{facility}")
    List<Map<String, Object>> selectActivityName(@Param("facility") String facility);

    @Select("SELECT * FROM activity WHERE facility = #{facility}")
    List<Activity> selectActivity(@Param("facility") String facility);

    @Select("SELECT * FROM activity WHERE facility = #{facility}")
    List<Map<String, Object>> selectActivityMap(@Param("facility") String facility);

    @Select("SELECT count(*) FROM team.activity where name= #{name} and facility = #{facility}")
    Integer selectActivityOfFacilityNumber(@Param("name") String name, @Param("facility") String facility);

    /**
     * 查找单个活动
     */
    @Select("SELECT * FROM activity WHERE aid = #{aid}")
    Activity selectActivityByAid(@Param("aid") Integer aid);

    @Insert("INSERT INTO activity (name, facility, isLesson, description) " +
            " VALUES ( #{name}, #{facility}, #{isLesson}, #{description})")
    void insertActivity(Activity activity);

    @Delete("DELETE FROM activity Where facility= #{name}")
    void deleteActivitiesByName(@Param("name") String name);

    @Delete("DELETE FROM activity Where aid= #{aid}")
    void deleteActivityByAid(@Param("aid") Integer aid);

    @Update("UPDATE activity SET name=#{name}, description=#{description} WHERE aid=#{aid}")
    void updateActivity(Activity activity);

}
