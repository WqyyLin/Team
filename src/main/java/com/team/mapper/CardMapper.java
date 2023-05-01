package com.team.mapper;

import com.team.entity.Activity;
import com.team.entity.Card;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface CardMapper {
    @Insert("INSERT INTO card (type, money, time, discount, name) " +
            " VALUES ( #{type}, #{money}, #{time}, #{discount}, #{name})")
    void insertCard(Card card);

    @Insert("INSERT INTO user_card (cid, id, time) " +
            " VALUES (#{cid}, #{id}, #{time})")
    void insertCardOfUser(@Param("cid") Integer cid, @Param("id") Integer id, @Param("time") LocalDateTime time);

    @Select("SELECT cid From user_card Where id=#{id}")
    List<Integer> selectCidById(@Param("id") Integer id);

    @Select("SELECT discount FROM card WHERE cid=#{cid}")
    Integer getDiscount(@Param("cid") Integer cid);

    @Select("SELECT cid, money, discount, name, time From card WHERE type=1")
    List<Map<String, Object>> selectAllMemberCard();

    @Select("SELECT count(name) From card where name=#{name} and type=#{type}")
    Integer selectCardNum(@Param("name") String name, @Param("type") Integer type);

    @Select("SELECT count(*) From user_card where cid=#{cid} and id=#{id}")
    Integer selectCardNumOfUser(@Param("cid") Integer cid, @Param("id") Integer id);

    @Select("SELECT time From user_card where cid=#{cid} and id=#{id}")
    LocalDateTime selectCardTimeOfUser(@Param("cid") Integer cid, @Param("id") Integer id);

    @Select("SELECT * From card where cid=#{cid}")
    Card selectCardByCid(@Param("cid") Integer cid);

    @Update("UPDATE user_card SET time=#{time} WHERE cid=#{cid} and id=#{id}")
    void updateUserCard(@Param("cid") Integer cid, @Param("id") Integer id, @Param("time") LocalDateTime time);

    @Delete("DELETE FROM user_card WHERE time>#{time}")
    void deleteExpiredMember(@Param("time") LocalDateTime time);
}
