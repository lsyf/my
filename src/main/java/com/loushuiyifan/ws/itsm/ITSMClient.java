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

//        public static final String url = "http://134.96.188.67:7002/service/ZJIncomeAuditWs?wsdl";
    public static final String url = "http://134.96.168.104:80/service/ZJIncomeAuditWs?wsdl";
//    public static final String url = "http://134.96.168.46:7001/service/ZJIncomeAuditWs?wsdl";
    public static final String method = "createIncomeAuditFlow";

    @Autowired
    RptItsmLogDAO rptItsmLogDAO;

    public static void main(String[] args) throws Exception {
        test1();
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
        String url = ITSMClient.url;
        String method = ITSMClient.method;
//        Object o = WSClient.call(url, method, "{\"title\":\"201801账期杭州市手工收入审批20171212172731\",\"eipAccount\":\"sdq.zj\",\"month\":\"201801\",\"latnId\":\"10\",\"importLogId\":[\"30877\",\"30907\"],\"sumAmount\":\"2557115.57\",\"curAmount\":\"2557115.57\",\"remark\":\"201801账期杭州市手工收入审批20171212172731\",\"detail\":[{\"c4Name\":\"临安市\",\"incomeName\":\"0501\",\"amount\":\"54142.24\"},{\"c4Name\":\"上城区\",\"incomeName\":\"0501\",\"amount\":\"274431.04\"},{\"c4Name\":\"下沙区\",\"incomeName\":\"0501\",\"amount\":\"-723651.07\"},{\"c4Name\":\"淳安县\",\"incomeName\":\"0501\",\"amount\":\"-216017.87\"},{\"c4Name\":\"萧山区\",\"incomeName\":\"0501\",\"amount\":\"18282608.04\"},{\"c4Name\":\"拱墅区\",\"incomeName\":\"0501\",\"amount\":\"-1079020.31\"},{\"c4Name\":\"余杭区\",\"incomeName\":\"0501\",\"amount\":\"-2273180.74\"},{\"c4Name\":\"桐庐县\",\"incomeName\":\"0501\",\"amount\":\"-405188.11\"},{\"c4Name\":\"建德市\",\"incomeName\":\"0501\",\"amount\":\"473106.77\"},{\"c4Name\":\"富阳市\",\"incomeName\":\"0501\",\"amount\":\"-5819481.74\"},{\"c4Name\":\"江干区\",\"incomeName\":\"0501\",\"amount\":\"-205621.14\"},{\"c4Name\":\"杭州市辖区\",\"incomeName\":\"0501\",\"amount\":\"-3696698.64\"},{\"c4Name\":\"下城区\",\"incomeName\":\"0501\",\"amount\":\"-339526.04\"},{\"c4Name\":\"滨江区\",\"incomeName\":\"0501\",\"amount\":\"-1439388.36\"},{\"c4Name\":\"西湖区\",\"incomeName\":\"0501\",\"amount\":\"-329398.5\"}]}\n");
        Object o = WSClient.call(url, method, "");
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
