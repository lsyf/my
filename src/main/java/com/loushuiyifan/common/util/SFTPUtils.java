package com.loushuiyifan.common.util;

import com.jcraft.jsch.*;
import lombok.Data;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

/**
 * @author 漏水亦凡
 * @date 2017/12/11
 */

@Data
public class SFTPUtils {
    private static Logger log = LoggerFactory.getLogger(SFTPUtils.class);

    private String host;//服务器连接ip
    private String username;//用户名
    private String password;//密码
    private int port = 22;//端口号
    private ChannelSftp sftp = null;
    private Session sshSession = null;


    public static final String SFTP_HOST = "134.96.246.22";
    public static final String SFTP_PORT = "22";
    public static final String SFTP_USERNAME = "rptadmin";
    public static final String SFTP_PASSWORD = "rpt!1234";
    public static final int SFTP_DEFAULT_PORT = 22;
    public static final String SFTP_LOCAL_FILE = "d:/report/111.xls";
    public static final String SFTP_REMOTE_FILE = "/report/111.xls";
    public static final String SFTP_LOCAL = "d:/report";
    public static final String SFTP_REMOTE = "/report";


    /**
     * 测试
     */
    public static void main(String[] args) {
        SFTPUtils sftp = null;

        List<String> filePathList = new ArrayList<String>();
        try {
            sftp = new SFTPUtils(SFTP_HOST, SFTP_USERNAME, SFTP_PASSWORD);
            sftp.connect();
            // 下载
            System.out.println(sftp.batchDownLoadFile(SFTP_REMOTE, new String[]{"111.xls", "222.xls"}, SFTP_LOCAL));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            sftp.disconnect();
        }
    }

    public SFTPUtils() {
    }

    public SFTPUtils(String host, int port, String username, String password) {
        this.host = host;
        this.username = username;
        this.password = password;
        this.port = port;
    }

    public SFTPUtils(String host, String username, String password) {
        this(host, 22, username, password);
    }

    /**
     * 通过SFTP连接服务器
     */
    public void connect() {
        try {
            JSch jsch = new JSch();
            jsch.getSession(username, host, port);
            sshSession = jsch.getSession(username, host, port);
            if (log.isInfoEnabled()) {
                log.info("Session created.");
            }
            sshSession.setPassword(password);
            Properties sshConfig = new Properties();
            sshConfig.put("StrictHostKeyChecking", "no");
            sshSession.setConfig(sshConfig);
            sshSession.connect();
            if (log.isInfoEnabled()) {
                log.info("Session connected.");
            }
            Channel channel = sshSession.openChannel("sftp");
            channel.connect();
            if (log.isInfoEnabled()) {
                log.info("Opening Channel.");
            }
            sftp = (ChannelSftp) channel;
            if (log.isInfoEnabled()) {
                log.info("Connected to " + host + ".");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭连接
     */
    public void disconnect() {
        if (this.sftp != null) {
            if (this.sftp.isConnected()) {
                this.sftp.disconnect();
                if (log.isInfoEnabled()) {
                    log.info("sftp is closed already");
                }
            }
        }
        if (this.sshSession != null) {
            if (this.sshSession.isConnected()) {
                this.sshSession.disconnect();
                if (log.isInfoEnabled()) {
                    log.info("sshSession is closed already");
                }
            }
        }
    }

    /**
     * 批量下载文件
     *
     * @param remoteFilePaths：远程下载文件目录
     * @param localPath：本地保存目录
     * @return 本地文件目录
     */
    public List<String> batchDownLoadFile(String[] remoteFilePaths,
                                          String localPath) {

        List<String> list = new ArrayList<>();
        for (String remoteFilePath : remoteFilePaths) {
            String localFilePath = downloadFile(remoteFilePath, localPath);
            list.add(localFilePath);
        }
        return list;
    }

    /**
     * 批量下载文件
     *
     * @param remotePath：远程下载文件目录
     * @param remoteFileNames：远程下载文件列表
     * @param localPath：本地保存目录
     * @return 本地文件目录
     */
    public List<String> batchDownLoadFile(String remotePath,
                                          String[] remoteFileNames,
                                          String localPath) {

        List<String> list = new ArrayList<>();
        for (String fileName : remoteFileNames) {
            String localFilePath = downloadFile(remotePath, fileName, localPath, fileName);
            list.add(localFilePath);
        }
        return list;
    }


    /**
     * 下载文件
     *
     * @param remoteFilePath：远程下载目录(以路径符号结束)
     * @param localPath：本地保存目录(以路径符号结束)
     * @return
     */
    public String downloadFile(String remoteFilePath,
                               String localPath) {
        String remotePath = FilenameUtils.getFullPath(remoteFilePath);
        String fileName = FilenameUtils.getName(remoteFilePath);
        return downloadFile(remotePath, fileName, localPath, fileName);

    }

    /**
     * 下载文件
     *
     * @param remotePath：远程下载目录(以路径符号结束)
     * @param remoteFileName：下载文件名
     * @param localPath：本地保存目录(以路径符号结束)
     * @param localFileName：保存文件名
     * @return
     */
    public String downloadFile(String remotePath,
                               String remoteFileName,
                               String localPath,
                               String localFileName) {

        Path path = Paths.get(localPath, localFileName);
        remotePath =
                remotePath.lastIndexOf("/") == (remotePath.length() - 1)
                        ? remotePath : remotePath + "/";
        try (OutputStream os = Files.newOutputStream(path)) {
            sftp.get(remotePath + remoteFileName, os);
            log.info("===DownloadFile:" + remoteFileName + " success from sftp.");
            return path.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 上传文件
     *
     * @param localFilePath 本地文件路径
     * @param remotePath    远程保存目录
     * @return
     */
    public boolean uploadFile(String localFilePath,
                              String remotePath) {

        String localPath = FilenameUtils.getFullPath(localFilePath);
        String fileName = FilenameUtils.getName(localFilePath);
        return uploadFile(localPath, fileName, remotePath, fileName);
    }

    /**
     * 上传文件
     *
     * @param localPath：本地上传目录(以路径符号结束)
     * @param localFileName：上传的文件名
     * @param remotePath：远程保存目录
     * @param remoteFileName：保存文件名
     * @return
     */
    public boolean uploadFile(String localPath,
                              String localFileName,
                              String remotePath,
                              String remoteFileName) {
        Path path = Paths.get(localPath, localFileName);
        try (InputStream in = Files.newInputStream(path)) {
            createDir(remotePath);
            sftp.put(in, remoteFileName);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 批量上传文件
     *
     * @param localPath：本地上传目录(以路径符号结束)
     * @param remotePath：远程保存目录
     * @return
     */
    public boolean bacthUploadFile(String localPath,
                                   String[] fileNames,
                                   String remotePath) {
        return bacthUploadFile(localPath, fileNames, remotePath, false);
    }

    /**
     * 批量上传文件
     *
     * @param localFilePaths：本地上传文件列表
     * @param remotePath：远程保存目录
     * @return
     */
    public boolean bacthUploadFile(String[] localFilePaths,
                                   String remotePath) {
        try {
            for (int i = 0; i < localFilePaths.length; i++) {
                String localPath = FilenameUtils.getFullPath(localFilePaths[i]);
                String filename = FilenameUtils.getName(localFilePaths[i]);
                uploadFile(localPath, filename, remotePath, filename);
            }

            log.info("upload file is success:remotePath={} ,file size is {}"
                    , remotePath, localFilePaths.length);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 批量上传文件
     *
     * @param localPath：本地上传目录(以路径符号结束)
     * @param remotePath：远程保存目录
     * @param del：上传后是否删除本地文件
     * @return
     */
    public boolean bacthUploadFile(String localPath,
                                   String[] fileNames,
                                   String remotePath,
                                   boolean del) {
        try {
            for (int i = 0; i < fileNames.length; i++) {
                boolean flag = uploadFile(localPath, fileNames[i], remotePath, fileNames[i]);
                if (flag && del) {
                    deleteFile(Paths.get(localPath, fileNames[i]));
                }
            }

            log.info("upload file is success:remotePath={} and localPath={},file size is {}"
                    , remotePath, localPath, fileNames.length);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;

    }

    /**
     * 删除本地文件
     *
     * @param path
     * @return
     */
    public boolean deleteFile(Path path) {
        if (Files.exists(path)) {
            try {
                Files.delete(path);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        log.info("delete file success from local[{}].", path);
        return true;
    }

    /**
     * 创建目录
     *
     * @param createpath
     * @return
     */
    public boolean createDir(String createpath) {
        try {
            if (isDirExist(createpath)) {
                this.sftp.cd(createpath);
                return true;
            }
            String pathArry[] = createpath.split("/");
            StringBuffer filePath = new StringBuffer("/");
            for (String path : pathArry) {
                if (path.equals("")) {
                    continue;
                }
                filePath.append(path + "/");
                if (isDirExist(filePath.toString())) {
                    sftp.cd(filePath.toString());
                } else {
                    // 建立目录
                    sftp.mkdir(filePath.toString());
                    // 进入并设置为当前目录
                    sftp.cd(filePath.toString());
                }

            }
            this.sftp.cd(createpath);
            return true;
        } catch (SftpException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 判断目录是否存在
     *
     * @param directory
     * @return
     */
    public boolean isDirExist(String directory) {
        boolean isDirExistFlag = false;
        try {
            SftpATTRS sftpATTRS = sftp.lstat(directory);
            isDirExistFlag = true;
            return sftpATTRS.isDir();
        } catch (Exception e) {
            if (e.getMessage().toLowerCase().equals("no such file")) {
                return false;
            }
            e.printStackTrace();
        }
        return isDirExistFlag;
    }

    /**
     * 删除stfp文件
     *
     * @param directory：要删除文件所在目录
     * @param deleteFile：要删除的文件
     */
    public void deleteSFTP(String directory, String deleteFile) {
        try {
            // sftp.cd(directory);
            sftp.rm(directory + deleteFile);
            if (log.isInfoEnabled()) {
                log.info("delete file success from sftp.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 如果目录不存在就创建目录
     *
     * @param path
     */
    public void mkdirs(String path) {
        File f = new File(path);

        String fs = f.getParent();

        f = new File(fs);

        if (!f.exists()) {
            f.mkdirs();
        }
    }

    /**
     * 列出目录下的文件
     *
     * @param directory：要列出的目录
     * @return
     * @throws SftpException
     */
    public Vector listFiles(String directory) throws SftpException {
        return sftp.ls(directory);
    }


}
