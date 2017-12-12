package com.loushuiyifan.system.service;

import com.google.common.collect.Maps;
import com.loushuiyifan.report.serv.DateService;
import com.ztesoft.uccp.dubbo.interfaces.UCCPSendService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * @author 漏水亦凡
 * @date 2017/12/7
 */
//@Service
public class SendShortMsgService {
    @Autowired
    UCCPSendService uccpSendService;

    public void send() {
        LocalDateTime now = LocalDateTime.now();
        String systemCode = "CWRPT_";
        String seqId = "0000000001";
        Map<String, String> param = Maps.newHashMap();

        param.put("TransactionId",systemCode+now.format(DateService.YYYYMMDDHHMMSS)+seqId);
        param.put("ExtOrderId",seqId);
        param.put("SendDate",now.format(DateService.YYYY_MM_DD_HH_MM_SS));
        param.put("AccNbr","15345828232");
        param.put("Password","");
        param.put("ContentParam","亦凡|#|123123");
        param.put("SceneId","");
        param.put("SystemCode",systemCode);
        param.put("UserAcct","");
        param.put("OrderContent","");
        param.put("LanId","杭州市");
        param.put("RequestTime",now.format(DateService.YYYY_MM_DD_HH_MM_SS));
        try {
            Map<String,String> result = uccpSendService.sendShortMessage(param);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
