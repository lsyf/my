package com.loushuiyifan.data.dao;

import com.loushuiyifan.config.mybatis.MyMapper;
import com.loushuiyifan.data.vo.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 漏水亦凡
 * @date 2017/9/19
 */
public interface DataDAO extends MyMapper<DataAnalysis> {

    List<MonthData> da1();


    List<DataAnalysis2> da2_listIncomeSource(@Param("month") String month,
                                             @Param("latnId") String latnId,
                                             @Param("latnId2") String latnId2,
                                             @Param("productId") String productId,
                                             @Param("sourceId") String sourceId);

    List<DataAnalysis2> da2_listProduct(@Param("month") String month,
                                        @Param("latnId") String latnId,
                                        @Param("latnId2") String latnId2,
                                        @Param("sourceId") String sourceId,
                                        @Param("sourceId2") String sourceId2);

    List<DataAnalysis2> da2_listLatn(@Param("month") String month,
                                     @Param("latnId") String latnId,
                                     @Param("productId") String productId,
                                     @Param("sourceId") String sourceId,
                                     @Param("sourceId2") String sourceId2);

    List<DataAnalysis3> da3(@Param("month") String month,
                            @Param("latnId") String latnId);

    List<DataAnalysis4> da4_listLatn1(@Param("month") String month,
                                      @Param("discountId") String discountId);

    List<DataAnalysis4> da4_listLatn2(@Param("month") String month,
                                      @Param("discountId") String discountId,
                                      @Param("billId") String billId);

    List<DataAnalysis4> da4_listBill(@Param("month") String month,
                                     @Param("latnId") String latnId,
                                     @Param("discountId") String discountId);

    List<DataAnalysis4> da4_listDiscount1(@Param("month") String month,
                                          @Param("latnId") String latnId);

    List<DataAnalysis4> da4_listDiscount2(@Param("month") String month,
                                          @Param("latnId") String latnId,
                                          @Param("billId") String billId);
}
