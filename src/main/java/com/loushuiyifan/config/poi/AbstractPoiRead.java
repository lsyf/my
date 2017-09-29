package com.loushuiyifan.config.poi;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.filesystem.NPOIFSFileSystem;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.util.List;

/**
 * @author 漏水亦凡
 * @create 2016-12-08 15:37.
 */
public abstract class AbstractPoiRead<E> implements PoiRead<E> {

    private DataFormatter dataFormatter = new DataFormatter();
    protected File file = null;

    protected int startX = 0;
    protected int startY = 1;
    protected boolean isMulti = false;


    /**
     * 设置读取的起始位置
     *
     * @param startX
     * @param startY
     */
    public AbstractPoiRead startWith(int startX, int startY) {
        this.startX = startX;
        this.startY = startY;
        return this;
    }

    /**
     * 是否多sheet
     *
     * @param isMulti
     * @return
     */
    public AbstractPoiRead multi(boolean isMulti) {
        this.isMulti = isMulti;
        return this;
    }


    /**
     * 加载excel
     *
     * @param file
     * @return
     */
    public AbstractPoiRead load(File file) {
        this.file = file;
        return this;
    }

    /**
     * 加载excel
     *
     * @param filePath
     * @return
     */
    public AbstractPoiRead load(String filePath) {
        this.file = new File(filePath);
        return this;
    }

    /**
     * 根据 excel后缀区分 xls和xlsx
     *
     * @return
     * @throws Exception
     */
    public List<E> read() throws Exception {
        return file.getName().endsWith("xlsx") ? readXLSX() : readXLS();
    }

    /**
     * 读取 xlsx
     *
     * @return
     * @throws Exception
     */
    public List<E> readXLSX() throws Exception {
        OPCPackage pkg = OPCPackage.open(file);
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
        NPOIFSFileSystem fs = new NPOIFSFileSystem(file);
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
     * @param evaluator
     * @return
     */
    protected String getCellData(Cell cell, FormulaEvaluator evaluator) {

        return dataFormatter.formatCellValue(cell,evaluator);
    }

    /**
     * 获取单元格的值
     */
    @Deprecated
    protected String getXLSCellValue(Cell cell) {
        String cellValue = "";
        DataFormatter formatter = new DataFormatter();
        if (cell != null) {
            //判断单元格数据的类型，不同类型调用不同的方法
            switch (cell.getCellType()) {
                //数值类型
                case Cell.CELL_TYPE_NUMERIC:
                    //进一步判断 ，单元格格式是日期格式
                    if (DateUtil.isCellDateFormatted(cell)) {
                        cellValue = formatter.formatCellValue(cell);
                    } else {
                        //数值
                        double value = cell.getNumericCellValue();
                        int intValue = (int) value;
                        cellValue = value - intValue == 0 ? String.valueOf(intValue) : String.valueOf(value);
                    }
                    break;
                case Cell.CELL_TYPE_STRING:
                    cellValue = cell.getStringCellValue();
                    break;
                case Cell.CELL_TYPE_BOOLEAN:
                    cellValue = String.valueOf(cell.getBooleanCellValue());
                    break;
                //判断单元格是公式格式，需要做一种特殊处理来得到相应的值
                case Cell.CELL_TYPE_FORMULA: {
                    try {
                        cellValue = String.valueOf(cell.getNumericCellValue());
                    } catch (IllegalStateException e) {
                        cellValue = String.valueOf(cell.getRichStringCellValue());
                    }

                }
                break;
                case Cell.CELL_TYPE_BLANK:
                    cellValue = "";
                    break;
                case Cell.CELL_TYPE_ERROR:
                    cellValue = "";
                    break;
                default:
                    cellValue = cell.toString().trim();
                    break;
            }
        }
        return cellValue.trim();
    }

}
