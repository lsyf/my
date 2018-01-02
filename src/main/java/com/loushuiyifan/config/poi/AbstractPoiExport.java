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

    protected Workbook workbook;

    //模板
    protected InputStream is = null;
    //导出
    protected OutputStream os = null;


    public AbstractPoiExport in(InputStream is) throws FileNotFoundException {
        this.is = is;
        hasTemp = true;
        return this;
    }

    public AbstractPoiExport out(OutputStream os) throws FileNotFoundException {
        this.os = os;
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
        Workbook wb = createWorkbook();

        process(wb);

        wb.write(os);

        os.flush();
        os.close();
        wb.close();
    }

    private Workbook createWorkbook() throws IOException {
        if (!hasTemp) {
            if (isXlsx) {
                workbook = new SXSSFWorkbook();
            } else {
                workbook = new HSSFWorkbook();
            }
        } else {//有输入流
            //WorkbookFactory:07默认采用XSSFWorkbook,03默认采用HSSFWorkbook,为了优化内存,弃用,采用SXSSFWorkbook
            //wb = WorkbookFactory.create(is);

            if (isXlsx) {
                workbook = new SXSSFWorkbook(new XSSFWorkbook(is));
            } else {
                workbook = new HSSFWorkbook(is);
            }
        }
        return workbook;
    }

    protected abstract void process(Workbook wb) throws Exception;


    public Workbook getWorkbook() {
        return workbook;
    }
}
