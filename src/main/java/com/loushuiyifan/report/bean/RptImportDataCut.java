package com.loushuiyifan.report.bean;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Table(name = "dim_cut_cfg", schema = "charge")
public class RptImportDataCut {

    @Id
    String ruleId;
    Integer latnId;
    String incomeSource;
    Integer shareType;
    String express;
    @Column(name = "is_active")
    String activeFlag;
    String chgWho;
    Date lstUpd;
    Long groupId;
}
