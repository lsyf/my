package com.loushuiyifan.report.bean;

import lombok.Data;

import javax.persistence.Table;
import java.util.Date;

@Data
@Table(name = "dim_sub_group", schema = "charge")
public class RptImportDataGroup {

    Long groupId;
    Integer latnId;
    String groupName;
    String subCode;
    String subName;
    Integer userId;
    Date lstUpd;
}
