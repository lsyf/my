package com.loushuiyifan.report.dao;

import com.loushuiyifan.common.bean.Organization;
import com.loushuiyifan.config.mybatis.MyMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author 漏水亦凡
 * @date 2017/9/25
 */
public interface LocalNetDAO extends MyMapper<Organization> {


    List<Organization> listByUserAndLvl(@Param("userId") Long userId,
                                        @Param("lvl") Integer lvl);

    List<Map> listByRootAndLvl(@Param("list") List<Organization> relatedList,
                               @Param("lvl")  Integer lvl);
}
