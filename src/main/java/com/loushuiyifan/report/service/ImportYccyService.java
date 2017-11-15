package com.loushuiyifan.report.service;

import com.loushuiyifan.config.poi.PoiRead;
import com.loushuiyifan.report.bean.ExtImportYccyLog;
import com.loushuiyifan.report.bean.RptImportYccyData;
import com.loushuiyifan.report.dao.ExtImportYccyLogDAO;
import com.loushuiyifan.report.dao.RptImportYccyDataDAO;
import com.loushuiyifan.report.dto.DeleteYccyDataDTO;
import com.loushuiyifan.report.dto.SPDataDTO;
import com.loushuiyifan.report.exception.ReportException;
import com.loushuiyifan.report.serv.DateService;
import com.loushuiyifan.report.serv.ReportReadServ;
import com.loushuiyifan.report.vo.ImportLogDomTaxVO;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 漏水亦凡
 * @date 2017/9/22
 */
@Service
public class ImportYccyService {

    private static final Logger logger = LoggerFactory.getLogger(ImportYccyService.class);


    @Autowired
    RptImportYccyDataDAO rptImportYccyDataDAO;

    @Autowired
    ExtImportYccyLogDAO extImportYccyLogDAO;

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
                     String remark) throws Exception {

        String filename = path.getFileName().toString();

        //首先将文件解析成bean
        List<RptImportYccyData> list = getRptImportYccyData(path);

        //校验数据是否为空
        int size = list.size();
        if (size == 0) {
            String error = "文件数据为空: " + filename;
            logger.error(error);
            throw new ReportException(error);
        }

        //然后保存解析的数据
        Long logId = extImportYccyLogDAO.nextvalKey();
        importDataByGroup(list, logId, month);

        //最后保存日志数据
        ExtImportYccyLog log = new ExtImportYccyLog();
        log.setLogId(logId);
        log.setUserId(Math.toIntExact(userId));
        log.setAcctMonth(month);
        log.setExportDesc(remark);
        log.setStatus("Y");
        log.setImportDate(Date.from(Instant.now()));
        log.setIncomeSoure("0");
        log.setFileName(filename);
        extImportYccyLogDAO.insertSelective(log);
        
        
        SPDataDTO dto = new SPDataDTO();
        dto.setLogId(logId);
        rptImportYccyDataDAO.checkYCCYData(dto);
        Integer code = dto.getRtnCode();
        if (code != 0) {//非0为失败
            String error = "";
            try {
                delete(Math.toIntExact(userId), logId);
            } catch (Exception e) {
                error = "8校验失败后删除数据异常: " + e.getMessage();
            } finally {
                error = String.format("8导入数据校验失败: %s ; %s", dto.getRtnMsg(), error);
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
    public List<RptImportYccyData> getRptImportYccyData(Path path) throws Exception {

        PoiRead read = new RptImportYccyDataRead()
                .load(path.toFile())
                .multi(true)//excel数据可以解析多sheet
                .startWith(0, 1);

        List<RptImportYccyData> list = read.read();
        return list;
    }

    /**
     * 在一个session中批量 插入
     *
     * @param list
     * @param logId
     * @param month
     */
    public void importDataByGroup(List<RptImportYccyData> list, Long logId, String month) {
       
            for (final RptImportYccyData data : list) {
                data.setLogId(logId);
                data.setAcctMonth(month);
                rptImportYccyDataDAO.insertSelective(data);
            }
         
            logger.debug("批量插入结束");
        
    }

    /**
     * 查询该用户某账期所有导入记录
     *
     * @param userId
     * @param month
     * @return
     */
    public Map<String, Object> list(Long userId, String month) {
    	List<ImportLogDomTaxVO> list =rptImportYccyDataDAO.listDataLog(userId, month);
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
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("count", tmp);
		result.put("list", list);
		
        return result;
    }
    
    /**
     * 删除数据
     *
     * @param userId
     * @param logId
     */
    public void delete(Integer userId, Long logId) {
        
    	DeleteYccyDataDTO dto = new DeleteYccyDataDTO();
        dto.setUserId(userId);
        dto.setLogId(logId);
        rptImportYccyDataDAO.deleteImportData(dto);
        int code = dto.getISts();
        
        if (code != 0) {//非0为失败
            throw new ReportException("1数据删除失败: " + dto.getIRetMsg());
        }
    }


    /**
     * 收入数据解析类
     */
    static class RptImportYccyDataRead extends ReportReadServ<RptImportYccyData> {


        @Override
        protected List<RptImportYccyData> processSheet(Sheet sheet) {
            FormulaEvaluator evaluator = sheet
                    .getWorkbook()
                    .getCreationHelper()
                    .createFormulaEvaluator();

            List<RptImportYccyData> list = new ArrayList<>();
            for (int y = startY; y <= sheet.getLastRowNum(); y++) {
                Row row = sheet.getRow(y);
                RptImportYccyData bean = new RptImportYccyData();
                for (int x = startX; x <= row.getLastCellNum(); x++) {
                    String data = getCellData(row.getCell(x), evaluator);
                    if (StringUtils.isEmpty(data)) {
                        continue;
                    }
                    switch (x) {
                        case 0:
                            bean.setLatnId(Long.parseLong(data));
                            break;
                        case 2:
                            bean.setHorCode(data);
                            break;
                        case 4:
                            bean.setVerCode(data);
                            break;
                        case 6:
                            bean.setReportNo(data);
                            break;
                        case 8:
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
