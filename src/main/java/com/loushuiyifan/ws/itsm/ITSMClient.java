package com.loushuiyifan.ws.itsm;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loushuiyifan.ws.WSClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * ITSM 接口客户端
 *
 * @author 漏水亦凡
 * @date 2017/11/8
 */
public class ITSMClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(ITSMClient.class);

    public static final String url = "http://134.96.246.24:8085/service/itsm?wsdl";
    public static final String method = "call";

    public static void main(String[] args) throws Exception {
        ITSMRequest request = new ITSMRequest();
        request.setMonth("201711");
        ITSMRequest.Detail detail = new ITSMRequest.Detail();
        detail.setC4Name("1");
        List<ITSMRequest.Detail> list = new ArrayList<>();
        list.add(detail);

        request.setDetail(list);
        call(request);
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
        Object o = WSClient.call(url, method, param);
        ITSMResponse response = null;
        if (o != null) {
            LOGGER.info(o.toString());
            response = mapper.readValue(o.toString(), ITSMResponse.class);
        }
        return response;
    }


}
