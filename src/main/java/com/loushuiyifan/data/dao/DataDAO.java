package com.loushuiyifan.data.dao;

import com.loushuiyifan.config.mybatis.MyMapper;
import com.loushuiyifan.data.vo.DataAnalysis;
import com.loushuiyifan.data.vo.DataAnalysis2;
import com.loushuiyifan.data.vo.DataAnalysis3;
import com.loushuiyifan.data.vo.MonthData;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 漏水亦凡
 * @date 2017/9/19
 */
public interface DataDAO extends MyMapper<DataAnalysis> {

    List<MonthData> da1();

    List<DataAnalysis> da2(String month);

    List<DataAnalysis2> da2_2(@Param("month") String month,
                              @Param("latnId") String latnId);

    List<DataAnalysis2> da2_3(@Param("month") String month,
                              @Param("latnId") String latnId,
                              @Param("parentId") String parentId);

    List<DataAnalysis2> da2_4(@Param("month") String month,
                              @Param("latnId") String latnId,
                              @Param("parentId") String parentId);

    List<DataAnalysis3> da2_listIncomeSource(@Param("month") String month,
                                             @Param("latnId") String latnId,
                                             @Param("latnId2") String latnId2,
                                             @Param("productId") String productId,
                                             @Param("sourceId") String sourceId);

    List<DataAnalysis3> da2_listProduct(@Param("month") String month,
                                             @Param("latnId") String latnId,
                                             @Param("latnId2") String latnId2,
                                             @Param("sourceId") String sourceId,
                                             @Param("sourceId2") String sourceId2);

    List<DataAnalysis3> da2_listLatn(@Param("month") String month,
                                        @Param("latnId") String latnId,
                                        @Param("productId") String productId,
                                        @Param("sourceId") String sourceId,
                                        @Param("sourceId2") String sourceId2);
}
