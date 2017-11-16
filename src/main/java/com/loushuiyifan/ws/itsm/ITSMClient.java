package com.loushuiyifan.ws.itsm;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loushuiyifan.ws.WSClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLDecoder;

/**
 * ITSM 接口客户端
 *
 * @author 漏水亦凡
 * @date 2017/11/8
 */
public class ITSMClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(ITSMClient.class);

    public static final String url = "http://134.96.188.67:7002/service/ZJIncomeAuditWs?wsdl";
    public static final String method = "createIncomeAuditFlow";

    public static void main(String[] args) throws Exception {
//        ITSMRequest request = new ITSMRequest();
//        request.setTitle("测试");
//        request.setMonth("201711");
//        request.setEipAccount("sdq.zj");
//        request.setLatnId("本地网");
//        request.setImportLogId(Arrays.asList("测试平台流水号"));
//        request.setSumAmount("100");
//        request.setCurAmount("10");
//        request.setRemark("测试备注");
//        ITSMRequest.Detail detail = new ITSMRequest.Detail();
//        detail.setC4Name("10");
//        detail.setIncomeName("10收入来源");
//        detail.setAmount("10");
//        List<ITSMRequest.Detail> list = new ArrayList<>();
//        list.add(detail);
//
//        request.setDetail(list);
//        ITSMResponse resp =  call(request);

        test();
    }

    private static void test() throws Exception{
        System.out.println(URLDecoder.decode("%2Fzjsso%2Fauth.do%3Fsystem%3Dincome%26redirect%3D%2Fworkshop%2Fform%2Findex.jsp%3FflowId%3D570004584492","utf-8"));

        ITSMResponse resp = new ITSMResponse();
        resp.setStatusCode("0");
        resp.setMessage("失败: eip账号为空");
        System.out.println(new ObjectMapper().writeValueAsString(resp));
    }

    /**
     * ITSM 接口访问
     *
     * @param request
     * @return
     * @throws Exception
     */
    public static ITSMResponse call(ITSMRequest request) throws Exception {
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


}
