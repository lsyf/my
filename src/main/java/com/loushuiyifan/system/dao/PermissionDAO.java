package com.loushuiyifan.system.dao;


import com.loushuiyifan.common.bean.Permission;

import java.util.List;

public interface PermissionDAO {
    List<Permission> findAllPermissions();

    List<Permission> findPermissions(String username);

    int deleteAllKids(String parentIds);
}