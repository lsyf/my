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
public interface RptQueryComDetailDAO extends MyMapper<CommonVO> {

	List<Map<String, String>> selectIndexCodeAndName(String rptNo);

    List<Map<String, String>> listComDetailRowMap(String rptNo);
    
    /**
     * 2018年列添加客户群明细
     * 上年同期数
     */
    @MapKey("key")
    Map<String, Map<String, String>> lastYearAsMap(@Param("lastYearThisMonth") String lastYearThisMonth,
                                                   @Param("latnId") String latnId);

    /**
     * 2018年列添加客户群明细
     * 本月发生数
     */
    @MapKey("key")
    Map<String, Map<String, String>> thisMonthAsMap(@Param("thisMonth") String thisMonth,
                                                    @Param("latnId") String latnId);

    /**
     * 2018年列添加客户群明细
     * 本年累计数
     */
    @MapKey("key")
    Map<String, Map<String, String>> thisYearAsMap(@Param("firstMonth") String firstMonth,
										           @Param("thisMonth") String thisMonth,
										           @Param("latnId") String latnId);

}
