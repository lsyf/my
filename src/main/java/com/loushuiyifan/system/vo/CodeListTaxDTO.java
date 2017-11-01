package com.loushuiyifan.system.vo;

import lombok.Data;

/**
 * @author 漏水亦凡
 * @date 2017/9/12
 */
@Data
public class CodeListTaxDTO {
    private String codeId;
    private String codeName;
    private String parentCodeId;
    private Integer codeLevel;
}
