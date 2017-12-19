package com.loushuiyifan.report.serv;

import java.io.ByteArrayOutputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.loushuiyifan.config.poi.AbstractPoiExport;

public class TaxExportServ extends AbstractPoiExport<Map<String, String>>{

	//列名(id+name)
    protected List<Map<String, String>> cols;
    //行数据
    protected List<Map<String, String>> rows;
    
    public TaxExportServ column(List<Map<String, String>> columns) {
        this.cols = columns;
        return this;
    }

    public TaxExportServ row(List<Map<String, String>> rows) {
        this.rows = rows;
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

        Sheet sheet = wb.createSheet("数据");

        //首先添加表头
        Row titleRow = sheet.createRow(0);
        for (int i = 0; i < cols.size(); i++) {
        	 Map<String, String> title = cols.get(i);
   
        	String name = title.get("name");
        	titleRow.createCell(0).setCellValue("行次");
        	titleRow.createCell(i+1).setCellValue(name);
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
            	row.createCell(0).setCellValue(rowNum-1);                
                row.createCell(j+1).setCellValue(data);
            }
        }

    }
    
}
