package com.loushuiyifan.config.poi;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.WorkbookUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * poi工具类
 *
 * @author 漏水亦凡
 * @create 2016-11-25 15:46.
 */
public class PoiUtils {
    private static final Logger LOG = LoggerFactory.getLogger(PoiUtils.class);
    private static String filePath = "d:/a.xlsx";

    public static CellStyle valueCellStyle(Workbook wb) {
        CellStyle cellStyle = wb.createCellStyle();
        DataFormat df = wb.createDataFormat();
        cellStyle.setDataFormat(df.getFormat("0.00"));
        return cellStyle;
    }

    /**
     * 创建的sheet名，非法字符替换成下划线
     *
     * @param name 原文件名
     * @return
     */
    public static String createSheetName(String name) {
        return WorkbookUtil.createSafeSheetName(name, '_');
    }

    /**
     * 单sheet整行拷贝
     *
     * @param startRow  源起始行(起始为0)
     * @param endRow    源结束行
     * @param pPosition 目标起始行位置(起始为0)
     * @param sheet
     */
    public static void copyRows(Sheet sheet, int startRow, int endRow, int pPosition) {
        int pStartRow = startRow;
        int pEndRow = endRow;
        int targetRowFrom;
        int targetRowTo;
        int columnCount;
        CellRangeAddress region = null;
        int i;
        int j;
        LOG.debug("起始行 %d 到 %d 拷贝到目标行 %d ", pStartRow, pEndRow, pPosition);
        if (pStartRow < 0 || pEndRow < 0) {
            LOG.error("拷贝源起始行 或 结束行错误");
            return;
        }
        // 拷贝合并的单元格
        for (i = 0; i < sheet.getNumMergedRegions(); i++) {
            region = sheet.getMergedRegion(i);
            if ((region.getFirstRow() >= pStartRow) && (region.getLastRow() <= pEndRow)) {
                targetRowFrom = region.getFirstRow() - pStartRow + pPosition;
                targetRowTo = region.getLastRow() - pStartRow + pPosition;
                CellRangeAddress newRegion = region.copy();
                newRegion.setFirstRow(targetRowFrom);
                newRegion.setFirstColumn(region.getFirstColumn());
                newRegion.setLastRow(targetRowTo);
                newRegion.setLastColumn(region.getLastColumn());
                sheet.addMergedRegion(newRegion);
            }
        }
        // 设置列宽
        for (i = pStartRow; i <= pEndRow; i++) {
            Row sourceRow = sheet.getRow(i);
            columnCount = sourceRow.getLastCellNum();
            if (sourceRow != null) {
                Row newRow = sheet.createRow(pPosition - pStartRow + i);
                newRow.setHeight(sourceRow.getHeight());
                for (j = 0; j < columnCount; j++) {
                    Cell templateCell = sourceRow.getCell(j);
                    if (templateCell != null) {
                        Cell newCell = newRow.createCell(j);
                        copyCell(templateCell, newCell);
                    }
                }
            }
        }
    }

    /**
     * 拷贝单元格
     *
     * @param srcCell
     * @param distCell
     */
    private static void copyCell(Cell srcCell, Cell distCell) {
        distCell.setCellStyle(srcCell.getCellStyle());
        if (srcCell.getCellComment() != null) {
            distCell.setCellComment(srcCell.getCellComment());
        }

        CellType srcCellType = srcCell.getCellTypeEnum();
        distCell.setCellType(srcCellType);
        switch (srcCellType) {
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(srcCell)) {
                    distCell.setCellValue(srcCell.getDateCellValue());
                } else {
                    distCell.setCellValue(srcCell.getNumericCellValue());
                }
                break;
            case STRING:
                distCell.setCellValue(srcCell.getRichStringCellValue());
                break;
            case BOOLEAN:
                distCell.setCellValue(srcCell.getBooleanCellValue());
                break;
            case ERROR:
                distCell.setCellErrorValue(srcCell.getErrorCellValue());
                break;
            case FORMULA:
                distCell.setCellFormula(srcCell.getCellFormula());
                break;
            case BLANK:
                // nothing21
                break;
            default:
        }
    }

}
