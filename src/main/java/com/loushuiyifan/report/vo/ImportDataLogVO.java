package com.loushuiyifan.report.vo;

import lombok.Data;

/**
 * 收入报账的 导入记录
 *
 * @author 漏水亦凡
 * @date 2017/9/26
 */
@Data
public class ImportDataLogVO {


    Long logId;

    String fileName;

    Integer num;

    Double sum;

    Long userId;

    String city;

    String action;

    String remark;

}
