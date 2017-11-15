package com.loushuiyifan.report.bean;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Table(name = "LOG_rpt_import_data_ssc")
public class ExtImportYccyLog {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY,
            generator = "select SEQ_IMPORT_LOG_ID.nextval from dual")
	Long logId;
    Integer latnId;
    String incomeSoure;
    String fileName;
    String acctMonth;
    Integer userId;
    Date importDate;
    String status;
    String exportDesc;


}
