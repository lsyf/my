package com.loushuiyifan.poi;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.filesystem.NPOIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.util.List;

/**
 * @author 漏水亦凡
 * @create 2016-12-08 15:37.
 */
public abstract class AbstractPoiRead<E> implements PoiRead<E> {

    private DataFormatter dataFormatter = new DataFormatter();
    protected String filePath = null;

    public AbstractPoiRead(String filePath) {
        this.filePath = filePath;
    }

    /**
     * 根据 excel后缀区分 xls和xlsx
     *
     * @return
     * @throws Exception
     */
    public List<E> read() throws Exception {
        return filePath.endsWith("x") ? readXLSX() : readXLSX();
    }

    /**
     * 读取 xlsx
     *
     * @return
     * @throws Exception
     */
    public List<E> readXLSX() throws Exception {
        OPCPackage pkg = OPCPackage.open(new File(filePath));
        XSSFWorkbook wb = new XSSFWorkbook(pkg);

        List<E> list = process(wb);

        pkg.close();
        return list;
    }

    /**
     * 读取 xls
     *
     * @return
     * @throws Exception
     */
    public List<E> readXLS() throws Exception {
        NPOIFSFileSystem fs = new NPOIFSFileSystem(new File(filePath));
        HSSFWorkbook wb = new HSSFWorkbook(fs);

        List<E> list = process(wb);

        fs.close();
        return list;
    }


    /**
     * 数据处理
     *
     * @param wb
     * @return
     */
    protected abstract List<E> process(Workbook wb);


    /**
     * 获取单元格数据
     *
     * @param cell
     * @return
     */
    protected String getCellData(Cell cell) {
        return dataFormatter.formatCellValue(cell);
    }
}
