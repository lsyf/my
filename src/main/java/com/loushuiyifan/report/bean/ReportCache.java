package com.loushuiyifan.report.bean;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author 漏水亦凡
 * @date 2017/11/20
 */

@Table(name = "report_cache")
@Data
public class ReportCache {

    @Id
    Long rptCaseId;
    byte[] htmlData;
    String filePath;


}
