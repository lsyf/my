package com.loushuiyifan.report.dao;

import com.loushuiyifan.config.mybatis.MyMapper;
import com.loushuiyifan.report.vo.CommonVO;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author 漏水亦凡
 * @date 2017/11/7
 */
public interface RptQueryComDetailDAO2017 extends MyMapper<CommonVO> {

    @MapKey("key")
    Map<String, Map<String, String>> listAsMap(@Param("lastYearThisMonth") String lastYearThisMonth,
                                               @Param("firstMonth") String firstMonth,
                                               @Param("thisMonth") String thisMonth,
                                               @Param("latnId") String latnId);


    List<Map<String, String>> listComDetailRowMap(String rptNo);
}
