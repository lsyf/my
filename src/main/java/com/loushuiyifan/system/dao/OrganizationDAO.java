package com.loushuiyifan.system.dao;


import com.loushuiyifan.common.bean.Organization;
import com.loushuiyifan.system.vo.CodeListTaxDTO;
import com.loushuiyifan.system.vo.UserCompany;
import org.apache.ibatis.annotations.Param;

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
    List<CodeListTaxDTO> listFromCodeListTax(int lvl);

    /**
     * 查询user_company表中 所有部门组织信息
     * @return
     */
    List<UserCompany> listFromUserCompany();

    /**
     * 根据组织类型查询所有 信息
     * @param type
     * @return
     */
    List<Organization> listByType(String type);

    List<Organization> listByTypeAndLvl(@Param("type") String type,
                                        @Param("lvl") int lvl);

    List<Organization> getAllKidsByData(@Param("pData")String pData,
                                        @Param("type")String type);
}