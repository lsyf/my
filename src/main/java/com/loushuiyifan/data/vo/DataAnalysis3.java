package com.loushuiyifan.data.vo;

import lombok.Data;

/**
 * @author 漏水亦凡
 * @date 2017/9/19
 */
@Data
public class DataAnalysis3 {
    String id;
    String name;
    Double lastYearMonth;
    Double lastMonth;
    Double thisMonth;
    Double totalLastYear;
    Double totalThisYear;
    Double yearTotalGrowthRate;
    Double monthGrowthRate;
    Double monthBudget;
    Double monthBudgetGap;
}
