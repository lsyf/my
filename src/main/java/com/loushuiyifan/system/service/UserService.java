package com.loushuiyifan.system.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.ImmutableSet;
import com.loushuiyifan.common.bean.User;
import com.loushuiyifan.common.mapper.UserMapper;
import com.loushuiyifan.config.mybatis.BaseService;
import com.loushuiyifan.config.shiro.tool.PasswordHelper;
import com.loushuiyifan.system.dao.UserDAO;
import com.loushuiyifan.system.vo.AddVO;
import com.loushuiyifan.system.vo.DataPage;
import com.loushuiyifan.system.vo.UserVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author 漏水亦凡
 * @create 2017-02-08 11:26.
 */
@Service
public class UserService extends BaseService<User> {

    @Autowired
    UserMapper userMapper;

    @Autowired
    UserDAO userDAO;

    @Autowired
    PasswordHelper passwordHelper;

    public Long nextvalKey() {
        return userDAO.nextvalKey();
    }

    public int saveSelective(User user) {
        return userMapper.insertSelective(user);
    }

    /**
     * 根据用户名查找角色
     *
     * @param username
     * @return
     */
    public Set<String> findRoles(String username) {
        List<String> list_role = userDAO.findRoles(username);
        Set<String> roles = ImmutableSet.copyOf(list_role);
        return roles;
    }

    /**
     * 根据用户名查找权限
     *
     * @param username
     * @return
     */
    public Set<String> findPermissions(String username) {

        List<String> list_permission = userDAO.findPermissions(username);

        Set<String> permissions = ImmutableSet.copyOf(list_permission);
        return permissions;
    }

    /**
     * 根据用户名查找用户
     *
     * @param username
     * @return
     */
    public User findByUsername(String username) {
        User user = userDAO.selectByUsername(username);
        return user;
    }

    /**
     * 根据用户名判断是否为管理员Admin
     *
     * @param username
     * @return
     */
    public boolean isAdmin(String username) {
        String name = userDAO.isAdmin(username);
        if (name == null || !name.equals(username)) {
            return false;
        } else {
            return true;
        }
    }

    public DataPage getUsers(int offset, int limit, String name) {
        List<User> users;

        // 如果筛选条件为空 则返回所有用户
        PageHelper.offsetPage(offset, limit);
        if (StringUtils.isEmpty(name) || name.trim().length() == 0) {
            users = userDAO.selectAllUsers();
        } else {
            name = "%" + name.trim() + "%";
            users = userDAO.selectByNameOrRole(name);
        }

        PageInfo page = new PageInfo(users);
        long num = page.getTotal();

        DataPage dp = new DataPage();
        dp.setRows(users);
        dp.setTotal((int) num);

        return dp;
    }


    public Map<String, Object> getUserInfo(String id) {
        long userId = Long.parseLong(id);


        //获取用户所有角色
        List<Long> roleIds = userDAO.selectAllRoleIds(userId);

        //获取所有组织信息
        List<Long> orgIds = userDAO.selectAllOrgIds(userId);

        Map<String, Object> map = new HashMap<>();
        map.put("roleIds", roleIds);
        map.put("orgIds", orgIds);
        return map;
    }

    public int updateUser(UserVO userUpdate) {
        Long id = userUpdate.getId();
        String username = userUpdate.getUsername();
        String password = userUpdate.getPassword();
        Byte locked = userUpdate.getLocked();

        //首先更新user
        User bean = new User();
        bean.setId(id);
        bean.setUsername(username);
        bean.setPassword(password);
        bean.setLocked(locked);

        //如果密码不为空，则更新密码
        if (password != null && !StringUtils.isEmpty(password.trim())) {
            passwordHelper.encryptPassword(bean);
        } else {
            bean.setPassword(null);
        }
        int num = userMapper.updateByPrimaryKeySelective(bean);

        //然后更新user相关的 role(增加or删除)
        List<Long> list_add = new ArrayList<>();
        List<Long> list_del = new ArrayList<>();
        for (AddVO a : userUpdate.getRoles()) {
            if (a.isAdd()) {
                list_add.add(a.getId());
            } else {
                list_del.add(a.getId());
            }
        }

        if (!list_add.isEmpty()) {
            for (long roleId : list_add) {
                long keyId = userDAO.nextvalKey();
                userDAO.addUserRole(keyId, id, roleId);
            }
        }
        if (!list_del.isEmpty()) {
            userDAO.deleteUserRoles(id, list_del);
        }

        //然后更新user相关的 organization(增加or删除)
        List<Long> list_add2 = new ArrayList<>();
        List<Long> list_del2 = new ArrayList<>();
        for (AddVO a : userUpdate.getOrgs()) {
            if (a.isAdd()) {
                list_add2.add(a.getId());
            } else {
                list_del2.add(a.getId());
            }
        }

        if (!list_add2.isEmpty()) {
            for (long orgId : list_add2) {
                long keyId = userDAO.nextvalKey();
                userDAO.addUserOrg(keyId, id, orgId);
            }
        }
        if (!list_del2.isEmpty()) {
            userDAO.deleteUserOrgs(id, list_del2);
        }

        return num;
    }

    public int delete(long id) {

        //删除用户
        int num = userMapper.deleteByPrimaryKey(id);
        //删除用户相关角色
        userDAO.deleteAllUserRoles(id);
        //删除用户相关组织
        userDAO.deleteAllUserOrgs(id);

        return 0;
    }

    public int addUser(UserVO userUpdate) throws Exception {

        String username = userUpdate.getUsername();
        String password = userUpdate.getPassword();

        //首先更新user
        User bean = new User();
        bean.setUsername(username);
        bean.setPassword(password);

        //如果密码不为空，则更新密码
        if (password == null || StringUtils.isEmpty(password.trim())) {
            throw new Exception("新建用户密码不能为空");
        }
        passwordHelper.encryptPassword(bean);
        int num = userMapper.insertSelective(bean);


        long id = bean.getId();

        //然后更新user相关的 role
        List<Long> list_add = new ArrayList<>();
        for (AddVO a : userUpdate.getRoles()) {
            list_add.add(a.getId());
        }

        if (!list_add.isEmpty()) {
            for (long roleId : list_add) {
                long keyId = userDAO.nextvalKey();
                userDAO.addUserRole(keyId, id, roleId);
            }
        }

        //然后更新user相关的 organization
        List<Long> list_add2 = new ArrayList<>();
        for (AddVO a : userUpdate.getOrgs()) {
            list_add2.add(a.getId());
        }

        if (!list_add2.isEmpty()) {
            for (long orgId : list_add2) {
                long keyId = userDAO.nextvalKey();
                userDAO.addUserOrg(keyId, id, orgId);
            }
        }


        return num;

    }


}
