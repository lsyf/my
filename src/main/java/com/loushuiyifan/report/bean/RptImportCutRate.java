package com.loushuiyifan.report.bean;

import lombok.Data;

import javax.persistence.Table;

@Data
@Table(name = "dim_cut_rate", schema = "charge")
public class RptImportCutRate {
    private String ruleId;
    private Integer bureauId; // c5
    private Integer areaId; // c4
    private Double rate;
    private String acctMonth;
}
