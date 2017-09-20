package com.loushuiyifan.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @author 漏水亦凡
 * @create 2017-03-17 10:16.
 */

@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {

    public static final String PATH = "D:/pic";

    /**
     * 静态资源映射
     *
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");

        registry.addResourceHandler("/pic/**")
                .addResourceLocations("file:" + PATH)
                .setCachePeriod(3155926);

    }


}
