package com.loushuiyifan.report.vo;

import lombok.Data;

@Data
public class RuleConfigVO {

	Long logId;
	String month;
	String latnName; //本地网
	String codeName; //营业区
	String cardType;  //卡类型
	Double discount; //折扣率
	String inactiveAmount; //库存过期卡金额
	String platformAmount; //平台过期卡金额 
	String chgWho; //修改人
	String lstUpd; //修改时间
}
