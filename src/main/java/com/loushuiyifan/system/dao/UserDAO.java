package com.loushuiyifan.system.dao;

import com.loushuiyifan.common.bean.User;
import com.loushuiyifan.system.vo.SUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface UserDAO {

    List<Map> test(List<String> ids);

    long nextvalKey();

    User selectByUsername(String username);

    String isAdmin(String username);

    List<String> findAllPermissions();

    List<String> findRoles(String username);

    List<String> findPermissions(String username);

    List<User> selectByNameOrRole(String name);

    List<Long> selectAllRoleIds(long userId);

    /**
     * 根据用户id和组织类型，获取用户所属组织
     *
     * @param userId
     * @return
     */
    List<Long> selectAllOrgIds(@Param("userId") long userId,
                               @Param("type") String type);


    List<User> selectAllUsers();


    int addUserRoles(
            @Param("userId") Long userId,
            @Param("roleIds") List<Long> roleIds);

    int addUserRole(@Param("id") long id,
                    @Param("userId") long userId,
                    @Param("roleId") long roleId);


    int deleteUserRoles(@Param("userId") long userId,
                        @Param("roleIds") List<Long> roleIds);

    int deleteAllUserRoles(long id);

    int deleteAllUserOrgs(long id);


    void addUserOrgs(@Param("userId") long userId,
                     @Param("orgIds") List<Long> orgIds);

    void deleteUserOrgs(@Param("userId") long userId,
                        @Param("orgIds") List<Long> orgIds);

    int addUserOrg(@Param("id") long id,
                   @Param("userId") long userId,
                   @Param("orgId") long orgId);

    void updateLogin(Long id);

    /**
     * 从s_user查询所有sts为Y的用户信息
     *
     * @return
     */
    List<SUser> listFromSUser();

    User findByPhone(String phone);
}