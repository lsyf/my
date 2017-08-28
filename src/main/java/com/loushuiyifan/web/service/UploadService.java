package com.loushuiyifan.web.service;

import com.loushuiyifan.common.util.ImageUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author 漏水亦凡
 * @date 2017/7/19
 */
@Service
public class UploadService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UploadService.class);

    public static final String MARK_TEXT = "水印";
    public static final String FONT_NAME = "微软雅黑";

    public static final int FONT_SIZE = 120;
    public static final int FONT_STYPE = Font.BOLD;
    public static final Color FONT_COLOR = Color.RED;

    public static final int X = 10;
    public static final int Y = 10;

    public static float ALPHA = 0.3F;


    public String uploadImage(MultipartFile file, String uploadPath, String realUploadPath) {
        InputStream is = null;
        OutputStream os = null;

        try {

            is = file.getInputStream();
            os = new FileOutputStream(realUploadPath + "/" + file.getOriginalFilename());

            byte[] buffer = new byte[1024];
            int len = 0;

            while ((len = is.read(buffer)) > 0) {
                os.write(buffer);
            }

        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("图片处理失败", e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return uploadPath + "/" + file.getOriginalFilename();
    }


    public String watermark(MultipartFile file, String uploadPath, String realUploadPath) {

        String logoFileName = "logo" + file.getOriginalFilename();
        try (
                InputStream is = file.getInputStream();
                OutputStream os = new FileOutputStream(realUploadPath + "/" + logoFileName);
        ) {

            ImageUtils.waterMark(is, os, "哈哈", null, 0, null, 0, 0, 10, 10);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return uploadPath + "/" + logoFileName;
    }

    public int getTextLength(String text) {
        int length = text.length();

        for (int i = 0; i < text.length(); i++) {
            String s = String.valueOf(text.charAt(i));
            if (s.getBytes().length > 1) {
                length++;
            }
        }

        length = length % 2 == 0 ? length / 2 : length / 2 + 1;
        return length;
    }


}