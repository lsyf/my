package com.loushuiyifan.mybatis.mapper;


import com.loushuiyifan.mybatis.bean.Organization;
import com.loushuiyifan.mybatis.util.MyMapper;

import java.util.List;

public interface OrganizationMapper extends MyMapper<Organization> {


    List<Organization> listOrganizationByParentId(long parentId);

    List<Organization> listByLvl(int lvl);

    List<Organization> listNodesByPath(String path);

    int deleteAllKids(String path);

    int countUserByOrgPath(String path);

    int countUserByOrgId(Long id);
}