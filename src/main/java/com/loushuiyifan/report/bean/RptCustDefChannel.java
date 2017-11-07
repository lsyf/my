package com.loushuiyifan.report.bean;

import lombok.Data;

import javax.persistence.Table;

/**
 * @author 漏水亦凡
 * @date 2017/11/7
 */
@Data
@Table(name = "rpt_cust_def_channel")
public class RptCustDefChannel {

    Integer horizonCode;
    String indexCode;
    String parentIndex_;
    String indexName;
    String rptNo;
    Integer groupId;

}
