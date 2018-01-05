package com.loushuiyifan.report.service;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.loushuiyifan.report.dao.QueryTransLogDAO;
import com.loushuiyifan.report.exception.ReportException;
import com.loushuiyifan.report.serv.CodeListTaxService;
import com.loushuiyifan.report.serv.CommonExportServ;
import com.loushuiyifan.report.serv.FileService;
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
        if (list == null ||list.size()==0) {
            throw new ReportException("查询数据为空！");
        }
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

        String[] keys = {"MONTH", "INCOMESOURCE", "INCOMENAME", "CODENAME",
                "BATCHID", "SUBID", "STATUS", "CREATEDATE",
                "LSTUPD", "VOUCHERCODE"};
        String[] titles = {"账期", "收入来源编码", "收入来源名称", "本地网名称",
                "批次号", "版本号", "状态", "创建时间",
                "最后修改时间", "凭证号"};

        byte[] data = new CommonExportServ().column(keys, titles)
                .data(list).type(null)
                .exportData();

        return data;
    }

    /**
     * 电子档案下载
     */
    public void downLoadFile(String batchId, String month) {
        
    	
    		String fileName = queryTransLogDAO.queryFileName(batchId);
            if (fileName == null) {
                throw new ReportException("电子档案文件为空");
            }
            
            //待确定电子档案生成gz文件目录,文件按月份归档
            Path path = Paths.get("/report/files/fileMonth/",fileName);
                
            try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(path.toFile()))){
    			
            	
            	String filePath = path.resolve(fileName).toString();
                filePath = filePath.replaceAll("\\\\", "/");
            	//首先批量下载所有文件
                FileService.pull(filePath);
                
                File f = new File(filePath);
                if (!f.exists()) {
                	throw new ReportException("电子档案文件下载失败"); 
                }
                
                BufferedInputStream bis = new BufferedInputStream(new FileInputStream(f));
                
                String filename = FilenameUtils.getName(filePath);

                ZipEntry entry = new ZipEntry(filename);
                zos.putNextEntry(entry);

                int len;
                byte[] buffer = new byte[8192];
                while ((len = bis.read(buffer)) != -1) {
                    zos.write(buffer, 0, len);
                }
                
                zos.closeEntry();
                bis.close();           
                zos.close();
                               
            } catch (Exception e) {
    			e.printStackTrace();
    			throw new ReportException("下载失败" + e.getMessage());
    		}
		
    	
        
        
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
