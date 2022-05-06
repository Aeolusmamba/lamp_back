package com.home.lamp.dao;


import com.home.lamp.bean.Auth;
import com.home.lamp.bean.Menu;
import com.home.lamp.bean.MenuList;
import com.home.lamp.bean.User;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface UserDao {

    /**
     * login
     * @param username
     * @param password
     * @return
     */
    @Select("select * from user where username=#{username} and password=#{password}")
    User login(@Param("username") String username, @Param("password") String password);

    /**
     * 查询是否关联auth
     * @param id
     * @return
     */
    @Select("select * from auth where userId=#{userId}")
    Auth queryAuth(@Param("userId") Integer id);

    /**
     * 关联auth
     * @param id
     */
    @Insert("insert into auth(userId, menuAuth) values(#{userId}, #{menuAuth})")
    void withAuth(@Param("userId") Integer id, @Param("menuAuth") Integer menuAuth);

    /**
     *
     * @param auth
     */
    @Update("update auth set token=#{token}, updateTime=#{updateTime}, ip=#{ip} where userId=#{userId}")
    void updateAuth(Auth auth);

    /**
     * 根据token值查询menuAuth是0还是1
     */
    @Select("select menuAuth from auth where token=#{token}")
    Integer getAuth(@Param("token") String token);

    /**
     * 超级管理员查询表menu信息，只找父菜单
     */
    @Select("select menuId, authName, childrenId, order1, path from menu where level = 0")
    List<Menu> adminSelectMenus();

    /**
     * 查询表menu信息，只找父菜单
     */
    @Select("select menuId, authName, childrenId, order1, path from menu where menuAuth=#{menuAuth} and level=0")
    List<Menu> selectMenus(@Param("menuAuth") int menuAuth);

    /**
     * 查询表menu信息，只查一条信息
     */
    @Select("select menuId, authName, childrenId, order1, path, menuAuth from menu where menuId = #{menuId}")
    Menu selectMenu(@Param("menuId") int menuId);

    /**
     * 超级管理员查询：返回所有用户信息
     */
    @Select("select * from user limit #{limit} offset #{offset}")
    List<User> getAllUsers(@Param("limit") int limit, @Param("offset") int offset);

    /**
     * 超级管理员查询：当前用户总数
     */
    @Select("select count(*) from user")
    Integer getUsersNum();

    /**
     * 超级管理员查询：通过username模糊查询，返回所有用户信息
     */
    @Select("select * from user where username like concat('%',#{query},'%') limit #{limit} offset #{offset}")
    List<User> getAllUsersByQuery(@Param("limit") int limit, @Param("offset") int offset, @Param("query") String query);

    /**
     * 超级管理员查询：通过username模糊查询，当前用户总数
     */
    @Select("select count(*) from user where username like concat('%',#{query},'%')")
    Integer getUsersNumByQuery(@Param("query") String query);

    /**
     * 超级管理员添加新用户
     * @param user
     * @return
     */
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    @Insert("insert into user(username, password, roleName, mobile, addTime) values(#{username}, #{password}, #{roleName}, #{mobile}, #{addTime})")
    void addUser(User user);

    /**
     * 获取用户信息
     * @param id
     * @return
     */
    @Select("select * from user where id = #{id}")
    User getUser(Integer id);

    /**
     * 超级管理员修改其他用户的信息
     * @param id
     * @param username
     * @param mobile
     */
    @Update("update user set username=#{username}, mobile=#{mobile} where id=#{id}")
    void editUser(int id, String username, String mobile);

    /**
     * 超级管理员删除用户
     * @param id
     */
    @Delete("delete from user where id=#{id}")
    void deleteUser(int id);
}
