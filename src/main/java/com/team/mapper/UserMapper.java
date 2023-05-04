package com.team.mapper;

import com.team.entity.User;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface UserMapper {

    /**
     * 新增账号
     */
    @Insert("INSERT INTO user ( email, password, salt, activation_time, is_valid, confirm_code, name, type, membership, money) " +
            " VALUES ( #{email}, #{password}, #{salt}, #{activationTime}, #{isValid}, #{confirmCode}, #{name}, #{type}, 0, 0)")
    Integer insertUser(User user);

    @Select("SELECT money FROM user WHERE email = #{email} and is_valid = 1")
    Integer selectUserMoneyByEmail(@Param("email") String email);

    /**
     * 根据确认码查询用户
     */
    @Select("SELECT email, activation_time FROM user WHERE confirm_code = #{confirmCode} AND is_valid = 0")
    User selectUserByConfirmCode(@Param("confirmCode") String confirmCode);

    /**
     * 根据id查询用户
     */
    @Select("SELECT * FROM user WHERE id = #{id}")
    User selectUserById(@Param("id") Integer id);

    /**
     * 查询某时间新注册的用户数量
     */
    @Select("SELECT count(distinct(id)) FROM user where is_valid=1 AND activation_time between #{time1} AND #{time2}")
    Integer selectNewUsers(@Param("time1") LocalDateTime time1, @Param("time2") LocalDateTime time2);

    /**
     * 根据确认码查询用户并修改状态值为1
     */
    @Update("UPDATE user SET is_valid = 1 WHERE confirm_code = #{confirmCode}")
    Integer updateUserByConfirmCode(@Param("confirmCode") String confirmCode);

    @Update("UPDATE user SET headPicture = #{headPicture} WHERE email = #{email} AND is_valid = 1")
    Integer updateUserHeadPhoto(@Param("headPicture") String headPicture, @Param("email") String email);

    /**
     * 根据邮箱查询用户
     */
    @Select("SELECT id, name, email, password, salt, membership FROM user WHERE email = #{email} AND is_valid = 1")
    List<User> selectUserByEmail(@Param("email") String email);

    @Select("SELECT id, name, email, password, money, salt FROM user WHERE email = #{email} AND is_valid = 1")
    User selectOneUserByEmail(@Param("email") String email);

    @Select("SELECT id, name, email, password, money FROM user WHERE email = #{email}")
    User selectByEmail(@Param("email") String email);

    /**
     * 查询所有用户
     */
    @Select("SELECT id, email, name, money, membership, headPicture FROM user WHERE is_valid = 1")
    List<Map<String, Object>> selectAllUsers();

    /**
     * 通过邮箱更新用户信息
     */
    @Update("UPDATE user SET name=#{name}, password=#{password}, salt=#{salt}, money=#{money} WHERE email = #{email} And is_valid = 1")
    void changeUserInfo(@Param("name") String name, @Param("password") String password, @Param("money") Integer money, @Param("salt") String salt, @Param("email") String email);

    @Update("UPDATE user SET password=#{password}, salt=#{salt} WHERE email = #{email} And is_valid = 1")
    void resetUserPassword(@Param("password") String password, @Param("salt") String salt, @Param("email") String email);

    @Update("UPDATE user SET money=#{money} WHERE email=#{email} and is_valid=1")
    void updateUserMoney(@Param("money") Integer money, @Param("email") String email);

    @Update("UPDATE user SET money=#{money}, membership=1 WHERE email=#{email} and is_valid=1")
    void updateUserMember(@Param("money") Integer money, @Param("email") String email);

    /**
     * 删除重复用户
     */
    @Delete("DELETE FROM user WHERE email = #{email} AND is_valid = 0")
    void deleteRepeatUser(@Param("email") String email);

    /**
     * 通过id删除用户
     */
    @Delete("DELETE FROM user WHERE email = #{email} and is_valid = 1")
    void deleteUserByEmail(@Param("email") String email);

}
