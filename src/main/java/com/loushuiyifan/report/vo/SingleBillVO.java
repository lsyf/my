package com.loushuiyifan.report.vo;

import lombok.Data;

@Data
public class SingleBillVO {
	String phoneId; //电话号码
	String acctItemTypeId; //科目ID
	String name;//账目名称
	String acctItemTypeCode;//四级账目
	String acctItemTypeName;//四级账目名称
	Double unpayFee;//费用
	String subCode;//科目代码
	String verticalIndexName;//科目名称
	String seqNbr;//报表行号
	String queryType;//账单类型
}
