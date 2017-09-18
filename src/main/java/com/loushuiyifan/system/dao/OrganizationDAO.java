package com.loushuiyifan.system.dao;


import com.loushuiyifan.common.bean.Organization;
import com.loushuiyifan.system.vo.CodeListTax;
import com.loushuiyifan.system.vo.UserCompany;

import java.util.List;

public interface OrganizationDAO {


    List<Organization> listOrganizationByParentId(long parentId);

    List<Organization> listByLvl(int lvl);

    List<Organization> listNodesByPath(String path);

    int deleteAllKids(String path);

    int countUserByOrgPath(String path);

    int countUserByOrgId(Long id);

    /**
     * 根据等级lvl获取 code_list_tax表中符合的地市组织信息
     * @param lvl
     * @return
     */
    List<CodeListTax> listFromCodeListTax(int lvl);

    /**
     * 查询user_company表中 所有部门组织信息
     * @return
     */
    List<UserCompany> listFromUserCompany();
}