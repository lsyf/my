package com.loushuiyifan.report.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.loushuiyifan.common.bean.Organization;
import com.loushuiyifan.common.util.ProtoStuffSerializerUtil;
import com.loushuiyifan.config.poi.PoiUtils;
import com.loushuiyifan.report.ReportConfig;
import com.loushuiyifan.report.bean.ReportCache;
import com.loushuiyifan.report.bean.RptCase;
import com.loushuiyifan.report.dao.ReportCacheDAO;
import com.loushuiyifan.report.dao.RptQueryCustDAO2017;
import com.loushuiyifan.report.dto.SPDataDTO;
import com.loushuiyifan.report.exception.ReportException;
import com.loushuiyifan.report.serv.CodeListTaxService;
import com.loushuiyifan.report.serv.FileService;
import com.loushuiyifan.report.serv.LocalNetService;
import com.loushuiyifan.report.serv.ReportDownloadService;
import com.loushuiyifan.report.serv.ReportExportServ;
import com.loushuiyifan.report.vo.RptAuditVO;
import com.loushuiyifan.report.vo.RptQueryDataVO;
import com.loushuiyifan.system.service.DictionaryService;

/**
 * @author 漏水亦凡
 * @date 2017/11/7
 */
@Service
public class RptQueryCustService2017 {

    private static Logger logger = LoggerFactory.getLogger(RptQueryCustService2017.class);

    @Autowired
    RptEditionService rptEditionService;

    @Autowired
    RptQueryCustDAO2017 rptQueryCustDAO;

    @Autowired
    LocalNetService localNetService;

    @Autowired
    CodeListTaxService codeListTaxService;

    @Autowired
    DictionaryService dictionaryService;

    @Autowired
    ReportDownloadService reportDownloadService;

    @Autowired
    RptCaseService rptCaseService;

    @Autowired
    ReportCacheDAO reportCacheDAO;

    //生成缓存状态
    ConcurrentHashMap<String, Instant> cacheStatusMap = new ConcurrentHashMap<>(3000);

    @Transactional
    public RptQueryDataVO list(String month,
                               String latnId,
                               String incomeSource,
                               String type,
                               Long userId) {

        // 查询报表实例是否存在
        //case(状态不为2)和cache同步，两者皆有或者两者皆无
        RptCase rptCase = rptCaseService.selectRptCustCase(month, latnId, incomeSource, type);
        if (rptCase != null) {
            Long rptCaseId = rptCase.getRptCaseId();
            ReportCache cache = reportCacheDAO.selectByPrimaryKey(rptCaseId);
            byte[] data = cache.getHtmlData();
            RptQueryDataVO vo = ProtoStuffSerializerUtil.deserialize(data, RptQueryDataVO.class);
            return vo;

        }

        //判断是否正在生成
        String cacheStatusKey = month + latnId + incomeSource + type;
        Instant lastInst = cacheStatusMap.get(cacheStatusKey);
        if (lastInst != null) {
            Long during = Duration.between(lastInst, Instant.now()).getSeconds();
            throw new ReportException("正在生成数据，已耗时: " + during + "秒");
        }
        //设置为正在生成缓存状态
        cacheStatusMap.put(cacheStatusKey, Instant.now());
        RptQueryDataVO vo = new RptQueryDataVO();

        try {
            rptCase = new RptCase();
            rptCase.setAcctMonth(month);
            rptCase.setLatnId(Long.parseLong(latnId));
            rptCase.setIncomeSoure(incomeSource);
            rptCase.setProcessId(1101L);
            rptCase.setReportNo(1701L);
            rptCase.setCreateUserid(userId + "");
            rptCase.setTaxMark(Integer.parseInt(type));
            rptCase.setType(ReportConfig.RptExportType.RPT_QUERY_CUST.toString());//类型
            Long rptCaseId = rptCaseService.saveCaseSelective(rptCase);

            //客户群
            List<Map<String, String>> custs = rptEditionService.listCustMap();
            //指标
            List<Map<String, String>> fields = rptEditionService.listFieldMap2017();
            //数据
            Map<String, Map<String, String>> datas = rptQueryCustDAO.listAsMap(month, incomeSource, latnId, type);
            if (datas == null ||datas.size()==0) {
                throw new ReportException("数据还未准备好！");
            }
            //由于fields接下来会更改，优先生成文件
            String filePath = export(month, latnId, incomeSource, type,
                    custs, fields, datas);

            //生成文件后sftp推送到文件主机
            FileService.push(filePath);

            //生成html需求数据模型
            //首先遍历指标,建立 id->feild
            Map<String, Map<String, String>> fieldMap = Maps.newHashMapWithExpectedSize(2000);
            for (int i = 0; i < fields.size(); i++) {
                Map<String, String> temp = fields.get(i);
                fieldMap.put(temp.get("id"), temp);
            }

            //遍历数据，分别插入到指标数据中
            Iterator<String> it = datas.keySet().iterator();
            while (it.hasNext()) {
                String key = it.next();
                String[] xy = key.split("_");
                String x = xy[0];
                String y = xy[1];
                String v = datas.get(key).get("data");

                Map<String, String> temp = fieldMap.get(x);
                if (temp == null) {
                    continue;
                }
                temp.put(y, v);
            }

            vo.setTitles(custs);
            vo.setDatas(fields);


            // 保存缓存
            rptCaseService.saveReportCache(rptCaseId,
                    ProtoStuffSerializerUtil.serialize(vo),
                    filePath);

        } catch (Exception e) {
            throw new ReportException("查询数据失败: " + e.getMessage());
        } finally {
            //移除生成状态
            cacheStatusMap.remove(cacheStatusKey);
        }
        return vo;
    }

    /**
     * 根据已获取的数据生成文件
     */
    public String export(String month,
                         String latnId,
                         String incomeSource,
                         String type,
                         List<Map<String, String>> custs,
                         List<Map<String, String>> fields,
                         Map<String, Map<String, String>> data) throws Exception {
        String latnName = localNetService.getCodeName(latnId);
        if (latnName == null) {
            throw new ReportException("选择营业区错误,请重试");
        }

        //数据
        LinkedHashMap reportData = new LinkedHashMap<>();
        reportData.put(latnName, data);


        String isName = codeListTaxService.
                getNameByTypeAndData("income_source2017", incomeSource).getCodeName();


        String templatePath = configTemplatePath();
        String outPath = configExportPath(month,
                latnName, isName, type, false);

        ReportExportServ export = new RptQueryExport();
        export.template(templatePath)
                .out(outPath)
                .column(custs)
                .row(fields)
                .data(reportData)
                .export();

        return outPath;
    }


    public String export(String month,
                         String latnId,
                         String incomeSource,
                         String type,
                         Boolean isMulti) throws Exception {

        //非多营业区 可从缓存表中判断是否已经生成数据
        if (!isMulti) {
            RptCase rptCase = rptCaseService.selectRptCustCase(month, latnId, incomeSource, type);
            if (rptCase != null) {
                ReportCache cache = reportCacheDAO.selectByPrimaryKey(rptCase.getRptCaseId());
                String path = cache.getFilePath();

                //首先从文件主机下载文件
                FileService.pull(path);
                return path;
            }
        }

        List<Organization> orgs = localNetService.listUnderKids(latnId, isMulti);
        if (orgs == null || orgs.size() == 0) {
            throw new ReportException("选择营业区错误,请重试");
        }

        //客户群
        List<Map<String, String>> custs = rptEditionService.listCustMap();
        //指标
        List<Map<String, String>> fields = rptEditionService.listFieldMap2017();

        //数据
        LinkedHashMap reportData = new LinkedHashMap<>();
        for (Organization org : orgs) {
            String name = org.getName();
            String id = org.getData();
            Map<String, Map<String, String>> data = rptQueryCustDAO.listAsMap(month, incomeSource, id, type);
            reportData.put(name, data);
        }

        String latnName = orgs.get(0).getName();
        String isName = codeListTaxService.
                getNameByTypeAndData("income_source2017", incomeSource).getCodeName();


        String templatePath = configTemplatePath();
        String outPath = configExportPath(month,
                latnName, isName, type, isMulti);


        ReportExportServ export = new RptQueryExport();
        export.template(templatePath)
                .out(outPath)
                .column(custs)
                .row(fields)
                .data(reportData)
                .export();

        return outPath;
    }

    /**
     * 查询审核信息
     *
     * @param month
     * @param latnId
     * @param incomeSource
     * @param type
     * @param userId
     * @return
     */
    public Map<String, Object> listAudit(String month, String latnId, String incomeSource, String type, Long userId) {
        //首先判断rptCaseId是否存在
        RptCase rptCase = rptCaseService.selectRptCustCase(month, latnId, incomeSource, type);
        if (rptCase == null) {
            throw new ReportException("报表尚未生成,请查询后重试!");
        }
        Long rptCaseId = rptCase.getRptCaseId();

        //然后判断用户是否存在审核权限,查询审核进度无需权限
//        String rtn = rptQueryCustDAO.hasAuditAuthority(userId, rptCaseId);
//
//        if (!"Y".equals(rtn)) {
//            throw new ReportException(rtn);
//        }
        //最后返回审核结果

        Map param = new HashMap();
        param.put("rptCaseId", rptCaseId);
        rptQueryCustDAO.selectAudits(param);
        List<RptAuditVO> list = (List<RptAuditVO>) param.get("list");
        if (list == null || list.size() == 0) {
            throw new ReportException("审核信息为空");
        }

        Map<String, Object> map = Maps.newHashMap();
        map.put("rptCaseId", rptCaseId);
        map.put("list", list);
        return map;
    }

    /**
     * 审核
     *
     * @param rptCaseId
     * @param status
     * @param comment
     * @param userId
     * @return
     */
    public void audit(Long rptCaseId, String status, String comment, Long userId) {
        SPDataDTO dto = new SPDataDTO();
        dto.setRptCaseId(rptCaseId);
        dto.setUserId(userId);
        dto.setStatus(status);
        dto.setComment(comment);
        rptQueryCustDAO.auditRpt(dto);
        if (dto.getRtnCode() != 0) {//非0审核失败
            throw new ReportException(dto.getRtnMsg());
        }
    }


    /**
     * 配置导出路径(包括文件名)
     *
     * @param month
     * @param latnName
     * @param isName
     * @param type
     * @param isMulti
     * @return
     * @throws IOException
     */
    public String configExportPath(String month,
                                   String latnName,
                                   String isName,
                                   String type,
                                   Boolean isMulti) throws IOException {

        String sep = "_";
        String suffix;
        type = ReportConfig.getTaxType(type);
        if (isMulti) {
            suffix = "多营业区.xls";
        } else {
            suffix = "报表.xls";
        }

        String fileName = new StringBuffer("财务报表")
                .append(sep).append(latnName)
                .append(sep).append(month)
                .append(sep).append(isName)
                .append(sep).append(type)
                .append(sep).append(suffix)
                .toString();
        fileName = fileName.replace("*", "");
        Path path = Paths.get(reportDownloadService.configLocation(), month);
        if (!Files.exists(path)) {
            Files.createDirectory(path);
        }
        return path.resolve(fileName).toString();
    }


    /**
     * 配置模板路径
     * TODO 待整合(使用框架封装)
     *
     * @return
     */
    public String configTemplatePath() {
        String sep = File.separator;
        String templateName = dictionaryService.getKidDataByName(ReportConfig.RptExportType.PARENT.toString(),
                ReportConfig.RptExportType.RPT_QUERY_CUST.toString());
        return new StringBuffer(reportDownloadService.configTemplateLocation())
                .append(sep).append(templateName).toString();
    }


    public static class RptQueryExport extends ReportExportServ<Map<String, Map<String, String>>> {


        @Override
        protected List<String> processTitle(Sheet sheet, List<Map<String, String>> columns) throws Exception {

            int columnIndex = 4;//列指针
            int rowIndex = 3;//行指针
            Row idRow = sheet.createRow(rowIndex);
            Row nameRow = sheet.createRow(rowIndex + 1);
            List<String> ids = new ArrayList<>();
            for (int i = 0; i < columns.size(); i++) {
                Map<String, String> title = columns.get(i);
                Cell idCell = idRow.getCell(columnIndex + i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                Cell nameCell = nameRow.getCell(columnIndex + i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);

                String id = title.get("id");
                String name = title.get("name");
                idCell.setCellValue(id);
                nameCell.setCellValue(name);

                ids.add(id);
            }

            return ids;
        }

        @Override
        protected void processSheet(Sheet sheet,
                                    List<String> columns,
                                    List<Map<String, String>> rows,
                                    Map<String, Map<String, String>> datas) throws Exception {
            int rowIndex = 5;//行指针
            int columnIndex = 4;//列指针
            CellStyle cellStyle = PoiUtils.valueCellStyle(getWorkbook());
            
            String _append = "_";
            for (int i = 0; i < rows.size(); i++) {
                Map<String, String> rowData = rows.get(i);

                int rowNum = i + 1;
                String id = rowData.get("id");
                String pid = rowData.get("pid");
                String name = rowData.get("name");

                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(rowNum);
                row.createCell(1).setCellValue(id);
                row.createCell(2).setCellValue(pid);
                row.createCell(3).setCellValue(name);

                
                for (int j = 0; j < columns.size(); j++) {
                    int tempIndex = columnIndex + j;
                    String key = id + _append + columns.get(j);
                    Map<String, String> temp = datas.get(key);
                    double data = temp == null ? 0 : Double.parseDouble(temp.get("data"));
                    Cell cell = row.createCell(tempIndex);
                    cell.setCellValue(data);
                    cell.setCellStyle(cellStyle);
                }

            }


        }
    }
}
