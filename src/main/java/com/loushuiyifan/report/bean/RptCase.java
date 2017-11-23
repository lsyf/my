package com.loushuiyifan.report.bean;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author 漏水亦凡
 * @date 2017/11/20
 */

@Table(name = "rpt_case")
@Data
public class RptCase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY,
            generator = "select SEQ_RPTCASE_ID.nextval from dual")
    Long rptCaseId;
    Long reportNo;
    Long latnId;
    String incomeSoure;
    Long processId;
    String acctMonth;
    String rptCaseStatus;
    String createUserid;
    Date createDate;
    String custGroup;
    String odsId;
    Long c5Id;
    Integer taxMark;
    String remark;

    String type;


}
