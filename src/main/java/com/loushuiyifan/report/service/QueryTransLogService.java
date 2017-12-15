package com.loushuiyifan.report.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.loushuiyifan.report.dao.QueryTransLogDAO;
import com.loushuiyifan.report.exception.ReportException;
import com.loushuiyifan.report.serv.CodeListTaxService;
import com.loushuiyifan.report.serv.CommonExportServ;
import com.loushuiyifan.report.serv.LocalNetService;
import com.loushuiyifan.report.serv.ReportDownloadService;
import com.loushuiyifan.report.vo.TransLogVO;

@Service
public class QueryTransLogService {
    private static final Logger logger = LoggerFactory.getLogger(QueryTransLogService.class);
    private static String[] str = {};
    private static String[] key = {};
    @Autowired
    QueryTransLogDAO queryTransLogDAO;

    @Autowired
    public LocalNetService localNetService;

    @Autowired
    public CodeListTaxService codeListTaxService;

    @Autowired
    public ReportDownloadService reportDownloadService;

    /**
     * 查询数据
     */
    public List<TransLogVO> list(String month,
                                 String latnId,
                                 String incomeSource,
                                 String taxtId) {


        List<TransLogVO> list =queryTransLogDAO.queryLogList(month, latnId, incomeSource, taxtId);

        return list;
    }

    /**
     * 导出数据
     */
    public byte[] export(String month,
                         String latnId,
                         String incomeSource,
                         String taxtId) throws Exception {

        List<Map<String, String>> list = queryTransLogDAO.queryLogForMap(month, latnId, incomeSource, taxtId);

        String[] keys = {"month", "incomeSource", "incomeName", "codeName",
                "batchId", "subId", "status", "createDate",
                "lstUpd", "voucherCode"};
        String[] titles = {"账期", "收入来源编码", "收入来源名称", "本地网名称",
                "批次号", "版本号", "状态", "创建时间",
                "最后修改时间", "凭证号"};

        byte[] data = new CommonExportServ().column(keys, titles)
                .data(list)
                .exportData();

        return data;
    }

    /**
     * 电子档案下载
     */
    public String downLoadFile(String batchId, String month) throws IOException {
        String fileName = queryTransLogDAO.queryFileName(batchId);
        if (fileName == null) {
            throw new ReportException("电子档案文件为空");
        }
        //TODO 待确定电子档案生成gz文件目录,文件按月份归档
        Path path = Paths.get("");
        if (!Files.exists(path)) {
            Files.createDirectory(path);
        }
        return path.resolve(fileName).toString();
    }

    public String getFileName(String month, String latnId, String incomeSource, String taxtId) {
    	String isName = codeListTaxService.getAreaName(latnId, "local_net");
                
    	String areaName = codeListTaxService.
                getNameByTypeAndData("local_net", incomeSource).getCodeName();
    	
    	return "收入来源传输日志_"+areaName+"_"+isName+ "_" +getType(taxtId)+".xls";
    }
    
    public String getType(String taxtId){
    	String s ="";
    	if("0".equals(taxtId)){
    		s ="凭证";    		
    	}else{
    		s ="电子档案";
    	}
    	return s;
    }
}
