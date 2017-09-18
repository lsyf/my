package com.loushuiyifan.system.service;

import com.loushuiyifan.common.bean.Role;
import com.loushuiyifan.common.mapper.RoleMapper;
import com.loushuiyifan.system.dao.RoleDAO;
import com.loushuiyifan.system.vo.AddVO;
import com.loushuiyifan.system.vo.Menu4Role;
import com.loushuiyifan.system.vo.Permission4Role;
import com.loushuiyifan.system.vo.RoleVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 漏水亦凡
 * @create 2017-05-10 15:44.
 */
@Service
public class RoleService {

    @Autowired
    RoleMapper roleMapper;

    @Autowired
    RoleDAO roleDAO;


    @Autowired
    UserService userService;


    public List<Role> getRoles(String name) {
        List<Role> roles;
        if (StringUtils.isEmpty(name) || name.trim().length() == 0) {
            roles = roleMapper.selectAll();
        } else {
            name = String.format("%%%s%%", name.trim());
            roles = roleDAO.selectByName(name);
        }

        return roles;
    }

    /**
     * 根据id查询角色所有菜单和权限列表
     *
     * @param id
     * @return
     */
    public List<Long> getRoleInfo(Long id, String name) {

        //查询 该角色所有的资源列表
        //如果是管理员则返回所有结果
        List<Long> resources = null;
        if ("admin".equals(name)) {
            resources = roleDAO.selectAllResources();
        } else {
            resources = roleDAO.selectResourceByRole(id);
        }


        return resources;
    }

    /**
     * 获取所有 menu 和 permission
     *
     * @return
     */
    public Map<String, Object> getAllMP() {

        List<Menu4Role> menus = roleDAO.selectAllMenus();
        List<Permission4Role> permissions = roleDAO.selectAllPermissions();

        Map<String, Object> map = new HashMap<>();
        map.put("menus", menus);
        map.put("permissions", permissions);

        return map;
    }


    /**
     * 更新 ROLE
     *
     * @param roleUpdate
     * @return
     */
    public int updateRole(RoleVO roleUpdate) {
        long id = roleUpdate.getId();
        String name = roleUpdate.getName();
        String role = roleUpdate.getRole();

        //首先更新role
        Role bean = new Role();
        bean.setId(id);
        bean.setName(name);
        bean.setRole(role);
        int num = roleMapper.updateByPrimaryKeySelective(bean);

        //然后更新role相关的 资源(增加or删除)
        List<Long> list_add = new ArrayList<>();
        List<Long> list_del = new ArrayList<>();
        for (AddVO a : roleUpdate.getMenu()) {
            if (a.getAdd()) {
                list_add.add(a.getId());
            } else {
                list_del.add(a.getId());
            }
        }
        for (AddVO b : roleUpdate.getPermission()) {
            if (b.getAdd()) {
                list_add.add(b.getId());
            } else {
                list_del.add(b.getId());
            }
        }
        if (!list_add.isEmpty()) {
            for (long resId : list_add) {
                long keyId = userService.nextvalKey();
                roleDAO.addRoleResource(keyId, id, resId);
            }
        }
        if (!list_del.isEmpty()) {
            roleDAO.deleteRoleResources(id, list_del);
        }

        return num;
    }

    /**
     * 添加 ROLE
     *
     * @param roleUpdate
     * @returnd
     */
    public int addRole(RoleVO roleUpdate) {
        String name = roleUpdate.getName();
        String role = roleUpdate.getRole();

        //首先添加role
        Role bean = new Role();
        bean.setName(name);
        bean.setRole(role);

        int num = roleMapper.insertSelective(bean);

        //然后更新role相关的 资源(增加or删除)
        List<Long> list_add = new ArrayList<>();
        for (AddVO a : roleUpdate.getMenu()) {
            list_add.add(a.getId());
        }
        for (AddVO b : roleUpdate.getPermission()) {
            list_add.add(b.getId());
        }
        if (!list_add.isEmpty()) {
            for (long resId : list_add) {
                long keyId = userService.nextvalKey();
                roleDAO.addRoleResource(keyId, bean.getId(), resId);
            }
        }

        return num;
    }

    /**
     * 删除角色
     *
     * @param id
     * @return
     */
    public int deleteRole(long id) {
        //删除角色
        int num = roleMapper.deleteByPrimaryKey(id);
        //删除角色相关资源
        roleDAO.deleteAllRoleResources(id);

        return num;
    }

    /**
     * 判断角色与用户是否存在关联
     *
     * @param id
     * @return
     */
    public boolean isRoleRelatedUser(long id) {
        int num = roleDAO.countUserByRoleId(id);
        return num > 0;
    }

    public List<Role> listAll() {
        return roleMapper.selectAll();
    }
}
