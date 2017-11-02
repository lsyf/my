package com.loushuiyifan.report.dao;

import com.loushuiyifan.config.mybatis.MyMapper;
import com.loushuiyifan.report.bean.RptImportDataCut;

import java.util.List;

/**
 * @author yxk
 * @data 2017/10/26
 */
public interface RptImportCutDataDAO extends MyMapper<RptImportDataCut> {


    void updataCutFlag(RptImportDataCut cut);
    //删除前验证
    List<String> checkCut(String month,
                          Integer latnId,
                          String incomeSource,
                          Integer shareType,
                          String userName,
                          String ruleId);


}
