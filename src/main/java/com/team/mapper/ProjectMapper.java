package com.team.mapper;

import com.team.entity.Activity;
import com.team.entity.Facility;
import com.team.entity.Project;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.BlobTypeHandler;

import java.sql.Blob;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface ProjectMapper {

    @Insert("INSERT INTO project (name, facility, activity, money, startTime, endTime," +
            " description, isWeekly, dayOfWeek, capacity, isLesson) " +
            " VALUES ( #{name}, #{facility}, #{activity}, #{money}, #{startTime}, #{endTime}, " +
            "#{description}, #{isWeekly}, #{weekDay}, #{capacity}, #{isLesson})")
    void insertProject(Project project);

    @Select("SELECT count(name) FROM project where name=#{name} and facility=#{facility} and activity=#{activity}")
    Integer selectIsAvailable(@Param("name") String name, @Param("facility") String facility, @Param("activity")String activity);

    @Select("SELECT count(*) FROM project WHERE find_in_set(#{day}, dayOfWeek) And pid=#{pid}")
    Integer validLesson(@Param("day") String day, @Param("pid") Integer pid);


    @Select("SELECT * FROM project WHERE pid=#{pid} and valid=1")
    Project selectProjectByPid(@Param("pid") Integer pid);

    @Select("SELECT * FROM project WHERE pid=#{pid}")
    Project selectByPid(@Param("pid") Integer pid);

    @Select("SELECT pid From project WHERE name=#{name} and facility=#{facility} and activity=#{activity} and isLesson=#{isLesson}")
    Integer selectPid(@Param("name") String name, @Param("facility") String facility, @Param("activity")String activity, @Param("isLesson") Integer isLesson);

    //查找课程
    @Select("SELECT * FROM project where isLesson=1 and valid=1")
    List<Map<String, Object>> selectAllLessons();

    @Select("SELECT * FROM project where isLesson=1")
    List<Map<String, Object>> selectLessons();

    @Select("SELECT * FROM project where isLesson=1 and email=#{email}")
    List<Map<String, Object>> selectLessonOrder(@Param("email") String email);

    @Select("SELECT * FROM project where isLesson=0 or isLesson=2")
    List<Map<String, Object>> selectProjects();

    @Select("SELECT * FROM project WHERE isLesson=0 or isLesson=2 and valid=1")
    List<Map<String, Object>> select();

    @Select("SELECT * FROM project where valid=1")
    List<Map<String, Object>> selectAll();

    @Select("SELECT * FROM project where facility=#{facility} and activity=#{activity} and valid=1")
    List<Map<String, Object>> selectAllProjectOfOneActivity(@Param("facility") String facility, @Param("activity") String activity);

    @Select("SELECT * FROM project where facility=#{facility} and activity=#{activity}")
    List<Map<String, Object>> selectProjectOfOneActivity(@Param("facility") String facility, @Param("activity") String activity);

    //查找单个设施所有项目
    @Select("SELECT * FROM project where facility=#{facility}")
    List<Map<String, Object>> selectAllProjectOfOneFacility(@Param("facility") String facility);

    @Select("SELECT count(capacity) FROM project WHERE facility=#{facility} AND isLesson=1 AND isWeekly=0 AND ((#{startTime} between startTime and endTime) or (#{endTime} between startTime and endTime))")
    Integer usedNumberOfFacility(@Param("facility") String facility, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    @Select("SELECT count(capacity) FROM project WHERE find_in_set(#{weekDay}, dayOfWeek) AND isWeekly=1 AND facility=#{facility} AND isLesson=1 AND ((#{startTime} between startTime and endTime) or (#{endTime} between startTime and endTime))")
    Integer usedWeekNumberOfFacility(@Param("facility") String facility, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime, @Param("weekDay") Integer weekDay);

    @Update("UPDATE team.project SET picture=#{picture} where name=#{name} and facility=#{facility} and activity=#{activity}")
    void insertProjectPicture(@Param("dayOfWeek") Blob picture, @Param("name")String name, @Param("facility")String facility, @Param("activity")String activity);

    @Update("UPDATE team.project SET valid = #{valid} WHERE pid = #{pid}")
    void stopProject(Project project);

    @Delete("DELETE FROM project WHERE facility = #{facility}")
    void deleteFacilitiesByName(@Param("facility") String facility);

    @Delete("DELETE FROM project WHERE pid = #{pid}")
    void deleteProjectByPid(@Param("pid") Integer pid);


}


