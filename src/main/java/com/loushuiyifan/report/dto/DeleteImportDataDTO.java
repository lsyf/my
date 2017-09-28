package com.loushuiyifan.report.dto;

import lombok.Data;

/**
 * @author 漏水亦凡
 * @date 2017/9/26
 */
@Data
public class DeleteImportDataDTO {
    Long userId;
    Long logId;
    Integer rtnCode;
    String rtnMeg;
}
