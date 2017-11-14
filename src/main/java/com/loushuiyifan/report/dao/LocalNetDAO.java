package com.loushuiyifan.report.dao;

import com.loushuiyifan.common.bean.Organization;
import com.loushuiyifan.config.mybatis.MyMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 漏水亦凡
 * @date 2017/9/25
 */
public interface LocalNetDAO extends MyMapper<Organization> {


    List<Organization> listByUserAndLvl(@Param("userId") Long userId,
                                        @Param("lvl") Integer lvl);

    List<Organization> listByRootAndLvl(@Param("list") List<Organization> relatedList,
                               @Param("lvl") Integer lvl);

    /**
     * TODO sql逻辑有点复杂，需要修改
     * 根据用户 和 c2值 获取相关的 1,2,3级别的地市，
     * 为接下来 获取该c2下属 关联做准备
     *
     * @param userId
     * @param data
     * @return
     */
    List<Organization> preForC3(@Param("userId") Long userId,
                                @Param("data") String data);
    
    /**
     * 根据收入来源typeCode和codeId获得codeName
     */
    String getCodeNameById(@Param("typeCode") String typeCode,
    		               @Param("codeId") String codeId);
}
