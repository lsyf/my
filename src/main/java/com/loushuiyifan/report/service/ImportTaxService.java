package com.loushuiyifan.report.service;

import java.nio.file.Path;
import java.sql.Date;
import java.time.Instant;
import java.util.ArrayList;
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

import com.google.common.collect.Maps;
import com.loushuiyifan.config.poi.PoiRead;
import com.loushuiyifan.report.ReportConfig;
import com.loushuiyifan.report.bean.ExtImportLog;
import com.loushuiyifan.report.bean.RptImportDataTax;
import com.loushuiyifan.report.dao.ExtImportLogDAO;
import com.loushuiyifan.report.dao.RptImportDataTaxDAO;
import com.loushuiyifan.report.dto.SPDataDTO;
import com.loushuiyifan.report.exception.ReportException;
import com.loushuiyifan.report.serv.DateService;
import com.loushuiyifan.report.serv.ReportReadServ;
import com.loushuiyifan.report.vo.ImportLogDomTaxVO;


@Service
public class ImportTaxService {
    private static final Logger logger = LoggerFactory.getLogger(ImportTaxService.class);

    @Autowired
    ExtImportLogDAO extImportLogDAO;

    @Autowired
    SqlSessionFactory sqlSessionFactory;

    @Autowired
    DateService dateService;

    @Autowired
    RptImportDataTaxDAO rptImportDataTaxDAO;

    /**
     * 解析入库
     */
    @Transactional
    public void save(Path path,
                     Long userId,
                     String month,
                     String remark) throws Exception {

        String filename = path.getFileName().toString();

        //首先将文件解析成bean
        List<RptImportDataTax> list = getRptImportDataTax(path);

        //校验数据是否为空
        int size = list.size();
        if (size == 0) {
            String error = "文件数据为空: " + filename;
            logger.error(error);
            throw new ReportException(error);
        }

        //然后保存解析的数据
        Long logId = extImportLogDAO.nextvalKey();
        //判断是否提交
        String states = rptImportDataTaxDAO.checkPullStates(logId);
        if (states != null) {
            throw new ReportException("数据已提交");
        }

        importDataByGroup(list, logId, month);

        //最后保存日志数据
        ExtImportLog log = new ExtImportLog();
        log.setLogId(logId);
        log.setFileName(filename);
        log.setUserId(Math.toIntExact(userId));
        log.setAcctMonth(month);
        log.setExportDesc(remark);
        log.setIncomeSoure("0");
        log.setStatus("Y");
        log.setImportDate(Date.from(Instant.now()));
        log.setType(ReportConfig.RptImportType.TAX.toString());
        extImportLogDAO.insertSelective(log);

        SPDataDTO dto = new SPDataDTO();
        dto.setLogId(logId);
        rptImportDataTaxDAO.checkTaxData(dto);
        Integer code = dto.getRtnCode();
        
        SPDataDTO dyto = new SPDataDTO();
        dyto.setLogId(logId);
        rptImportDataTaxDAO.pkgCutTaxData(dyto);
        Integer code2 = dto.getRtnCode();
       
        if (code != 0 && code2 !=0) {//非0为失败
            String error = "";
            try {
                delete(userId, logId);
            } catch (Exception e) {
                error = "校验失败后删除数据异常: " + e.getMessage();
            } finally {
                error = String.format("导入数据校验失败: %s ; %s", dto.getRtnMsg(), error);
                logger.error(error);
                throw new ReportException(error);
            }
        }

    }

    /**
     * 查询
     */
    public Map<String, Object> list(String month, Long userId) {
        String type = ReportConfig.RptImportType.TAX.toString();
        List<ImportLogDomTaxVO> list = rptImportDataTaxDAO.listTax(userId, month, type);
        int count = 0;
        double total = 0;
        for (int i = 0; i < list.size(); i++) {
            ImportLogDomTaxVO tmp = list.get(i);
            count += tmp.getCount();
            total += tmp.getSum();
        }
        ImportLogDomTaxVO tmp = new ImportLogDomTaxVO();
        tmp.setCount(count);
        tmp.setSum(total);
        Map<String, Object> result = Maps.newHashMap();
        result.put("count", tmp);
        result.put("list", list);
        if (list != null && list.size() == 0) {
            result.put("msg", "查询成功");
        } else {
            result.put("msg", "查询结果为空");
        }
        
        return result;
    }


    /**
     * 删除数据
     *
     * @param userId
     * @param logId
     */
    public void delete(Long userId, Long logId) throws Exception {
    	SPDataDTO dto = new SPDataDTO();
        dto.setUserId(userId);
        dto.setLogId(logId);
        //存过 IRPT_DEL_TAXDATA
        rptImportDataTaxDAO.deleteTax(dto);
        int code = dto.getRtnCode();
       
        if (code != 0) {//非0为失败
            throw new ReportException("1数据删除失败: " + dto.getRtnMsg());
        }
    }

    /**
     * 税务导入-生成税务
     *
     * @return
     */
    public void taxFile(String month, String type) throws Exception {
        //TODO 税务插入到stat_to_group  存过输入修改MSS_STAT_TO_TAX
    	SPDataDTO dto = new SPDataDTO();
        dto.setLogId(Long.parseLong(type));
        rptImportDataTaxDAO.insertTaxGroup();
        int rtnCode = dto.getRtnCode();
        String rtnMeg = dto.getRtnMsg();
        if (rtnCode != 0) {
            System.out.println(rtnMeg);
        }

        ArrayList<String> list = rptImportDataTaxDAO.selectBatchIds(month);

        logger.info("tax to create file:" + list.size());
        if (list.size() == 0)
            return;
        int i = 0;
        for (String batchId : list) {
            i++;
            String num = String.format("%05d", i);


        }
    }

    /**
     * 解析数据
     *
     * @param path
     * @return
     */
    public List<RptImportDataTax> getRptImportDataTax(Path path) throws Exception {

        PoiRead read = new RptImportDataTaxRead()
                .load(path.toFile())
                .multi(true)//excel数据可以解析多sheet
                .startWith(0, 1);

        List<RptImportDataTax> list = read.read();
        return list;
    }

    /**
     * 在一个session中批量 插入
     *
     * @param list
     * @param logId
     * @param month
     */
    public void importDataByGroup(List<RptImportDataTax> list, Long logId, String month) {

            for (final RptImportDataTax data : list) {
                data.setLogId(logId);
                data.setAcctMonth(month);
                rptImportDataTaxDAO.insertSelective(data);
            }
         
            logger.debug("批量插入结束");
        
    }

    static class RptImportDataTaxRead extends ReportReadServ<RptImportDataTax> {

        protected List<RptImportDataTax> processSheet(Sheet sheet) {
            FormulaEvaluator evaluator = sheet.getWorkbook().getCreationHelper().createFormulaEvaluator();
            List<RptImportDataTax> list = new ArrayList<RptImportDataTax>();
            for (int y = startY; y <= sheet.getLastRowNum(); y++) {
                Row row = sheet.getRow(y);
                RptImportDataTax bean = new RptImportDataTax();

                for (int x = startX; x <= row.getLastCellNum(); x++) {

                    String data = getCellData(row.getCell(x), evaluator);
                    if (StringUtils.isEmpty(data)) {
                        continue;
                    }

                    switch (x) {
                        case 0:
                            bean.setPrctr(data);
                            break;

                        case 1:
                            bean.setAftTaxValue(Double.parseDouble(data));
                            break;
                    }
                }
                list.add(bean);
            }

            return list;
        }

    }


}