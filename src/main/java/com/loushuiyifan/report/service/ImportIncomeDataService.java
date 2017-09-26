package com.loushuiyifan.report.service;

import com.loushuiyifan.poi.PoiRead;
import com.loushuiyifan.report.bean.ExtImportLog;
import com.loushuiyifan.report.bean.RptImportDataChennel;
import com.loushuiyifan.report.dao.ExtImportLogDAO;
import com.loushuiyifan.report.dao.RptImportDataChennelDAO;
import com.loushuiyifan.report.dto.CheckDataDTO;
import com.loushuiyifan.report.exception.ReportException;
import com.loushuiyifan.report.serv.ReportReadServ;
import org.apache.commons.lang3.StringUtils;
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
public class ImportIncomeDataService {

    private static final Logger logger = LoggerFactory.getLogger(ImportIncomeDataService.class);


    @Autowired
    RptImportDataChennelDAO rptImportDataChennelDAO;

    @Autowired
    ExtImportLogDAO extImportLogDAO;

    /**
     * 解析入库
     */
    @Transactional
    public void save(Path path,
                     Integer userId,
                     String month,
                     Integer latnId,
                     String remark) throws Exception {

        String filename = path.getFileName().toString();

        //首先将文件解析成bean
        List<RptImportDataChennel> list = getRptImportDataChennels(path);

        //校验数据是否为空
        int size = list.size();
        if (size == 0) {
            String error = "文件数据为空: " + filename;
            logger.error(error);
            throw new ReportException(error);
        }


        //然后保存解析的数据
        Long logId = extImportLogDAO.nextvalKey();
        importDataByGroup(list, logId);

        //最后保存日志数据
        ExtImportLog log = new ExtImportLog();
        log.setLogId(logId);
        log.setUserId(userId);
        log.setAcctMonth(month);
        log.setLatnId(latnId);
        log.setExportDesc(remark);
        log.setStatus("Y");
        log.setImportDate(Date.from(Instant.now()));
        String incomeSource = list.get(0).getIncomeSource();
        log.setIncomeSource(incomeSource);
        log.setFileName(filename);
        extImportLogDAO.insert(log);

        //校验导入数据指标
        CheckDataDTO dto = new CheckDataDTO();
        dto.setLogId(logId);
        rptImportDataChennelDAO.checkRptImportData(dto);

        Integer code = dto.getRtnCode();
        if (code != 0) {//非0为失败
            //先删除所有数据
            rptImportDataChennelDAO.deleteByLogId(logId);
            //删除日志
            extImportLogDAO.deleteByPrimaryKey(logId);

            throw new ReportException("数据校验失败: " + dto.getRtnMeg());
        }

    }


    /**
     * 解析数据
     *
     * @param path
     * @return
     */
    public List<RptImportDataChennel> getRptImportDataChennels(Path path) throws Exception {

        PoiRead read = new RptImportDataChennelRead()
                .load(path.toFile())
                .multi(true)//excel数据可以解析多sheet
                .startWith(0, 1);

        List<RptImportDataChennel> list = read.read();
        return list;
    }

    /**
     * 以groupSize为单位分组 插入
     *
     * @param list
     * @param logId
     */
    public void importDataByGroup(List<RptImportDataChennel> list, Long logId) {
        int size = list.size();
        int groupSize = 1000;
        int group = size / groupSize + 1;

        for (int i = 0; i < group; i++) {
            int from = i * groupSize;
            int to = (i + 1) * groupSize;
            to = to > size ? size : to;
            if (from == to) {//如果首尾相同则跳出
                break;
            }

            logger.debug("第{}组  {}-{}", i + 1, from, to);
            List<RptImportDataChennel> datas = list.subList(from, to);
            rptImportDataChennelDAO.addRptImportDatas(logId, datas);
        }
    }

    /**
     * 收入数据解析类
     */
    static class RptImportDataChennelRead extends ReportReadServ<RptImportDataChennel> {


        @Override
        protected List<RptImportDataChennel> processSheet(Sheet sheet) {
            List<RptImportDataChennel> list = new ArrayList<>();
            for (int y = startY; y <= sheet.getLastRowNum(); y++) {
                Row row = sheet.getRow(y);
                RptImportDataChennel bean = new RptImportDataChennel();
                for (int x = startX; x <= row.getLastCellNum(); x++) {
                    String data = getCellData(row.getCell(x));
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
                            data.replace(",", "");
                            bean.setIndexData(Double.parseDouble(data));
                            break;
                        case 17:
                            data.replace(",", "");
                            bean.setTaxValue(Double.parseDouble(data));
                            break;
                        case 18://ICT合同号
                            bean.setIctCode(data);
                            break;
                        case 19://hkont供应商
                            bean.setHkont(data);
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
