package com.loushuiyifan.report.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author 漏水亦凡
 * @date 2017/9/20
 */
@ConfigurationProperties("storage")
public class StorageProperties {

    private String location = "storage";

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

}
