package com.loushuiyifan.report.serv;

import com.loushuiyifan.common.util.SFTPUtils;
import com.loushuiyifan.report.exception.ReportException;
import org.apache.commons.io.FilenameUtils;

/**
 * @author 漏水亦凡
 * @date 2017/12/13
 */

public class FileService {

    private static final String SFTP_HOST = "134.96.246.21";
    private static final String SFTP_PORT = "22";
    private static final String SFTP_USERNAME = "rptadmin";
    private static final String SFTP_PASSWORD = "rpt!1234";


    public static boolean push(String filePath) {
        SFTPUtils sftp = null;
        try {
            sftp = new SFTPUtils(SFTP_HOST, SFTP_USERNAME, SFTP_PASSWORD);

            String path = FilenameUtils.getFullPath(filePath);
            path = path.substring(0, path.length() - 1);
            path = path.replaceAll("\\\\", "/");

            String fileName = FilenameUtils.getName(filePath);

            sftp.connect();
            sftp.uploadFile(path, fileName, path, fileName);
        } catch (Exception e) {
            e.printStackTrace();
//            throw new ReportException("上传到文件主机失败: " + filePath);
            return false;
        } finally {
            sftp.disconnect();
        }
        return true;
    }

    public static boolean pull(String filePath) {
        SFTPUtils sftp = null;
        try {
            sftp = new SFTPUtils(SFTP_HOST, SFTP_USERNAME, SFTP_PASSWORD);
            
            filePath = filePath.replaceAll("\\\\", "/");
            String path = FilenameUtils.getFullPath(filePath);
            String fileName = FilenameUtils.getName(filePath);

            sftp.connect();
            sftp.downloadFile(path, fileName, path, fileName);
        } catch (Exception e) {
            e.printStackTrace();
//            throw new ReportException("从文件主机下载失败: " + filePath);
            return false;
        } finally {
            sftp.disconnect();
        }
        return true;
    }

    public static boolean pull(String[] filePaths) {
        SFTPUtils sftp = null;
        try {
            sftp = new SFTPUtils(SFTP_HOST, SFTP_USERNAME, SFTP_PASSWORD);

            String path = FilenameUtils.getFullPath(filePaths[0]);

            sftp.connect();
            sftp.batchDownLoadFile(filePaths, path);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ReportException("从文件主机下载失败: " + filePaths);
        } finally {
            sftp.disconnect();
        }
        return true;
    }

}
