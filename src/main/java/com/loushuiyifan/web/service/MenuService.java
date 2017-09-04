package com.loushuiyifan.web.service;

import com.loushuiyifan.mybatis.bean.Menu;
import com.loushuiyifan.mybatis.mapper.MenuMapper;
import com.loushuiyifan.mybatis.service.UserService;
import com.loushuiyifan.redis.service.RedisDAO;
import com.loushuiyifan.web.bean.Sidebar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 漏水亦凡
 * @create 2017-05-03 17:21.
 */
@Service
public class MenuService {
    private Logger LOGGER = LoggerFactory.getLogger(MenuService.class);


    @Autowired
    MenuMapper menuMapper;

    @Autowired
    UserService userService;
    @Autowired
    ResourceService resourceService;

    @Autowired
    RedisDAO redisDAO;

    /**
     * 根据用户 获取menu
     *
     * @param username
     * @return
     */
    @Cacheable("menus")
    public List<Menu> getMenus(String username) {

        boolean isAdmin = userService.isAdmin(username);
        List<Menu> menus;
        if (isAdmin) {
            menus = menuMapper.findAllMenus();
        } else {
            menus = menuMapper.findMenus(username);
        }


        return menus;
    }


    public List<Menu> getAllMenus() {
        List<Menu> menus = menuMapper.findAllMenus();
        return menus;
    }

    /**
     * 根据用户名获取sidebar
     *
     * @param username
     * @return
     */
    public Sidebar getSidebar(String username) {
        List<Menu> menus = getMenus(username);

        //首先遍历一遍 将数据结构转成map
        //以父ID为key，子节点集合为value
        Map<Long, List<Menu>> map = new HashMap<>();
        for (Menu menu : menus) {
            long parentId = menu.getParentId();
            List<Menu> list = map.get(parentId);
            if (list == null) {
                list = new ArrayList<>();
            }
            list.add(menu);
            map.put(parentId, list);
        }

        //然后通过递归转换成Siderbar
        Sidebar sidebar = new Sidebar();
        sidebar = toSidebar(0, map, sidebar);

        return sidebar;
    }

    /**
     * 转换成Sidebar数据结构
     *
     * @param parentId
     * @param map
     * @param sidebar
     * @return
     */
    public Sidebar toSidebar(long parentId, Map<Long, List<Menu>> map, Sidebar sidebar) {
        //首先将 menu列表转换成 sidebar列表
        List<Menu> menu_kids = map.get(parentId);
        if (menu_kids == null) {
            return sidebar;
        }
        List<Sidebar> kids = new ArrayList<>();
        for (Menu menu : menu_kids) {
            Sidebar temp = new Sidebar();
            temp.setMenu(menu);
            kids.add(temp);
        }

        //关联父节点与子节点
        sidebar.setKids(kids);
        sidebar.setTree(kids != null && kids.size() > 0);

        //遍历子节点，重复关联后代节点
        for (Sidebar kid : kids) {
            toSidebar(kid.getMenu().getId(), map, kid);
        }

        return sidebar;
    }


    /**
     * 添加menu
     *
     * @param menu
     * @return 插入行数
     */
    public int addMenu(Menu menu) {
        //根据菜单 父id 查找父节点
        long parentId = menu.getParentId();

        if (parentId == 0) {
            menu.setLvl((short) 1);
            menu.setParentIds("0/");
        } else {
            Menu parent = menuMapper.selectByPrimaryKey(parentId);
            //lvl +1
            menu.setLvl((short) (parent.getLvl() + 1));
            //路径增加
            menu.setParentIds(parent.getParentIds() + parentId + "/");
        }

        //插入菜单
        int num = menuMapper.insertSelective(menu);

        //插入资源
        resourceService.addResource("menu", menu.getName(), menu.getId());
        return num;
    }

    /**
     * 修改menu
     *
     * @param menu
     * @return
     */
    public int updateMenu(Menu menu) {
        //更改菜单
        int num = menuMapper.updateByPrimaryKeySelective(menu);

        //更改资源
        resourceService.updateResource("menu", menu.getId(), menu.getName());

        return num;
    }

    /**
     * 删除menu
     *
     * @param menu
     * @return
     */
    public int deleteMenu(Menu menu) {
        String parentIds = menu.getParentIds() + menu.getId() + "%";

        //先删除资源
        resourceService.deleteResourceWithKids("menu", menu.getId(), parentIds);

        //删除
        int num = menuMapper.deleteByPrimaryKey(menu.getId());

        //删除所有子节点
        num += menuMapper.deleteAllKids(parentIds);
        return num;
    }
}
