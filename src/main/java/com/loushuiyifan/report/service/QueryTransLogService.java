package com.loushuiyifan.report.service;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.loushuiyifan.report.dao.QueryTransLogDAO;
import com.loushuiyifan.report.serv.CodeListTaxService;
import com.loushuiyifan.report.serv.LocalNetService;
import com.loushuiyifan.report.serv.ReportDownloadService;
import com.loushuiyifan.report.vo.TransLogVO;

@Service
public class QueryTransLogService {
	private static final Logger logger = LoggerFactory.getLogger(QueryTransLogService.class);
	private static String[] str ={};
	private static String[] key ={};
	@Autowired
	QueryTransLogDAO queryTransLogDAO;
	
	@Autowired
	public LocalNetService localNetService;

	@Autowired
	public CodeListTaxService codeListTaxService;
	    
	@Autowired
    public ReportDownloadService reportDownloadService;
	
	/**
     * 稽核数据
     */
    public List<TransLogVO> list(String month, 
    		                     String latnId,
		    		             String incomeSource,
		    		             String taxtId
		    					) {

        List<TransLogVO> list =queryTransLogDAO.queryLogList(month, latnId, incomeSource, taxtId);
        if(list.size() ==0){
        	return null; 
        }
        return list;
    }
	
    /**
     *导出数据 
     *
     */
	public void downExcel(String month, 
						 String latnId,
			             String incomeSource,
			             Integer taxtId
						)throws Exception {
		
		String name =localNetService.getCodeName("local_net", latnId)+"_"+
				codeListTaxService.getAreaName(incomeSource)+"_"+month+"_"+
				codeListTaxService.getName(taxtId)	;
		List<Map> data =null;
	
        File file = new File("");
        reportDownloadService.store(file, month, name);
        
        
        		
        
		
	}
	
	
	
	
}
