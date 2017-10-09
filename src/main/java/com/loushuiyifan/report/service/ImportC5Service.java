package com.loushuiyifan.report.service;

import com.loushuiyifan.report.dao.ExtImportLogDAO;
import com.loushuiyifan.report.dao.RptImportDataC5DAO;
import com.loushuiyifan.report.serv.DateService;
import com.loushuiyifan.report.vo.ImportDataLogVO;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.List;

/**
 * @author 漏水亦凡
 * @date 2017/9/22
 */
@Service
public class ImportC5Service {

    private static final Logger logger = LoggerFactory.getLogger(ImportC5Service.class);


    @Autowired
    RptImportDataC5DAO rptImportDataC5DAO;

    @Autowired
    ExtImportLogDAO extImportLogDAO;

    @Autowired
    SqlSessionFactory sqlSessionFactory;

    @Autowired
    DateService dateService;


    public void save(Path path, Long userId, String month, int i, String remark) {
    }

    public List<ImportDataLogVO> list(Long userId, String month) {
        return null;
    }

    public void delete(Long userId, Long logId) {
    }
}
