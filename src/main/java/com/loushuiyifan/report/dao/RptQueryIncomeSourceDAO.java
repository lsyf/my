package com.loushuiyifan.report.dao;

import com.loushuiyifan.config.mybatis.MyMapper;
import com.loushuiyifan.report.vo.CommonVO;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * @author 漏水亦凡
 * @date 2017/11/7
 */
public interface RptQueryIncomeSourceDAO extends MyMapper<CommonVO> {

    @MapKey("key")
    Map<String, Map<String, String>> listAsMap(@Param("month") String month,
                                               @Param("cust") String cust,
                                               @Param("latnId") String latnId,
                                               @Param("type") String type);


}
