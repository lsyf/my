package com.loushuiyifan.report.serv;

import com.loushuiyifan.config.poi.AbstractPoiRead;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.ArrayList;
import java.util.List;

/**
 * 报表解析
 *
 * @author 漏水亦凡
 * @date 2017/9/25
 */
public abstract class ReportReadServ<E> extends AbstractPoiRead<E> {


    @Override
    protected List<E> process(Workbook wb) {

        List<E> list = new ArrayList<>();
        int index = 0;
        for (Sheet sheet : wb) {
            //非多sheet解析，仅读取第一个sheet
            if (!isMulti && index == 1) {
                break;
            }
            //多sheet解析，添加sheet校验
            if (isMulti && !checkSheet(sheet)) {
                continue;
            }
            //解析sheet
            list.addAll(processSheet(sheet));
            index++;
        }
        return list;
    }

    /**
     * 解析数据
     *
     * @param sheet
     * @return
     */
    abstract protected List<E> processSheet(Sheet sheet);

    /**
     * 校验是否允许解析sheet
     *
     * @param sheet
     * @return
     */
    abstract protected boolean checkSheet(Sheet sheet);


}
