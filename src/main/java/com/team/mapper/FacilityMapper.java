package com.team.mapper;

import com.team.entity.Facility;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface FacilityMapper {

    /**
     * 通过fid查找单个设施
     */
    @Select("SELECT * FROM facility WHERE fid = #{fid}")
    Facility selectFacilityByFid(@Param("fid") Integer fid);

    /**
     * 查找所有设施的总容量和
     */
    @Select("SELECT SUM(capacity) FROM facility")
    Integer selectFacilityNumber();

    @Select("SELECT SUM(capacity) FROM facility where name=#{name}")
    Integer selectCapacity(@Param("name") String name);

    /**
     * 查找所有设施
     */
    @Select("SELECT fid, name, capacity as holdpeople, count(name) as groundnumber, title as Ad_title, description as Ad_describtion, startTime as starttime, endTime as endtime FROM facility group by name")
    List<Map<String, Object>> selectAllFacility();

    /**
     * 查找该类设施总量
     */
    @Select("SELECT count(name) FROM facility where name=#{name}")
    Integer selectIsAvailable(@Param("name") String name);

    /**
     * 查找所有该类的设施fid
     */
    @Select("SELECT fid FROM facility where name=#{name}")
    List<Integer> selectFacilitiesFidByName(@Param("name") String name);

    /**
     * 添加设施
     */
    @Insert("INSERT INTO facility (name, capacity, description, title, isValid, stopTime, startTime, endTime) " +
            " VALUES ( #{name}, #{capacity}, #{description}, #{title}, 1, #{stopTime}, #{startTime}, #{endTime})")
    void insertFacility(Facility facility);

    @Update("UPDATE facility SET name = #{name}, capacity = #{capacity} WHERE fac = #{fid}")
    int updateFacility(@Param("fid") int fid, @Param("name") String name, @Param("capacity") Integer capacity);

    @Update("UPDATE team.facility SET isValid = 0 WHERE name = #{name}")
    void stopFacility(@Param("name") String name);

    @Update("UPDATE team.facility SET isValid = 1, stopTime = #{stopTime} WHERE isValid = 0 And stopTime < #{time}")
    void restartFacility(@Param("stopTime")LocalDateTime stopTime, @Param("time") LocalDateTime time);

    /**
     * 删除设施
     */
    @Delete("DELETE FROM facility WHERE fid = #{fid}")
    int deleteFacility(@Param("fid") int fid);

    @Delete("DELETE FROM facility WHERE name = #{name}")
    void deleteFacilitiesByName(@Param("name") String name);

}
