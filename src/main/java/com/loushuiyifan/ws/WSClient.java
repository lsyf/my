package com.loushuiyifan.ws;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.apache.cxf.service.model.BindingInfo;
import org.apache.cxf.service.model.BindingOperationInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.namespace.QName;

/**
 * @author 漏水亦凡
 * @date 2017/11/8
 */
public class WSClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(WSClient.class);


    /**
     * 访问ws接口
     * @param url
     * @param method
     * @param params
     * @return
     * @throws Exception
     */
    public static Object call(String url, String method, Object... params) throws Exception {
        return call(false, url, method, params);
    }

    public static Object call(boolean flag, String url, String method, Object... params) throws Exception {
        JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();
        Client client = dcf.createClient(url);
        Object[] objects;
        if (flag) {
            QName operateQName = getOperateQName(client, method);
            objects = client.invoke(operateQName, params);
        } else {
            objects = client.invoke(method, params);
        }
        return objects.length == 0 ? null : objects[0];
    }

    /**
     * 针对SEI和SIB不在统一个包内的情况，先查找操作对应的Qname，
     * client通过Qname调用对应操作
     *
     * @param client
     * @param operation
     * @return
     */
    public static QName getOperateQName(Client client, String operation) {
        org.apache.cxf.endpoint.Endpoint endpoint = client.getEndpoint();
        QName opName = new QName(endpoint.getService().getName().getNamespaceURI(), operation);
        BindingInfo bindingInfo = endpoint.getEndpointInfo().getBinding();
        if (bindingInfo.getOperation(opName) == null) {
            for (BindingOperationInfo operationInfo : bindingInfo.getOperations()) {
                if (operation.equals(operationInfo.getName().getLocalPart())) {
                    opName = operationInfo.getName();
                    break;
                }
            }
        }
        LOGGER.info("Operation:" + operation + ",namespaceURI:" + opName.getNamespaceURI());
        return opName;
    }
}
