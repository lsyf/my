package com.loushuiyifan.report.bean;

import lombok.Data;

import javax.persistence.Table;

/**
 * 客户群表
 *
 * @author 漏水亦凡
 * @date 2017/11/7
 */
@Data
@Table(name = "rpt_repfield_def_channel")
public class RptRepfieldDefChannel {

    Integer verticalCode;
    String indexCode;
    String parentIndexId;
    String indexName;
    String indexAllis;
    String rptNo;
    Integer groupId;
    Integer indexId;
    String indentationName;
    String statTypeId;


}
