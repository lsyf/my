package com.loushuiyifan.report.bean;

import javax.persistence.Table;

import lombok.Data;

@Data
@Table(name="stat_to_group")
public class StatToGroup {
	private String batch_id;
	private String sub_id;
	private String acct_month;
	private Long latn_id;
	private String rpt_type;
	private Long rpt_case_id;
	private String create_date;
	private String status;
	private String lst_upd;
	private String income_source;
}
