package com.loushuiyifan.system.dao;


import com.loushuiyifan.common.bean.Organization;
import com.loushuiyifan.system.vo.CodeListTax;

import java.util.List;

public interface OrganizationDAO {


    List<Organization> listOrganizationByParentId(long parentId);

    List<Organization> listByLvl(int lvl);

    List<Organization> listNodesByPath(String path);

    int deleteAllKids(String path);

    int countUserByOrgPath(String path);

    int countUserByOrgId(Long id);

    List<CodeListTax> listFromCodeListTax(int lvl);
}