package com.loushuiyifan.report.dao;

import com.loushuiyifan.config.mybatis.MyMapper;
import com.loushuiyifan.report.bean.RptImportDataCut;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author yxk
 * @data 2017/10/26
 */
public interface RptImportCutDataDAO extends MyMapper<RptImportDataCut> {

    //重新导入更新is_active
    void updataCutFlag(@Param("latnId") Integer latnId,
                       @Param("incomeSource") String incomeSource,
                       @Param("shareType") Integer shareType,
                       @Param("userName") String userName,
                       @Param("activeFlag") String activeFlag
    );

    //删除前验证
    List<String> checkCut(@Param("month") String month,
                          @Param("latnId") Integer latnId,
                          @Param("incomeSource") String incomeSource,
                          @Param("shareType") Integer shareType,
                          @Param("userName") String userName,
                          @Param("ruleId") String ruleId);


}
