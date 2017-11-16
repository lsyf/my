package com.loushuiyifan.report.serv;

import com.loushuiyifan.report.exception.DownloadException;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author 漏水亦凡
 * @date 2017/9/21
 */
@Service
public class DownloadService {

    private static final Logger logger = LoggerFactory.getLogger(DownloadService.class);

    public void download(HttpServletRequest req,
                         HttpServletResponse resp,
                         String path) throws Exception {
        Path file = Paths.get(path);
        String name = FilenameUtils.getName(path);
        download(req, resp, file, name);
    }

    public void download(HttpServletRequest req,
                         HttpServletResponse resp,
                         Path path) throws Exception {
        String name = path.getFileName().toString();
        download(req, resp, path, name);
    }

    /**
     * 将内存中字节数组 写出流
     *
     * @param req
     * @param resp
     * @param data
     * @param name
     * @throws Exception
     */
    public void download(HttpServletRequest req,
                         HttpServletResponse resp,
                         byte[] data,
                         String name) throws Exception {


        name = configFileName(req, name);


        long length = data.length;
        resp.setContentType("multipart/form-data");
        resp.setContentLengthLong(length);
        resp.addHeader("Content-Disposition",
                "attachment; filename=" + name);
        try (OutputStream os = resp.getOutputStream()) {
            os.write(data);
            os.flush();
            os.close();
            resp.flushBuffer();
        } catch (IOException e) {
            e.printStackTrace();
            throw new DownloadException("下载文件失败: " + name, e);
        }
    }

    /**
     * 根据Path写出流
     *
     * @param req
     * @param resp
     * @param path
     * @param name
     * @throws Exception
     */
    public void download(HttpServletRequest req,
                         HttpServletResponse resp,
                         Path path,
                         String name) throws Exception {

        if (!Files.exists(path)) {
            throw new DownloadException("找不到文件路径: " + path.toString());
        }
        name = configFileName(req, name);


        String contentType = Files.probeContentType(path);
        long length = Files.size(path);
        resp.setContentType(contentType);
        resp.setContentLengthLong(length);
        resp.addHeader("Content-Disposition",
                "attachment; filename=" + name);
        try {
            Files.copy(path, resp.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
            throw new DownloadException("下载文件失败: " + name, e);
        }
    }

    /**
     * 根据path写出流(断点续传)
     * 待优化(使用RandomAccessFile)
     *
     * @param req
     * @param resp
     * @param path
     * @param name
     * @throws Exception
     */
    public void downloadPlus(HttpServletRequest req,
                             HttpServletResponse resp,
                             Path path,
                             String name) throws Exception {

        if (!Files.exists(path)) {
            throw new DownloadException("找不到文件路径: " + path.toString());
        }

        name = configFileName(req, name);


        long fileSize = Files.size(path);
        long length = fileSize;
        long start = 0;


        resp.setHeader("Accept-Ranges", "byte");
        //断点续传的信息就存储在这个Header属性里面
        // range:bytes=3-100;200 （从3开始，读取长度为100，总长度为200）
        String range = req.getHeader("Range");
        if (range != null) {
            //SC_PARTIAL_CONTENT 206 表示服务器已经成功处理了部分 GET 请求。
            // 类似于 FlashGet 或者迅雷这类的 HTTP下载工具都是使用此类
            // 响应实现断点续传或者将一个大文档分解为多个下载段同时下载。
            resp.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
            range = range.substring("bytes=".length());
            String[] rangeInfo = range.split("-");
            start = new Long(rangeInfo[0]);
            if (start > fileSize) {
                resp.sendError(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
                return;
            }
            if (rangeInfo.length > 1) {
                length = Long.parseLong(rangeInfo[1]) - start + 1;
            } else {
                length = length - start;
            }
            if (length + start > fileSize) {
                length = fileSize - start;
            }
        }

        if (range != null) {
            resp.setHeader("Content-Range", "bytes "
                    + new Long(start).toString() + "-"
                    + new Long(start + length - 1).toString()
                    + "/"
                    + new Long(fileSize).toString());
        }
        String contentType = Files.probeContentType(path);
        resp.setContentType(contentType);
        resp.setContentLengthLong(length);
        resp.setHeader("Content-Disposition", "attachment;filename=" + name);

        InputStream is = Files.newInputStream(path);
        try {
            if (start != 0) {
                is.skip(start);
            }
            OutputStream os = resp.getOutputStream();

            long nread = 0L;
            byte[] buf = new byte[65536];
            int n;
            while ((n = is.read(buf)) > 0) {
                os.write(buf, 0, n);
                nread += n;
            }

            os.flush();

        } catch (Exception e) {
            e.printStackTrace();
            throw new DownloadException("下载文件失败: " + name, e);
        } finally {
            is.close();
        }


    }

    /**
     * 根据浏览器配置 文件名(以防乱码)
     *
     * @return
     */
    public String configFileName(HttpServletRequest req, String name) throws UnsupportedEncodingException {
        String header = req.getHeader("User-Agent").toUpperCase();
        if (header.contains("MSIE") || header.contains("TRIDENT") || header.contains("EDGE")) {
            name = URLEncoder.encode(name, "utf-8");
            name = name.replace("+", "%20");    //IE下载文件名空格变+号问题
        } else {
            name = new String(name.getBytes(), "ISO8859-1");
        }
        return name;
    }

}
