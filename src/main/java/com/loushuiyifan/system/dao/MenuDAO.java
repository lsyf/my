package com.loushuiyifan.system.dao;


import com.loushuiyifan.common.bean.Menu;

import java.util.List;

public interface MenuDAO   {


    List<Menu> findAllMenus();

    List<Menu> findMenus(String username);

    int deleteAllKids(String paths);

}