package com.loushuiyifan.poi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * <p>Description: </p>
 * <p>Title: ZipFilesUtil.java</p>
 * <p>Copyright: Copyright (c) 2016</p>
 * <p>Company: zjhcSoft</p>
 * <p>Date: 2016年12月13日 下午2:33:44</p>
 *
 * @author Administrator
 * @version 1.0
 */
public class ZipFilesUtil {
    /**
     * @param srcfile  压缩前文件数组
     * @param zipfile  压缩后文件
     * @param isDelete 是否删除压缩前文件
     */
    public static void zipFiles(File[] srcfile, File zipfile, boolean isDelete) {
        byte[] buf = new byte[10 * 1024];
        ZipOutputStream out = null;
        FileInputStream in = null;
        try {
            out = new ZipOutputStream(new FileOutputStream(zipfile));
            for (int i = 0; i < srcfile.length; i++) {
                in = new FileInputStream(srcfile[i]);
                out.putNextEntry(new ZipEntry(srcfile[i].getName()));
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                out.closeEntry();
                in.close();
            }

            if (isDelete) {
                for (int i = 0; i < srcfile.length; i++) {
                    if (srcfile[i].exists()) {
                        srcfile[i].delete();
                    }
                    ;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }
}
