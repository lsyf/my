package com.loushuiyifan.data.vo;

import lombok.Data;

/**
 * @author 漏水亦凡
 * @date 2017/9/19
 */
@Data
public class DataAnalysis2 {
    String incomeSourceId;
    String incomeSourceName;
    Double lastMonth;
    Double thisMonth;
    Double monthGrowthValue;
    Double monthGrowthRate;
}
