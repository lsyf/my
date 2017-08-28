package com.loushuiyifan.common.util;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author 漏水亦凡
 * @date 2017/7/19
 */
public class ImageUtils {

    private static Logger logger = LoggerFactory.getLogger(ImageUtils.class);

    public static final String FONT_NAME = "微软雅黑";
    public static final int FONT_STYPE = Font.PLAIN;
    public static final float FONT_ALPHA = 1;
    public static final Color FONT_COLOR = Color.RED;


    public static void main(String[] args) throws Exception {
        waterMark("d:/1.jpg", "d:/2.jpg", "by abc", null, 0, null, 0, 0.5f, 10, 10);
    }

    /**
     * 打印文字水印图片
     */
    public static void waterMark(InputStream is, OutputStream os,
                                 String text, String fontName, int fontStyle,
                                 Color fontColor, int fontSize, float fontAlpha,
                                 int offsetX, int offsetY) {
        try {

            fontName = fontName == null ? FONT_NAME : fontName;
            fontStyle = fontStyle == 0 ? FONT_STYPE : fontStyle;
            fontColor = fontColor == null ? FONT_COLOR : fontColor;
            fontAlpha = fontAlpha == 0 ? FONT_ALPHA : fontAlpha;

            Image src = ImageIO.read(is);

            int width = src.getWidth(null);
            int height = src.getHeight(null);
            logger.debug("width:{}, height:{}", width, height);
            if (width < 100 || height < 100) {
                throw new Exception("图片尺寸过小");
            }

            //判断字体大小
            if (fontSize == 0) {
                int less = width > height ? height : width;
                fontSize = less / 40;
            }
            fontSize = fontSize > 10 ? fontSize : 10;
            logger.debug("fontName:{}, fontSize:{}, fontAlpha", fontName, fontSize, fontAlpha);

            BufferedImage image = new BufferedImage(width, height,
                    BufferedImage.TYPE_INT_RGB);
            Graphics2D g = image.createGraphics();
            g.drawImage(src, 0, 0, width, height, null);
            g.setColor(fontColor);
            g.setFont(new Font(fontName, fontStyle, fontSize));

            //判断位置
            int textSize = getTextLength(text);
            logger.debug("text:{}, size:{}", text, textSize);
            int x = width - fontSize * textSize - offsetX;
            int y = height - fontSize / 2 - offsetY;
            logger.debug("x:{}, y:{}", x, y);
            x = x < 0 ? 0 : x;
            y = y < 0 ? 0 : y;

            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, fontAlpha));
            g.drawString(text, x, y);
            g.dispose();

            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(os);
            encoder.encode(image);

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("添加水印失败", e);
        }
    }

    public static void waterMark(String in, String out,
                                 String text, String fontName, int fontStyle,
                                 Color fontColor, int fontSize, float fontAlpha,
                                 int offsetX, int offsetY) throws Exception {
        InputStream is = new FileInputStream(in);
        OutputStream os = new FileOutputStream(out);
        waterMark(is, os, text, fontName, fontStyle, fontColor, fontSize, fontAlpha, offsetX, offsetY);
        is.close();
        os.close();

    }

    public static int getTextLength(String text) {
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
