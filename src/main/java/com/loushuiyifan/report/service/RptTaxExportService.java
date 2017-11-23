package com.loushuiyifan.report.service;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.loushuiyifan.report.dao.RptTaxExportDAO;
import com.loushuiyifan.report.serv.CommonExportServ;

@Service
public class RptTaxExportService {
	private static final Logger logger = LoggerFactory.getLogger(RptTaxExportService.class);
	@Autowired
	RptTaxExportDAO rptTaxExportDAO;
	
	
	/**
     * 导出数据
     */
    public byte[] export(String month) throws Exception {

        List<Map<String, String>> list = rptTaxExportDAO.queryTaxData(month);
        String[] keys = {"month", "nssbh", "swkm", "crncy","swsrje", "swsrse"};
        String[] titles = {"账期", "纳税识别号", "税务科目", "会计收入","税务收入", "税额"};

        byte[] data = new CommonExportServ().column(keys, titles)
							                .data(list)
							                .exportData();

        return data;
    }
	
    public String getFileName(String month) {
        return "税号税目汇总_"+month+ ".xls";
    }
	
	
	
	
	
	
}
