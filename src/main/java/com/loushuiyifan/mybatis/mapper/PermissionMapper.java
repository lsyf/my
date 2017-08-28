package com.loushuiyifan.mybatis.mapper;


import com.loushuiyifan.mybatis.bean.Permission;
import com.loushuiyifan.mybatis.util.MyMapper;

import java.util.List;

public interface PermissionMapper extends MyMapper<Permission> {
    List<Permission> findAllPermissions();

    List<Permission> findPermissions(String username);

    int deleteAllKids(String parentIds);
}