package com.loushuiyifan.report.vo;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 报表查询展现
 * @author 漏水亦凡
 * @date 2017/11/20
 */
@Data
public class RptQueryDataVO {
    List<Map<String, String>> titles;
    List<Map<String, String>> datas;
}
