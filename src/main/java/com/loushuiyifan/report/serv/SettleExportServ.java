package com.loushuiyifan.report.serv;

import java.io.ByteArrayOutputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.loushuiyifan.config.poi.AbstractPoiExport;

public class SettleExportServ extends AbstractPoiExport<Map<String, String>>{

	//列名(id+name)
    protected List<Map<String, String>> cols;
    //行数据
    protected List<Map<String, String>> rows;
    
    protected List<Map<String, String>> cols2;
    //行数据
    protected List<Map<String, String>> rows2;
    public SettleExportServ column(List<Map<String, String>> columns, List<Map<String, String>> columns2) {
        this.cols = columns;
        this.cols2 = columns2;
        return this;
    }

    public SettleExportServ row(List<Map<String, String>> rows, List<Map<String, String>> rows2) {
        this.rows = rows;
        this.rows2 = rows2;
        return this;
    }

    public byte[] exportData() throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        out(os);
        super.export();
        return os.toByteArray();
    }
    
    @Override
    protected void process(Workbook wb) throws Exception {
    	//wb.setSheetName(0, "原始数据");//设置sheet名
    	//Sheet sheet = wb.getSheetAt(0);
        Sheet sheet1 = wb.createSheet("原始数据");
        Sheet sheet2 = wb.createSheet("处理后数据");
    	processSheet(sheet1,cols,rows);
    	processSheet(sheet2,cols2,rows2);
    }
    
    protected void processSheet(Sheet sheet,
    		                    List<Map<String, String>> cols,
    		                    List<Map<String, String>> rows){
    	
    	//首先添加表头
        Row titleRow = sheet.createRow(0);
        for (int i = 0; i < cols.size(); i++) {
        	 Map<String, String> title = cols.get(i);
   
        	String name = title.get("name");
        	titleRow.createCell(i).setCellValue(name);
        }
        
        //然后填充数据
        for (int i = 0; i < rows.size(); i++) {
        	int rowNum = i+1;
            Row row = sheet.createRow(rowNum++);
            
            Map<String, String> map = rows.get(i);
            for (int j = 0; j < cols.size(); j++) {
            	Map<String, String> title = cols.get(j);
            	String id = title.get("id");
            	String data = map.get(id);
                row.createCell(j).setCellValue(data);
            }
        }
    	
    }
    
       
}