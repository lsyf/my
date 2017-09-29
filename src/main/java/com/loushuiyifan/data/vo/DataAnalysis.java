package com.loushuiyifan.data.vo;

import lombok.Data;

/**
 * @author 漏水亦凡
 * @date 2017/9/19
 */
@Data
public class DataAnalysis {
    String acctMonth;
    String latnId;
    String latnName;
    Double lastYearMonth;
    Double lastMonth;
    Double thisMonth;
    Double totalLastYear;
    Double totalThisYear;
    Double yearTotalGrowthRate;
    Double monthGrowthRate;
    Double monthBudget;
    Double monthBudgetGap;
    Integer orderId;
}
