package com.loushuiyifan.common.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author 漏水亦凡
 * @date 2017/6/14
 */
public class DecimalUtils {


    /**
     * 按指定舍入模式保留指定小数位数
     *
     * @param d            格式化前的小数
     * @param newScale     保留小数位数
     * @param roundingMode 舍入模式
     *                     (RoundingMode.UP始终进一/DOWN直接舍弃/
     *                     CEILING正进负舍/FLOOR正舍负进/
     *                     HALF_UP四舍五入/HALF_DOWN五舍六进/
     *                     HALF_EVEN银行家舍入法/UNNECESSARY抛出异常)
     * @return 格式化后的小数
     */
    public static double formatDecimal(double d, int newScale, RoundingMode roundingMode) {
        BigDecimal bd = new BigDecimal(d).setScale(newScale, roundingMode);
        return bd.doubleValue();
    }

    public static double formatDecimal(double d, int newScale) {
        //四舍五入
        return formatDecimal(d, newScale, RoundingMode.HALF_UP);
    }


    /**
     * 按指定舍入模式保留指定小数位数
     *
     * @param d            格式化前的小数
     * @param newScale     保留小数位数
     * @param roundingMode 舍入模式
     *                     (RoundingMode.UP始终进一/DOWN直接舍弃/
     *                     CEILING正进负舍/FLOOR正舍负进/
     *                     HALF_UP四舍五入/HALF_DOWN五舍六进/
     *                     HALF_EVEN银行家舍入法/UNNECESSARY抛出异常)
     * @return 格式化后的小数
     */
    public static double formatDecimal(String d, int newScale, RoundingMode roundingMode) {
        if (d == null) {
            return 0.00;
        }
        BigDecimal bd = new BigDecimal(Double.valueOf(d)).setScale(newScale, roundingMode);
        return bd.doubleValue();
    }

    public static double formatDecimal(String d, int newScale) {
        //四舍五入
        return formatDecimal(d, newScale, RoundingMode.HALF_UP);
    }


    public static double formatDecimal(BigDecimal d, int newScale) {
        if (d == null) {
            return 0.00;
        }
        //四舍五入
        return d.setScale(newScale, RoundingMode.HALF_UP).doubleValue();
    }

}
