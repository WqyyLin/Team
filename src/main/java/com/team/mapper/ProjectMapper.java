package com.team.mapper;

import com.team.entity.Activity;
import com.team.entity.Project;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.type.BlobTypeHandler;

import java.sql.Blob;
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

    //查找课程
    @Select("SELECT * FROM project where isLesson=1")
    List<Map<String, Object>> selectAllLessons();

    //查找单个设施所有项目
    @Select("SELECT * FROM project where facility=#{facility}")
    List<Map<String, Object>> selectAllProjectOfOneFacility(@Param("facility") String facility);

    @Update("UPDATE team.project SET picture=#{picture} where name=#{name} and facility=#{facility} and activity=#{activity}")
    void insertProjectPicture(@Param("dayOfWeek") Blob picture, @Param("name")String name, @Param("facility")String facility, @Param("activity")String activity);
}
