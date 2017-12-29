package com.loushuiyifan.report.service;

import com.loushuiyifan.common.bean.User;
import com.loushuiyifan.common.util.AESSecurityUtils;
import com.loushuiyifan.config.poi.PoiRead;
import com.loushuiyifan.report.ReportConfig;
import com.loushuiyifan.report.bean.ExtImportLog;
import com.loushuiyifan.report.bean.RptImportDataChennel;
import com.loushuiyifan.report.dao.ExtImportLogDAO;
import com.loushuiyifan.report.dao.RptImportDataChennelDAO;
import com.loushuiyifan.report.dto.SPDataDTO;
import com.loushuiyifan.report.exception.ReportException;
import com.loushuiyifan.report.serv.CodeListTaxService;
import com.loushuiyifan.report.serv.DateService;
import com.loushuiyifan.report.serv.ReportReadServ;
import com.loushuiyifan.report.vo.ImportDataLogVO;
import com.loushuiyifan.system.service.UserService;
import com.loushuiyifan.ws.itsm.C4Detail;
import com.loushuiyifan.ws.itsm.ITSMClient;
import com.loushuiyifan.ws.itsm.ITSMRequest;
import com.loushuiyifan.ws.itsm.ITSMResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLDecoder;
import java.nio.file.Path;
import java.sql.Date;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    @Autowired
    UserService userService;


    @Autowired
    DateService dateService;

    @Autowired
    CodeListTaxService codeListTaxService;

    @Autowired
    ITSMClient itsmClient;


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
        List<RptImportDataChennel> list = getRptImportDataChennels(path);

        //校验数据是否为空
        int size = list.size();
        if (size == 0) {
            throw new ReportException("1文件数据为空: " + filename);
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
        log.setStatus("N");
        log.setImportDate(Date.from(Instant.now()));
        String incomeSource = list.get(0).getIncomeSource();
        log.setIncomeSoure(incomeSource);
        log.setFileName(filename);
        log.setType(ReportConfig.RptImportType.INCOME_DATA.toString());
        extImportLogDAO.insertSelective(log);

        //校验导入数据指标
        SPDataDTO dto = new SPDataDTO();
        dto.setLogId(logId);
        rptImportDataChennelDAO.checkRptImportData(dto);

        Integer code = dto.getRtnCode();
        if (code != 0) {//非0为失败
            throw new ReportException("1导入数据校验失败: " + dto.getRtnMsg());
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
     * 在一个session中批量 插入
     *
     * @param list
     * @param logId
     * @param month
     */
    public void importDataByGroup(List<RptImportDataChennel> list, Long logId, String month) {

        for (final RptImportDataChennel data : list) {
            data.setLogId(logId);
            data.setAcctMonth(month);
            rptImportDataChennelDAO.insertSelective(data);
        }
    }

    /**
     * 查询该用户某账期所有导入记录
     *
     * @param month
     * @return
     */
    public List<ImportDataLogVO> list(String latnId, String month, User user) {
        String type = ReportConfig.RptImportType.INCOME_DATA.toString();
        List<ImportDataLogVO> list = rptImportDataChennelDAO.listIncomeDataLog(latnId, month, type);

        //添加itsm url信息
//        String eipName = "&eipName=" + ReportConfig.ITSM_ACCOUNT;
        String eipName = "&eipName=" + user.getEip();

        String SecretKey = "67987452DFE86867A0F176E7035880BB";
        long currentTimeMillis = System.currentTimeMillis();
        long currentAvaliableTimeMillis = currentTimeMillis / 1000 / 300;
        String tc = "&tc=" +
                AESSecurityUtils.encode(String.valueOf(currentAvaliableTimeMillis), SecretKey);

        String server = "http://134.96.168.104:80";
        for (ImportDataLogVO vo : list) {
            String url = vo.getItsmUrl();
            vo.setItsmUrl(server + url + eipName + tc);
        }
        return list;
    }

    /**
     * 提交数据
     *
     * @param logId
     */
    public void commit(Long logId) {

        ExtImportLog log = extImportLogDAO.selectByPrimaryKey(logId);


        //首先校验是否处于待提交状态
        String status = log.getStatus();
        if (!"N".equals(status)) {
            throw new ReportException("该条记录已经提交!");
        }

        //查询日志账期，校验提交时间
        String month = log.getAcctMonth();
        dateService.checkImportIncomeData(month);

        //切割
        SPDataDTO iseeC4CutDTO = new SPDataDTO();
        iseeC4CutDTO.setLogId(logId);
        iseeC4CutDTO.setMonth(month);
        rptImportDataChennelDAO.iseeC4Cut(iseeC4CutDTO);
        Integer code = iseeC4CutDTO.getRtnCode();

        if (code != 0) {//非0为失败
            throw new ReportException("数据切割失败: " + iseeC4CutDTO.getRtnMsg());
        }

        //提交
        SPDataDTO dto = new SPDataDTO();
        dto.setLogId(logId);
        rptImportDataChennelDAO.commitRptImportData(dto);
        code = dto.getRtnCode();

        if (code != 0) {//非0为失败
            throw new ReportException("数据提交失败: " + dto.getRtnMsg());
        }

    }

    public String getMonth(Long id) {
        ExtImportLog log = new ExtImportLog();
        log.setLogId(id);
        log = extImportLogDAO.selectOne(log);
        if (log == null) {
            return null;
        }
        return log.getAcctMonth();
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
        rptImportDataChennelDAO.deleteImportData(dto);
        int code = dto.getRtnCode();
        if (code != 0) {//非0为失败
            throw new ReportException("1数据删除失败: " + dto.getRtnMsg());
        }
    }

    /**
     * 送审
     *
     * @param logIds
     */
    public void itsm(Long[] logIds, Long userId) {
        //首先保证不能重复送审，策略为查询送审状态是否符合
        ExtImportLog log = extImportLogDAO.selectDistinctData(logIds);
        if (log == null || !"1".equals(log.getIsItsm()) || !"0".equals(log.getItsmStatus())) {
            throw new ReportException("无法送审 或 重复送审");
        }

        //根据用户id获取eip账号
        User user = userService.selectByKey(userId);
        if (user == null || StringUtils.isEmpty(user.getEip())) {
            throw new ReportException("该用户无eip账号");
        }
        String eip = user.getEip();

        //先更改状态,防止重复送审 0->1
        int num = extImportLogDAO.updateItsmStatus(logIds, "1", "0");
        if (num == 0) {
            throw new ReportException("重复送审");
        }
        try {
            //然后查询 流水号的 地市和账期
            String month = log.getAcctMonth();
            Integer latnId = log.getLatnId();


            //统计本次审核金额，以及本月导入金额
            Map<String, String> amountMap = extImportLogDAO.calcAmount(logIds, month, latnId);

            //按照县分收入来源统计本次审核金额
            List<C4Detail> c4Details = extImportLogDAO.calcC4Detail(logIds, month);

            //送审数据处理
            List<String> list = new ArrayList<>();
            for (int i = 0; i < logIds.length; i++) {
                list.add(logIds[i].toString());
            }
            // String area = codeListTaxService.getAreaName(latnId + "local_net");
            String area = codeListTaxService.getAreaName(latnId + "", "local_net");

            String title = String.format("%s账期%s手工收入审批%s",
                    month, area, LocalDateTime.now().format(DateService.YYYYMMDDHHMMSS));

            StringBuilder remark = new StringBuilder();
            List<ExtImportLog> logs = extImportLogDAO.getAllLogs(logIds);
            for (ExtImportLog temp : logs) {
                remark.append(temp.getLogId()).append(" : ")
                        .append(temp.getExportDesc()).append(" ; \n");
            }

            //送审操作
            //保存送审日志
            ITSMRequest request = new ITSMRequest();
            request.setTitle(title);
            request.setEipAccount(eip);
            request.setMonth(month);
            request.setLatnId(latnId + "");
            request.setImportLogId(list);
            request.setSumAmount(amountMap.get("sumAmount"));
            request.setCurAmount(amountMap.get("curAmount"));
            request.setDetail(c4Details);
            request.setRemark(remark.toString());

            ITSMResponse response = itsmClient.call(request, month, userId);

            //成功则更改单号
            String itsmOrderNo = response.getItsmOrderId();
            String itsmUrl = URLDecoder.decode(response.getItsmUrl(), "utf-8");
            extImportLogDAO.updateItsmInfo(logIds, itsmOrderNo, itsmUrl, eip);

        } catch (Exception e) {
            String error = e.getMessage();
            //失败后回退状态
            if (extImportLogDAO.updateItsmStatus(logIds, "0", "1") == 0) {
                error += " , 回退状态失败!";
            }
            logger.error(error, e);
            throw new ReportException("发送送审失败:" + error);
        }

    }


    /**
     * 收入数据解析类
     */
    static class RptImportDataChennelRead extends ReportReadServ<RptImportDataChennel> {

        @Override
        protected List<RptImportDataChennel> processSheet(Sheet sheet) {
//            FormulaEvaluator evaluator = sheet
//                    .getWorkbook()
//                    .getCreationHelper()
//                    .createFormulaEvaluator();

            List<RptImportDataChennel> list = new ArrayList<>();
            for (int y = startY; y <= sheet.getLastRowNum(); y++) {
                Row row = sheet.getRow(y);
                RptImportDataChennel bean = new RptImportDataChennel();
                for (int x = startX; x <= row.getLastCellNum(); x++) {
                    String data = getXLSCellValue(row.getCell(x));

                    if (StringUtils.isEmpty(data)) {
                        continue;
                    }
                    Double temp;
                    switch (x) {
                        case 0:
                            temp = Double.parseDouble(data);
                            bean.setAreaId(temp.intValue());
                            break;
                        case 2:
                            temp = Double.parseDouble(data);
                            bean.setC5Id(temp.longValue());
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
                            temp = Double.parseDouble(data);
                            bean.setSelfCode(temp.intValue());
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
                            temp = Double.parseDouble(data);
                            bean.setIndexData(temp);
                            break;
                        case 17:
                            temp = Double.parseDouble(data);
                            bean.setTaxValue(temp);
                            break;
                        case 18://ICT合同号
                            bean.setIctCode(data);
                            break;
                        case 19://hkont供应商
                            bean.setHkont(data);
                            break;
                        case 20://ICT
                            bean.setItemCode(data);
                            break;
                    }
                }
                //如果areaId和 indexData为空,则默认该行为无效数据
                if (bean.getAreaId() == null&&bean.getIndexData()==null) {
                    continue;
                }
                list.add(bean);
            }
            return list;
        }


    }


}
