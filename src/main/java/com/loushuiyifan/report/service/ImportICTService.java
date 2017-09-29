package com.loushuiyifan.report.service;

import com.loushuiyifan.config.poi.PoiRead;
import com.loushuiyifan.report.ReportConfig;
import com.loushuiyifan.report.bean.ExtImportLog;
import com.loushuiyifan.report.bean.RptImportDataICT;
import com.loushuiyifan.report.dao.ExtImportLogDAO;
import com.loushuiyifan.report.dao.RptImportDataICTDAO;
import com.loushuiyifan.report.dto.CheckDataDTO;
import com.loushuiyifan.report.dto.DeleteImportDataDTO;
import com.loushuiyifan.report.exception.ReportException;
import com.loushuiyifan.report.serv.DateService;
import com.loushuiyifan.report.serv.ReportReadServ;
import com.loushuiyifan.report.vo.ImportDataLogVO;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Path;
import java.sql.Date;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 漏水亦凡
 * @date 2017/9/22
 */
@Service
public class ImportICTService {

    private static final Logger logger = LoggerFactory.getLogger(ImportICTService.class);


    @Autowired
    RptImportDataICTDAO rptImportDataICTDAO;

    @Autowired
    ExtImportLogDAO extImportLogDAO;

    @Autowired
    SqlSessionFactory sqlSessionFactory;

    @Autowired
    DateService dateService;

    /**
     * 解析入库
     */
    @Transactional
    public void save(Path path,
                     Long userId,
                     String month,
                     Integer latnId,
                     String remark) throws Exception {

        String filename = path.getFileName().toString();

        //首先将文件解析成bean
        List<RptImportDataICT> list = getRptImportDataICTs(path);

        //校验数据是否为空
        int size = list.size();
        if (size == 0) {
            String error = "文件数据为空: " + filename;
            logger.error(error);
            throw new ReportException(error);
        }


        //然后保存解析的数据
        Long logId = extImportLogDAO.nextvalKey();
        importDataByGroup(list, logId, month);

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
        log.setType(ReportConfig.RptImportType.ICT.toString());
        extImportLogDAO.insert(log);

        //校验导入数据指标
        CheckDataDTO dto = new CheckDataDTO();
        dto.setLogId(logId);
        rptImportDataICTDAO.checkImportData(dto);
        Integer code = dto.getRtnCode();
        //TODO 统一更改存过返回值
        if (code != 0) {//非0为失败
            String error = "";
            try {
                delete(userId, logId);
            } catch (Exception e) {
                error = "校验失败后删除数据异常: " + e.getMessage();
            } finally {
                error = String.format("导入数据校验失败: {}; {}", dto.getRtnMeg(), error);
                logger.error(error);
                throw new ReportException(error);
            }
        }

    }


    /**
     * 解析数据
     *
     * @param path
     * @return
     */
    public List<RptImportDataICT> getRptImportDataICTs(Path path) throws Exception {

        PoiRead read = new RptImportDataICTRead()
                .load(path.toFile())
                .multi(true)//excel数据可以解析多sheet
                .startWith(0, 1);

        List<RptImportDataICT> list = read.read();
        return list;
    }

    /**
     * 在一个session中批量 插入
     *
     * @param list
     * @param logId
     * @param month
     */
    public void importDataByGroup(List<RptImportDataICT> list, Long logId, String month) {

        final SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH);
        try {
            logger.debug("批量插入数量: {}", list.size());

            for (final RptImportDataICT data : list) {
                data.setLogId(logId);
                data.setAcctMonth(month);
                rptImportDataICTDAO.insertSelective(data);
            }
            sqlSession.commit();
        } finally {
            sqlSession.close();
            logger.debug("批量插入结束");
        }
    }

    /**
     * 查询该用户某账期所有导入记录
     *
     * @param userId
     * @param month
     * @return
     */
    public List<ImportDataLogVO> list(Long userId, String month) {
        String type = ReportConfig.RptImportType.ICT.toString();
        return rptImportDataICTDAO.listICTLog(userId, month, type);
    }


    /**
     * 删除数据
     *
     * @param userId
     * @param logId
     */
    public void delete(Long userId, Long logId) {
        DeleteImportDataDTO dto = new DeleteImportDataDTO();
        dto.setUserId(userId);
        dto.setLogId(logId);
        rptImportDataICTDAO.deleteImportData(dto);
        int code = dto.getRtnCode();
        //TODO 统一更改存过返回值
        if (code != 0) {//非0为失败
            throw new ReportException("数据删除失败:  " + dto.getRtnMeg());
        }
    }


    /**
     * ICT数据解析类
     */
    static class RptImportDataICTRead extends ReportReadServ<RptImportDataICT> {


        @Override
        protected List<RptImportDataICT> processSheet(Sheet sheet) {
            FormulaEvaluator evaluator = sheet
                    .getWorkbook()
                    .getCreationHelper()
                    .createFormulaEvaluator();

            List<RptImportDataICT> list = new ArrayList<>();
            for (int y = startY; y <= sheet.getLastRowNum(); y++) {
                Row row = sheet.getRow(y);
                RptImportDataICT bean = new RptImportDataICT();
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
                            bean.setIncomeSource(data);
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
                            bean.setZzxiulf(data);
                            break;
                        case 15:
                            bean.setZglks(data);
                            break;
                        case 16:
                            bean.setIndexData(Double.parseDouble(data));
                            break;
                        case 17:
                            bean.setTaxValue(Double.parseDouble(data));
                            break;
                        case 18://ICT合同号
                            bean.setIctCode(data);
                            break;
                        case 19://hkont供应商
                            bean.setHkont(data);
                            break;
                        case 20://ict名称
                            bean.setItemCode(data);
                            break;
                        case 21://原始冲销数据标记
                            bean.setRemark(data);
                            break;
                    }
                }
                list.add(bean);
            }
            return list;
        }

        @Override
        protected boolean checkSheet(Sheet sheet) {
            String name = sheet.getSheetName();
            if (StringUtils.isEmpty(name) || name.startsWith("$")
                    || name.indexOf("-") == -1) {
                return false;
            }
            return true;
        }


    }


}
