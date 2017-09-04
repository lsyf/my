package com.loushuiyifan.mybatis.mapper;

import com.loushuiyifan.mybatis.bean.Role;
import com.loushuiyifan.mybatis.util.MyMapper;
import com.loushuiyifan.web.bean.Menu4Role;
import com.loushuiyifan.web.bean.Permission4Role;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RoleMapper extends MyMapper<Role> {
    List<Role> selectByName(String name);

    List<Long> selectResourceByRole(long roleId);

    List<Menu4Role> selectAllMenus();

    List<Permission4Role> selectAllPermissions();

    List<Long> selectAllResources();

    int addRoleResource(
            @Param("id") Long id,
            @Param("roleId") Long roleId,
            @Param("resourceId") Long resourceIds);

    int addRoleResources(
            @Param("roleId") Long roleId,
            @Param("resourceIds") List<Long> resourceIds);

    int deleteRoleResources(
            @Param("roleId") Long roleId,
            @Param("resourceIds") List<Long> resourceIds);


    int deleteAllRoleResources(long id);

    int countUserByRoleId(long id);
}