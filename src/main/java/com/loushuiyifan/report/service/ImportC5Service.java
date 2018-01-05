package com.loushuiyifan.report.service;

import java.nio.file.Path;
import java.sql.Date;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.loushuiyifan.config.poi.PoiRead;
import com.loushuiyifan.report.ReportConfig;
import com.loushuiyifan.report.bean.ExtImportLog;
import com.loushuiyifan.report.bean.RptImportDataC5;
import com.loushuiyifan.report.dao.ExtImportLogDAO;
import com.loushuiyifan.report.dao.RptImportDataC5DAO;
import com.loushuiyifan.report.dto.SPDataDTO;
import com.loushuiyifan.report.exception.ReportException;
import com.loushuiyifan.report.serv.DateService;
import com.loushuiyifan.report.serv.ReportReadServ;
import com.loushuiyifan.report.vo.ImportC5DataVO;
import com.loushuiyifan.report.vo.ImportDataLogVO;

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


    @Transactional
    public void save(Path path,
                     Long userId,
                     String month,
                     Integer latnId,
                     String remark) throws Exception {

        String filename = path.getFileName().toString();

        //首先将文件解析成bean
        List<RptImportDataC5> list = getRptImportDataC5(path);
        //校验数据是否为空
        int size = list.size();
        if (size == 0) {
            String error = "文件数据为空: " + filename;
            logger.error(error);
            throw new ReportException(error);
        }

        //然后保存解析的数据
        Long logId = extImportLogDAO.nextvalKey();
        saveC5DataByGroup(list, logId, month, latnId);

        //最后保存日志数据
        ExtImportLog log = new ExtImportLog();
        log.setLogId(logId);
        log.setUserId(Math.toIntExact(userId));
        log.setAcctMonth(month);
        log.setLatnId(latnId);
        log.setExportDesc(remark);
        log.setStatus("Y");
        log.setImportDate(Date.from(Instant.now()));
        String incomeSource = list.get(0).getIncomeSource();
        log.setIncomeSoure(incomeSource);
        log.setFileName(filename);
        log.setType(ReportConfig.RptImportType.C5.toString());
        extImportLogDAO.insert(log);


        //校验导入数据指标
        SPDataDTO dto = new SPDataDTO();
        dto.setLogId(logId);
        dto.setMonth(month);
        rptImportDataC5DAO.checkC5Data(dto);

        //存过返回值(非0为失败)
        Integer code = dto.getRtnCode();
        if (code != 0) {
            String error = "";
            try {
                delete(userId, logId);
            } catch (Exception e) {
                error = "3校验失败后删除数据异常: " + e.getMessage();
            } finally {
                error = String.format("3导入数据校验失败: %s; %s", dto.getRtnMsg(), error);
                logger.error(error);
                throw new ReportException(error);
            }

        }


    }

    /**
     * @param list
     * @param logId
     * @param month
     */
    public void saveC5DataByGroup(List<RptImportDataC5> list, Long logId, String month, Integer latnId) {
            for (final RptImportDataC5 data : list) {
                //解析不到收入来源，默认为-1
                data.setIncomeSource("-1");
                data.setLatnid(latnId);
                data.setLogId(logId);
                data.setAcctMonth(month);
                rptImportDataC5DAO.insertSelective(data);
            }
    }

    /**
     * 稽核数据
     */
    public Map<String, Object> list(String month, Integer latnId) {

        String type = ReportConfig.RptImportType.C5.toString();
        Map<String, Object> map = new HashMap<>();
        List<ImportDataLogVO> list = rptImportDataC5DAO.jiheSum(month, latnId, type);
        List<ImportC5DataVO> l_area = rptImportDataC5DAO.areaCount(month, latnId);
        if (list == null ||list.size()==0) {
            throw new ReportException("查询数据为空！");
        }
        map.put("list", list);
        map.put("c5", l_area);
        return map;
    }

    /**
     * 删除数据
     *
     * @param userId
     * @param logId
     */
    public void delete(Long userId, Long logId) {
        SPDataDTO dto = new SPDataDTO();
        dto.setUserId(userId);
        dto.setLogId(logId);
        rptImportDataC5DAO.deleteImportData(dto);
        int code = dto.getRtnCode();

        if (code != 0) {//非0为失败
            throw new ReportException("3数据删除失败: " + dto.getRtnMsg());
        }
    }

    /**
     * 解析数据
     *
     * @param path
     * @return
     */
    public List<RptImportDataC5> getRptImportDataC5(Path path) throws Exception {

        PoiRead read = new RptImportDataC5Read()
                .load(path.toFile())
                .multi(true)//excel数据可以解析多sheet
                .startWith(0, 1);

        List<RptImportDataC5> list = read.read();

        return list;
    }

    static class RptImportDataC5Read extends ReportReadServ<RptImportDataC5> {
        @Override
        protected List<RptImportDataC5> processSheet(Sheet sheet) {

            FormulaEvaluator evaluator = sheet.getWorkbook().getCreationHelper().createFormulaEvaluator();
            List<RptImportDataC5> list = new ArrayList<RptImportDataC5>();
            for (int y = startY; y <= sheet.getLastRowNum(); y++) {
                Row row = sheet.getRow(y);
                RptImportDataC5 bean = new RptImportDataC5();
                for (int x = startX; x <= row.getLastCellNum(); x++) {
                    String data = getCellData(row.getCell(x), evaluator);
                    if (StringUtils.isEmpty(data)) {
                        continue;
                    }
                    switch (x) {
                        case 0:
                            bean.setAreaId(Integer.parseInt(data));
                            break;
                        case 2:
                            bean.setC5Id(Long.parseLong(data));
                            break;
                        case 4:
                            bean.setIncomeCode(data);
                            break;
                        case 6:
                            bean.setHorCode(data);
                            break;
                        case 8:
                            bean.setVerCode(data);
                            break;
                        case 10:
                            bean.setSelfCode(Integer.parseInt(data));
                            break;
                        case 12:
                            bean.setContractId(data);
                            break;
                        case 14:
                            bean.setIndexData(Double.parseDouble(data));
                            break;

                    }
                }
                list.add(bean);

            }
            return list;
        }

    }


}
