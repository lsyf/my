package com.loushuiyifan.report.vo;

import lombok.Data;

/**
 * 审核列表
 * @author 漏水亦凡
 * @date 2017/11/21
 */
@Data
public class RptAuditVO {
    Integer seqNo;
    String descText;
    String time;
    String auditorName;
    String auditStatus;
    String auditComment;
}
