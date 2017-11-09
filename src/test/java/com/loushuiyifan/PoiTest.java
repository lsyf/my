package com.loushuiyifan;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.NPOIFSFileSystem;
import org.apache.poi.ss.usermodel.Sheet;

import java.io.File;
import java.io.FileOutputStream;

/**
 * @author 漏水亦凡
 * @date 2017/11/9
 */
public class PoiTest {
    public static void main(String[] args)throws Exception {
        NPOIFSFileSystem fs = new NPOIFSFileSystem(new File("D:\\report\\1.xls"));
        HSSFWorkbook wb = new HSSFWorkbook(fs);

        Sheet sheet = wb.cloneSheet(0);
        wb.setSheetName(1,"abc");
        wb.write(new FileOutputStream("D:\\report\\sample1.xls"));

        fs.close();
    }
}
