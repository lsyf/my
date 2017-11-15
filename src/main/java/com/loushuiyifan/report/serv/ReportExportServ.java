package com.loushuiyifan.report.serv;

import com.loushuiyifan.config.poi.AbstractPoiExport;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.*;

/**
 * 报表导出
 *
 * @author 漏水亦凡
 * @date 2017/9/25
 */
public abstract class ReportExportServ<E> extends AbstractPoiExport<E> {


    //列名(id+name)
    protected List<Map<String, String>> reportColumns;
    //行数据
    protected List<Map<String, String>> reportRows;

    //数据(sheetName->data[key->v])
    protected LinkedHashMap<String, E> reportDatas;

    public ReportExportServ column(List<Map<String, String>> columns) {
        this.reportColumns = columns;
        return this;
    }

    public ReportExportServ row(List<Map<String, String>> rows) {
        this.reportRows = rows;
        return this;
    }

    public ReportExportServ data(LinkedHashMap<String, E> datas) {
        this.reportDatas = datas;
        isMulti = datas.size() > 1;
        return this;
    }

    public ReportExportServ template(String path) throws FileNotFoundException {
        super.in(new FileInputStream(path));
        return this;
    }

    public ReportExportServ out(String path) throws FileNotFoundException {
        super.out(new FileOutputStream(path));
        return this;
    }


    @Override
    protected void process(Workbook wb) throws Exception {

        //首先添加表头,复制sheet
        List<String> ids = new ArrayList<>();
        int index = 0;
        for (String key : reportDatas.keySet()) {
            if (index == 0) {
                wb.setSheetName(0, key);//设置sheet名
                Sheet sheet = wb.getSheetAt(0);
                ids = processTitle(sheet, reportColumns);//设置表头
            } else {
                wb.cloneSheet(0);//复制sheet
                wb.setSheetName(index, key);
            }
            index++;
        }

        //然后填充数据
        Iterator<String> it = reportDatas.keySet().iterator();
        while (it.hasNext()) {
            String name = it.next();
            E data = reportDatas.get(name);

            Sheet sheet = wb.getSheet(name);
            processSheet(sheet, ids, reportRows, data);

            it.remove();
        }

    }

    abstract protected List<String> processTitle(Sheet sheet,
                                                 List<Map<String, String>> columns) throws Exception;

    abstract protected void processSheet(Sheet sheet,
                                         List<String> columns,
                                         List<Map<String, String>> rows,
                                         E datas) throws Exception;

}
