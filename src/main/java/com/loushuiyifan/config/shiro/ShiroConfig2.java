package com.loushuiyifan.config.shiro;

import com.loushuiyifan.config.shiro.cache.RedisSessionDao;
import com.loushuiyifan.config.shiro.cache.ShiroRedisCacheManager;
import com.loushuiyifan.config.shiro.filter.MyFormAuthenticationFilter;
import com.loushuiyifan.config.shiro.filter.SysUserFilter;
import com.loushuiyifan.config.shiro.realm.UserRealm;
import org.apache.commons.io.IOUtils;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.config.MethodInvokingFactoryBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.filter.DelegatingFilterProxy;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author 漏水亦凡
 * @create 2017-03-19 16:12.
 */
@Configuration
public class ShiroConfig2 {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShiroConfig2.class);


    public static final String SYS_USERNAME = "username";
    public static final String SYS_USER = "user";


    @Bean
    public ShiroRedisCacheManager shiroRedisCacheManager(CacheManager cacheManager) {
        ShiroRedisCacheManager shiroRedisCacheManager = new ShiroRedisCacheManager();
        shiroRedisCacheManager.setSpringCacheManger(cacheManager);
        return shiroRedisCacheManager;
    }

    /**
     * 凭证匹配器
     *
     * @return
     */
    @Bean
    public HashedCredentialsMatcher credentialsMatcher() {
        HashedCredentialsMatcher credentialsMatcher = new HashedCredentialsMatcher();
        credentialsMatcher.setHashAlgorithmName("md5");
        credentialsMatcher.setHashIterations(2);
        credentialsMatcher.setStoredCredentialsHexEncoded(true);
        return credentialsMatcher;
    }


    /**
     * Realm实现
     *
     * @return
     */
    @Bean
    public UserRealm userRealm() {
        UserRealm userRealm = new UserRealm();
        userRealm.setCredentialsMatcher(credentialsMatcher()); 
        userRealm.setCachingEnabled(true);
        userRealm.setAuthenticationCachingEnabled(false);
        userRealm.setAuthorizationCachingEnabled(false); //TODO 缓存授权
        return userRealm;
    }


    /**
     * 会话DAO
     *
     * @return
     */
    @Bean
    public RedisSessionDao sessionDAO() {
        RedisSessionDao dao = new RedisSessionDao(true);
        return dao;
    }

    public SimpleCookie simpleCookie() {
        SimpleCookie cookie = new SimpleCookie();
        cookie.setName("SHAREJSESSIONID");
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        return cookie;
    }

    /**
     * 会话管理器
     *
     * @return
     */
    @Bean
    public DefaultWebSessionManager sessionManager() {
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        sessionManager.setSessionIdUrlRewritingEnabled(false);
        sessionManager.setGlobalSessionTimeout(30*60 * 1000);
        sessionManager.setDeleteInvalidSessions(true);
        sessionManager.setSessionValidationSchedulerEnabled(true);
        sessionManager.setSessionDAO(sessionDAO());
        sessionManager.setSessionIdCookieEnabled(true);
        sessionManager.setSessionIdCookie(simpleCookie());
        return sessionManager;
    }

    /**
     * 安全管理器
     *
     * @return
     */
    @Bean
    public DefaultWebSecurityManager securityManager(CacheManager cacheManager) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(userRealm());
        securityManager.setSessionManager(sessionManager());
        securityManager.setCacheManager(shiroRedisCacheManager(cacheManager));
        return securityManager;
    }


    @Bean("shiroMethodInvokingFactoryBean")
    public MethodInvokingFactoryBean methodInvokingFactoryBean(CacheManager cacheManager) {
        MethodInvokingFactoryBean factoryBean = new MethodInvokingFactoryBean();
        factoryBean.setStaticMethod("org.apache.shiro.SecurityUtils.setSecurityManager");
        factoryBean.setArguments(new DefaultWebSecurityManager[]{securityManager(cacheManager)});
        return factoryBean;
    }


    @Bean
    public FilterRegistrationBean shiroFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new DelegatingFilterProxy());
        registration.setDispatcherTypes(DispatcherType.REQUEST);
        registration.addUrlPatterns("/*");
        registration.addInitParameter("targetFilterLifecycle", "true");
        registration.setName("shiroFilter");
        return registration;
    }


    @Bean
    public ShiroFilterFactoryBean shiroFilter(CacheManager cacheManager) {
        ShiroFilterFactoryBean factoryBean = new ShiroFilterFactoryBean();
        factoryBean.setSecurityManager(securityManager(cacheManager));
        factoryBean.setLoginUrl("/login");
        factoryBean.setSuccessUrl("/");
        factoryBean.setUnauthorizedUrl("/error");

        Map<String, Filter> filterMap = new LinkedHashMap<>();
        filterMap.put("myForm", new MyFormAuthenticationFilter());
        filterMap.put("sysUser", new SysUserFilter());
        factoryBean.setFilters(filterMap);

        try {
            String str = IOUtils.toString(new ClassPathResource("shiro.properties").getInputStream(), "utf-8");
            factoryBean.setFilterChainDefinitions(str);
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.error("配置shiroFilter出错", e);
        }

        return factoryBean;
    }


    /**
     * 启用shrio授权注解拦截方式
     *
     * @return
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(CacheManager cacheManager) {
        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(securityManager(cacheManager));
        return advisor;
    }

    @Bean
    @DependsOn({"lifecycleBeanPostProcessor"})
    public DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        advisorAutoProxyCreator.setProxyTargetClass(true);
        return advisorAutoProxyCreator;
    }

    /**
     * Shiro生命周期处理器
     */
    @Bean
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

}
