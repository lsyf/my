package com.loushuiyifan.data.vo;

import lombok.Data;

/**
 * @author 漏水亦凡
 * @date 2017/7/24
 */
@Data
public class Report3 {

    private String name ;
    private Double monthTotal ;//本月实绩
    private Double monthDiff ;//环比差额
    private Double monthGap ;//预算缺口
    private Double yearTotal ;//本年累计
    private Double yearGrowRate ;//累计同比增长率
    private Double yearGap ;//累计预算缺口
    private Double yearComRate ;//年度预算完成率
    private Double percent ;//占总收入占比


}
