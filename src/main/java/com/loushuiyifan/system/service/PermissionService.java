package com.loushuiyifan.system.service;

import com.loushuiyifan.common.bean.Permission;
import com.loushuiyifan.common.mapper.PermissionMapper;
import com.loushuiyifan.system.dao.PermissionDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 漏水亦凡
 * @create 2017-05-10 11:11.
 */
@Service
public class PermissionService {

    @Autowired
    UserService userService;

    @Autowired
    ResourceService resourceService;

    @Autowired
    PermissionMapper permissionMapper;

    @Autowired
    PermissionDAO permissionDAO;

    /**
     * 根据用户 获取permissions
     *
     * @param username
     * @return
     */
    public List<Permission> getPermissions(String username) {
        boolean isAdmin = userService.isAdmin(username);
        List<Permission> permissions;
        if (isAdmin) {
            permissions = permissionDAO.findAllPermissions();
        } else {
            permissions = permissionDAO.findPermissions(username);
        }
        return permissions;
    }

    /**
     * 获取所有权限
     *
     * @return
     */
    public List<Permission> getAllPermissions() {
        List<Permission> permissions = permissionDAO.findAllPermissions();
        return permissions;
    }

    /**
     * 增加权限
     *
     * @param permission
     */
    public int addPermission(Permission permission) {
        //根据权限 父id 查找父节点
        long parentId = permission.getParentId();

        //路径增加
        Permission parent = permissionMapper.selectByPrimaryKey(parentId);
        permission.setParentIds(parent.getParentIds() + parentId + "/");

        int num = permissionMapper.insertSelective(permission);

        //插入资源
        resourceService.addResource("permission", permission.getName(), permission.getId());
        return num;
    }


    /**
     * 修改权限
     *
     * @param permission
     */
    public int updatePermission(Permission permission) {
        int num = permissionMapper.updateByPrimaryKeySelective(permission);

        //更改资源
        resourceService.updateResource("permission", permission.getId(), permission.getName());

        return num;
    }


    /**
     * 删除权限
     *
     * @param permission
     */
    public int deletePermission(Permission permission) {
        String parentIds = permission.getParentIds() + permission.getId() + "%";
        //先删除资源
        resourceService.deleteResourceWithKids("permission", permission.getId(), parentIds);

        //删除
        int num = permissionMapper.deleteByPrimaryKey(permission.getId());

        //删除所有子节点
        num += permissionDAO.deleteAllKids(parentIds);

        return num;
    }
}
