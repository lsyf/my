package com.loushuiyifan.report.dao;

import com.loushuiyifan.config.mybatis.MyMapper;
import com.loushuiyifan.report.bean.RptCase;
import org.apache.ibatis.annotations.Param;

/**
 * Created by Administrator on 2017/11/20.
 */
public interface RptCaseDAO extends MyMapper<RptCase> {

    Long nextval();

    RptCase selectCase(@Param("month") String month,
                       @Param("latnId") String latnId,
                       @Param("incomeSource") String incomeSource,
                       @Param("cust") String cust,
                       @Param("tax") String tax,
                       @Param("type") String type);
}
