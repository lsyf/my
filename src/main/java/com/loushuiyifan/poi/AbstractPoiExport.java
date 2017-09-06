package com.loushuiyifan.poi;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

/**
 * @author 漏水亦凡
 * @create 2016-12-08 15:37.
 */
public abstract class AbstractPoiExport<E> implements PoiExport<E> {

    protected boolean hasTemp = false;//是否有模板
    protected boolean isXlsx = false;//是否是xlsx
    protected String inPath = null;
    protected InputStream ins = null;
    protected String outPath = null;//输出 文件及路径
    protected List<E> list = null;

    public AbstractPoiExport(String inPath, String outPath, List<E> list) {
        this.inPath = inPath;
        this.outPath = outPath;
        this.list = list;
        hasTemp = (inPath != null);
        isXlsx = (outPath.endsWith("xlsx"));
    }

    public AbstractPoiExport(String outPath, List<E> list) {
        this(null, outPath, list);
    }

    public AbstractPoiExport() {

    }

    /**
     * 导出
     */
    public void export() throws Exception {
        Workbook wb = getWorkbook();

        process(wb);

        FileOutputStream out = new FileOutputStream(outPath);
        wb.write(out);

        out.flush();
        out.close();
        wb.close();
    }

    private Workbook getWorkbook() throws Exception {
        Workbook wb = null;
        if (!hasTemp) {
            if (isXlsx) {
                wb = new SXSSFWorkbook();
            } else {
                wb = new HSSFWorkbook();
            }
        } else {//有输入流
            //WorkbookFactory:07默认采用XSSFWorkbook,03默认采用HSSFWorkbook,为了优化内存,弃用,采用SXSSFWorkbook
            //wb = WorkbookFactory.create(AbstractPoiExport.class.getResourceAsStream(inPath));

            //注意:inPath为相对路径
            if (isXlsx) {
                wb = new SXSSFWorkbook(new XSSFWorkbook(AbstractPoiExport.class.getResourceAsStream(inPath)));
            } else {
                wb = new HSSFWorkbook(AbstractPoiExport.class.getResourceAsStream(inPath));
            }
        }
        return wb;
    }

    protected abstract void process(Workbook wb) throws Exception;

}
