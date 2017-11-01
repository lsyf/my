package com.loushuiyifan.system.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.loushuiyifan.common.bean.Organization;
import com.loushuiyifan.common.bean.User;
import com.loushuiyifan.common.bean.UserOrganization;
import com.loushuiyifan.common.mapper.UserMapper;
import com.loushuiyifan.common.mapper.UserOrganizationMapper;
import com.loushuiyifan.config.mybatis.BaseService;
import com.loushuiyifan.config.shiro.tool.PasswordHelper;
import com.loushuiyifan.system.dao.UserDAO;
import com.loushuiyifan.system.vo.AddVO;
import com.loushuiyifan.system.vo.DataPage;
import com.loushuiyifan.system.vo.SUser;
import com.loushuiyifan.system.vo.UserVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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


    /**
     * 获取用户的 角色，组织信息
     *
     * @param id
     * @return
     */
    public Map<String, Object> getUserInfo(String id) {
        long userId = Long.parseLong(id);


        //获取用户所有角色
        List<Long> roleIds = userDAO.selectAllRoleIds(userId);

        //获取所有组织信息
        List<Long> orgIds = userDAO.selectAllOrgIds(userId, OrganizationService.TYPE_CITY);

        //获取部门信息
        List<Long> deptIds = userDAO.selectAllOrgIds(userId, OrganizationService.TYPE_DEPT);
        Long deptId = null;
        if (deptIds != null && 1 == deptIds.size()) {
            deptId = deptIds.get(0);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("roleIds", roleIds);
        map.put("orgIds", orgIds);
        map.put("deptId", deptId);
        return map;
    }

    /**
     * 更改用户信息
     *
     * @param userUpdate
     * @return
     */
    public int updateUser(UserVO userUpdate) {
        Long id = userUpdate.getId();
        String username = userUpdate.getUsername();
        String password = userUpdate.getPassword();
        String nickname = userUpdate.getNickname();
        String phone = userUpdate.getPhone();
        String email = userUpdate.getEmail();
        Byte locked = userUpdate.getLocked();

        //如果未设昵称则默认为登录名
        if (StringUtils.isEmpty(nickname)) {
            nickname = username;
        }

        //首先更新user
        User bean = new User();
        bean.setId(id);
        bean.setUsername(username);
        bean.setPassword(password);
        bean.setLocked(locked);
        bean.setNickname(nickname);
        bean.setPhone(phone);
        bean.setEmail(email);

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
            if (a.getAdd()) {
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

        //由于部门组织 和地市组织 存放在一个表中
        //导致 用户和组织关联表只有一个
        //现抛弃 通过组织标记增删状态 进行数据库 删减操作
        //改为 先删除所有组织信息，再依次进行插入

        //删除该用户所有组织信息
        userDAO.deleteAllUserOrgs(id);

        //部门
        long keyId = userDAO.nextvalKey();
        Long deptId = userUpdate.getDeptId();
        userDAO.addUserOrg(keyId, id, deptId);

        //地市
        List<Long> list_add2 = new ArrayList<>();
        for (AddVO a : userUpdate.getOrgs()) {
            if (a.getAdd()) {
                list_add2.add(a.getId());
            }
        }
        if (!list_add2.isEmpty()) {
            for (long orgId : list_add2) {
                keyId = userDAO.nextvalKey();
                userDAO.addUserOrg(keyId, id, orgId);
            }
        }

        return num;
    }

    /**
     * 根据用户ID删除用户
     *
     * @param id
     * @return
     */
    public int delete(long id) {

        //删除用户
        int num = userMapper.deleteByPrimaryKey(id);
        //删除用户相关角色
        userDAO.deleteAllUserRoles(id);
        //删除用户相关组织
        userDAO.deleteAllUserOrgs(id);

        return 0;
    }

    /**
     * 新增一个用户信息
     *
     * @param userUpdate
     * @return
     * @throws Exception
     */
    public int addUser(UserVO userUpdate) throws Exception {

        String username = userUpdate.getUsername();
        String password = userUpdate.getPassword();
        String nickname = userUpdate.getNickname();
        String phone = userUpdate.getPhone();
        String email = userUpdate.getEmail();
        Byte locked = userUpdate.getLocked();

        //如果未设昵称则默认为登录名
        if (StringUtils.isEmpty(nickname)) {
            nickname = username;
        }

        //首先更新user
        User bean = new User();
        bean.setUsername(username);
        bean.setPassword(password);
        bean.setNickname(nickname);
        bean.setPhone(phone);
        bean.setEmail(email);
        bean.setLocked(locked);

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

        //部门
        long keyId = userDAO.nextvalKey();
        Long deptId = userUpdate.getDeptId();
        userDAO.addUserOrg(keyId, id, deptId);

        //地市
        List<Long> list_add2 = new ArrayList<>();
        for (AddVO a : userUpdate.getOrgs()) {
            list_add2.add(a.getId());
        }

        if (!list_add2.isEmpty()) {
            for (long orgId : list_add2) {
                keyId = userDAO.nextvalKey();
                userDAO.addUserOrg(keyId, id, orgId);
            }
        }


        return num;

    }


    /**
     * 更新登录时间
     *
     * @param id
     */
    public void updateLogin(Long id) {
        userDAO.updateLogin(id);
    }


    /**
     * 用来测试 事务管理
     */
    @Transactional
    public void testTransactional() {
        User user = new User();
        user.setUsername("testTransactional");
        userMapper.insertSelective(user);

        throw new RuntimeException("testTransactional");

    }

    @Autowired
    OrganizationService organizationService;

    @Autowired
    UserOrganizationMapper userOrganizationMapper;

    /**
     * 导入旧表s_user中用户信息(筛选sts为Y)
     */
    @Transactional
    public void importDataFromSUser() {
        //首先查询所有组织信息 部门+地市
        List<Organization> depts = organizationService.listByType(OrganizationService.TYPE_DEPT);
        List<Organization> citys = organizationService.listByType(OrganizationService.TYPE_CITY);

        //遍历组织信息 并 以data为key,id为value存放在Map中以备关联
        Map<String, Long> deptMap = Maps.newHashMap();
        Map<String, Long> cityMap = Maps.newHashMap();
        for (Organization o : depts) {
            deptMap.put(o.getData(), o.getId());
        }
        for (Organization o : citys) {
            cityMap.put(o.getData(), o.getId());
        }
        //释放内存
        depts = null;
        citys = null;


        //查询所有旧表用户信息
        List<SUser> list = userDAO.listFromSUser();

        //遍历用户信息
        for (SUser s : list) {
            User user = new User();
            user.setId(s.getId());
            user.setUsername(s.getName());
            user.setNickname(s.getViewname());
            user.setPhone(s.getTel());
            user.setEmail(s.getEmail());
            user.setPassword("123456");
            passwordHelper.encryptPassword(user);

            userMapper.insertSelective(user);

            Long id = user.getId();

            //关联部门
            Long deptId = deptMap.get(s.getCompanyid());
            if (deptId == null) {
                throw new RuntimeException("未找到部门ID");
            }
            UserOrganization dept = new UserOrganization();
            dept.setUserId(id);
            dept.setOrgId(deptId);
            userOrganizationMapper.insertSelective(dept);

            //关联地市
            Long cityId = cityMap.get(s.getDeptid());
            if (cityId == null) {
                throw new RuntimeException("未找到地市ID");
            }
            UserOrganization city = new UserOrganization();
            city.setUserId(id);
            city.setOrgId(cityId);
            userOrganizationMapper.insertSelective(city);

        }

        updateAllUserPassword();
    }

    /**
     * 更新所有用户密码
     */
    @Transactional
    public void updateAllUserPassword(){
        List<User> list = userMapper.selectAll();
        for (User user : list) {
            if (user.getId()<1000) {
                continue;
            }
            user.setPassword("123456");
            passwordHelper.encryptPassword(user);
            userMapper.updateByPrimaryKeySelective(user);
        }
    }

}
