package com.loushuiyifan.report.bean;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author 漏水亦凡
 * @date 2017/10/31
 */

@Data
@Table(name = "code_list_tax")
public class CodeListTax {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY ,
            generator = "select aweb_id.nextval from dual")
    Long rownums;


    String typeCode;
    String codeId;
    String codeName;
    Integer seq;
    String parentTypeCode;
    String parentCodeId;
    String flag;
    Integer codeLevel;

    String parentIds;
    String remark;
    Integer sorting;

}
