package com.loushuiyifan.mybatis.mapper;

import com.loushuiyifan.mybatis.bean.User;
import com.loushuiyifan.mybatis.util.MyMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface UserMapper extends MyMapper<User> {

    List<Map> test(List<String> ids);


    User selectByUsername(String username);

    String isAdmin(String username);

    List<String> findAllPermissions();

    List<String> findRoles(String username);

    List<String> findPermissions(String username);

    List<User> selectByNameOrRole(String name);

    List<Long> selectAllRoleIds(long userId);

    List<Long> selectAllOrgIds(long userId);


    List<User> selectAllUsers();


    int addUserRoles(
            @Param("userId") Long userId,
            @Param("roleIds") List<Long> roleIds);

    int deleteUserRoles(@Param("userId") long userId,
                        @Param("roleIds") List<Long> roleIds);

    int deleteAllUserRoles(long id);

    void addUserOrgs(@Param("userId") long userId,
                     @Param("orgIds") List<Long> orgIds);

    void deleteUserOrgs(@Param("userId") long userId,
                        @Param("orgIds") List<Long> orgIds);

    int deleteAllUserOrgs(long id);
}