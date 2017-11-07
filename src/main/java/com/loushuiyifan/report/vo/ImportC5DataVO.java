package com.loushuiyifan.report.vo;

import lombok.Data;

/**
 * C5导入 区域统计
 *
 * @author 漏水亦凡
 * @date 2017/11/7
 */
@Data
public class ImportC5DataVO {
    String latnName;
    String c5Id;
    String c5Name;
    String c4Id;
    String c4Name;
    Double sum;
}
