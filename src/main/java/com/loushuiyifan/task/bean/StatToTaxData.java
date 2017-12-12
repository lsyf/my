package com.loushuiyifan.task.bean;

import javax.persistence.Table;

import lombok.Data;

@Data
@Table(name="stat_to_tax")
public class StatToTaxData {

	private String documSerNbr;
	private Long voucherRowNbr;
	private String gjahr;
	private String monat;
	private String bukrs;
	private String prctr;
	private String wwa04;
	private String nssbh;
	private String wwa09;
	private String swkm;
	private String sfyywsr;
	private String srlx;
	private String sysz;
	private String sbfs;
	private String jsff;
	private String yslb;
	private String sfjzjt;
	private Long slv;
	private Double crncyAmt;
	private Double swsrje;
	private Double swsrse;
	private String sfstxs;
	private String swtzlx;
	private String swtzyy;
	private String jfsssj;
	private String jsswkn;
	private Long jfsl;
}
