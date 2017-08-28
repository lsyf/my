package com.loushuiyifan.mybatis.mapper;


import com.loushuiyifan.mybatis.bean.Menu;
import com.loushuiyifan.mybatis.util.MyMapper;

import java.util.List;

public interface MenuMapper extends MyMapper<Menu> {


    List<Menu> findAllMenus();

    List<Menu> findMenus(String username);

    int deleteAllKids(String paths);

}