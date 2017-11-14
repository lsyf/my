package com.loushuiyifan.config.poi;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;

/**
 * @author 漏水亦凡
 * @create 2016-12-08 15:37.
 */
public abstract class AbstractPoiExport<E> implements PoiExport<E> {

    protected boolean hasTemp = false;//是否有模板
    protected boolean isXlsx = false;//是否是xlsx
    protected boolean isMulti = false; //是否多sheet

    //模板
    protected InputStream is = null;
    //导出
    protected OutputStream os = null;


    public AbstractPoiExport template(File file) throws FileNotFoundException {
        this.is = new FileInputStream(file);
        hasTemp = true;
        return this;
    }

    public AbstractPoiExport out(File file) throws FileNotFoundException {
        os = new FileOutputStream(file);
        return this;
    }

    public AbstractPoiExport xlsx(boolean isXlsx) {
        this.isXlsx = isXlsx;
        return this;
    }

    public AbstractPoiExport multi(boolean isMulti) {
        this.isMulti = isMulti;
        return this;
    }


    /**
     * 导出
     */
    public void export() throws Exception {
        Workbook wb = getWorkbook();

        process(wb);

        wb.write(os);

        os.flush();
        os.close();
        wb.close();
    }

    private Workbook getWorkbook() throws IOException {
        Workbook wb;
        if (!hasTemp) {
            if (isXlsx) {
                wb = new SXSSFWorkbook();
            } else {
                wb = new HSSFWorkbook();
            }
        } else {//有输入流
            //WorkbookFactory:07默认采用XSSFWorkbook,03默认采用HSSFWorkbook,为了优化内存,弃用,采用SXSSFWorkbook
            //wb = WorkbookFactory.create(is);

            if (isXlsx) {
                wb = new SXSSFWorkbook(new XSSFWorkbook(is));
            } else {
                wb = new HSSFWorkbook(is);
            }
        }
        return wb;
    }

    protected abstract void process(Workbook wb) throws Exception;



}
