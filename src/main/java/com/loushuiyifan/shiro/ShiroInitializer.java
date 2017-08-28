package com.loushuiyifan.shiro;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.filter.DelegatingFilterProxy;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.EnumSet;

/**
 * @author 漏水亦凡
 * @create 2017-03-19 16:49.
 */
public class ShiroInitializer implements WebApplicationInitializer {
    private static final Logger LOG = LoggerFactory.getLogger(ShiroInitializer.class);

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        LOG.info("ShiroInitializer 配置");

        DelegatingFilterProxy webStatFilter = new DelegatingFilterProxy();
        FilterRegistration.Dynamic filterRegistration = servletContext.addFilter(
                "shiroFilter", webStatFilter);
        filterRegistration.setInitParameter("targetFilterLifecycle", "true");
        filterRegistration.addMappingForUrlPatterns(
                EnumSet.of(DispatcherType.REQUEST), true, "/*");


    }
}
