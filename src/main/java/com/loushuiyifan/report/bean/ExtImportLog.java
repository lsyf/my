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
    private Long logId;

    private Integer latnId;

    //TODO 数据库字段拼写错误
    private String incomeSoure;

    private String fileName;

    private String acctMonth;

    private Integer userId;

    private Date importDate;

    private String status;

    private String exportDesc;

}
