package com.loushuiyifan.report.bean;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author 漏水亦凡
 * @date 2017/9/26
 */
@Data
@Table(name = "ext_import_log")
public class ExtImportLog {

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

    String type;

}
