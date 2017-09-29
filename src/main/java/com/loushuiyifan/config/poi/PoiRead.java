package com.loushuiyifan.config.poi;

import java.util.List;

/**
 * @author 漏水亦凡
 * @create 2016-12-08 17:37.
 */
public interface PoiRead<E> {
    List<E> read() throws Exception;
}
