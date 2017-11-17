package com.loushuiyifan.report.service;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.loushuiyifan.report.dao.RptQueryFundsFeeDAO;
import com.loushuiyifan.report.serv.CommonExportServ;
import com.loushuiyifan.report.vo.FundsFeeVO;

/**
 * 
 * @author yuxk
 * @date 2017-11-15
 */
@Service
public class RptQueryFundsFeeService {
	private static final Logger logger = LoggerFactory.getLogger(RptQueryFundsFeeService.class);
	@Autowired
	RptQueryFundsFeeDAO rptQueryFundsFeeDAO;
	
	/**
	 * 查询
	 */
	public List<FundsFeeVO> list(String month, String reportId,String prctrName){
		
		List<FundsFeeVO> list =rptQueryFundsFeeDAO.listFundsFee(month, reportId, prctrName);
	
		return list;
	}
	
	/**
     * 导出数据
     */
    public byte[] export(String month,String reportId,String prctrName) throws Exception {

        List<Map<String, String>> list = rptQueryFundsFeeDAO.queryLogForMap(month, reportId, prctrName);

        String[] keys = {"indexCode", "indexName", "balance", "prctr", "prctrName", "sapFinCode"};
        
        String[] titles = {"指标编码", "指标名称", "金额", "利润中心编码", "利润中心简称", "SAP科目编码"};

        byte[] data = new CommonExportServ().column(keys, titles)
                .data(list)
                .exportData();

        return data;
    }
	
	
	
	
}
