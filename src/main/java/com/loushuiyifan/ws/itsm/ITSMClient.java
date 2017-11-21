package com.loushuiyifan.ws.itsm;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loushuiyifan.report.bean.RptItsmLog;
import com.loushuiyifan.report.dao.RptItsmLogDAO;
import com.loushuiyifan.report.exception.ReportException;
import com.loushuiyifan.ws.WSClient;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URLDecoder;
import java.sql.Date;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * ITSM 接口客户端
 *
 * @author 漏水亦凡
 * @date 2017/11/8
 */
@Service
public class ITSMClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(ITSMClient.class);

    public static final String url = "http://134.96.188.67:7002/service/ZJIncomeAuditWs?wsdl";
    public static final String method = "createIncomeAuditFlow";

    @Autowired
    RptItsmLogDAO rptItsmLogDAO;

    public static void main(String[] args) throws Exception {
        test();
    }

    private static void test() throws Exception {
        ITSMRequest request = new ITSMRequest();
        request.setTitle("测试标题");
        request.setEipAccount("sdq.zj");
        request.setMonth("201804");
        request.setLatnId("10");
        String[] logs = {"123"};
        request.setImportLogId(Arrays.asList(logs));
        request.setSumAmount("11111111");
        request.setCurAmount("22222222");

        C4Detail detail = new C4Detail();
        detail.setC4Name("江干区");
        detail.setIncomeName("00");
        detail.setAmount("333333333");
        List<C4Detail> list = new ArrayList<>();
        list.add(detail);
        request.setDetail(list);
        request.setRemark("测试备注");

        ITSMResponse resp = callTest(request);
        System.out.println(resp.getStatusCode());
        System.out.println(resp.getStatusCode());
        System.out.println(resp.getMessage());
        System.out.println(URLDecoder.decode(resp.getItsmUrl(), "utf-8"));
    }

    public static void test1() throws Exception {
        String url = "http://134.96.246.24:8085/service/itsm?wsdl";
        String method = "call";
        Object o = WSClient.call(url, method, "{\"statusCode\":\"1\",\"itsmOrderId\":\"" + "123" + "\"}");
        System.out.println(o);
    }


    public static ITSMResponse callTest(ITSMRequest request) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        String param = mapper.writeValueAsString(request);
        LOGGER.info("------request----------{}", param);
        Object o = WSClient.call(url, method, param);
        ITSMResponse response = null;
        if (o != null) {
            LOGGER.info("------respone----------{}", o);
            response = mapper.readValue(o.toString(), ITSMResponse.class);
        }
        return response;
    }

    public ITSMResponse call(ITSMRequest request, String month, Long userId) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        String reqJson = mapper.writeValueAsString(request);
        LOGGER.info("------request----------{}", reqJson);

        //调用接口
        Object o = WSClient.call(url, method, reqJson);
        if (o == null) {
            throw new ReportException("itsm接口调用回执为空");
        }
        String respJson = o.toString();
        LOGGER.info("------respone----------{}", respJson);
        ITSMResponse response = mapper.readValue(respJson, ITSMResponse.class);

        String statusCode = response.getStatusCode();
        String itsmOrderId = response.getItsmOrderId();
        if (!"0".equals(statusCode)) {
            throw new ReportException("itsm接口调用返回失败:" + response.getMessage());
        }
        if (StringUtils.isEmpty(itsmOrderId)) {
            throw new ReportException("itsm接口返回单号为空");
        }

        //保存调用itsm接口日志
        RptItsmLog log = new RptItsmLog();
        log.setAcctMonth(month);
        log.setUserId(userId);
        log.setRequestBody(reqJson);
        log.setRequestTime(Date.from(Instant.now()));
        log.setResponseBody(respJson);//临时为 调用回执，最终会改为 审核回执
        log.setItsmOrderNo(itsmOrderId);
        rptItsmLogDAO.insertSelective(log);

        return response;
    }


}
