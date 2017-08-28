package com.loushuiyifan.shiro;

import com.loushuiyifan.redis.RedisConfig;
import com.loushuiyifan.shiro.cache.EhcacheManagerWrapper;
import com.loushuiyifan.shiro.filter.MyFormAuthenticationFilter;
import com.loushuiyifan.shiro.filter.SysUserFilter;
import com.loushuiyifan.shiro.realm.UserRealm;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.session.mgt.eis.EnterpriseCacheSessionDAO;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.spring.web.config.DefaultShiroFilterChainDefinition;
import org.apache.shiro.spring.web.config.ShiroFilterChainDefinition;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.AbstractShiroFilter;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.config.MethodInvokingFactoryBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;

import javax.servlet.Filter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author 漏水亦凡
 * @create 2017-03-19 16:12.
 */
@Configuration
@Import({RedisConfig.class, EhcacheConfig.class})
public class ShiroConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShiroConfig.class);


    public static final String SYS_USERNAME = "username";
    public static final String SYS_USER = "user";


    @Bean
    public EhCacheManagerFactoryBean ehCacheManagerFactoryBean() {
        EhCacheManagerFactoryBean factoryBean = new EhCacheManagerFactoryBean();
        factoryBean.setConfigLocation(new ClassPathResource("ehcache.xml"));
        factoryBean.setShared(true);
        return factoryBean;
    }

    @Bean
    public EhCacheCacheManager ehCacheCacheManager() {
        EhCacheCacheManager cacheManager = new EhCacheCacheManager();
        cacheManager.setCacheManager(ehCacheManagerFactoryBean().getObject());
        return cacheManager;
    }

    /**
     * 缓存管理器
     *
     * @return
     */
    @Bean
    public EhcacheManagerWrapper shiroEhcacheManager() {
        EhcacheManagerWrapper wrapper = new EhcacheManagerWrapper();
        wrapper.setCacheManager(ehCacheCacheManager());
        return wrapper;
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
        userRealm.setCacheManager(shiroEhcacheManager());
        userRealm.setCachingEnabled(true);
        userRealm.setAuthenticationCachingEnabled(true);
        userRealm.setAuthorizationCachingEnabled(true);
        return userRealm;
    }


    /**
     * 会话DAO
     *
     * @return
     */
    @Bean
    public EnterpriseCacheSessionDAO sessionDAO() {
        EnterpriseCacheSessionDAO dao = new EnterpriseCacheSessionDAO();
        dao.setActiveSessionsCacheName("shiro-activeSessionCache");
        return dao;
    }

    /**
     * 会话管理器
     *
     * @return
     */
    @Bean
    public DefaultWebSessionManager sessionManager() {
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        sessionManager.setGlobalSessionTimeout(30 * 60 * 1000);
        sessionManager.setDeleteInvalidSessions(true);
        sessionManager.setSessionValidationSchedulerEnabled(true);
        sessionManager.setSessionDAO(sessionDAO());
        sessionManager.setSessionIdCookieEnabled(true);
        sessionManager.setCacheManager(shiroEhcacheManager());
        return sessionManager;
    }

    /**
     * 安全管理器
     *
     * @return
     */
    @Bean
    public DefaultWebSecurityManager securityManager() {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(userRealm());
        securityManager.setSessionManager(sessionManager());
        securityManager.setCacheManager(shiroEhcacheManager());
        return securityManager;
    }


    @Bean("shiroMethodInvokingFactoryBean")
    public MethodInvokingFactoryBean methodInvokingFactoryBean() {
        MethodInvokingFactoryBean factoryBean = new MethodInvokingFactoryBean();
        factoryBean.setStaticMethod("org.apache.shiro.SecurityUtils.setSecurityManager");
        factoryBean.setArguments(new DefaultWebSecurityManager[]{securityManager()});
        return factoryBean;
    }

    /**
     * 表单提交 自定义配置过滤器
     *
     * @return
     */
    @Bean
    public MyFormAuthenticationFilter myFormAuthenticationFilter() {
        return new MyFormAuthenticationFilter();
    }

    /**
     * 用户信息 过滤器
     *
     * @return
     */
    @Bean
    public SysUserFilter sysUserFilter() {
        return new SysUserFilter();
    }

    @Bean(name = "filterShiroFilterRegistrationBean")
    protected FilterRegistrationBean filterShiroFilterRegistrationBean() throws Exception {

        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter((AbstractShiroFilter) shiroFilter().getObject());
        filterRegistrationBean.setOrder(1);

        return filterRegistrationBean;
    }

    @Bean
    public ShiroFilterFactoryBean shiroFilter() {
        ShiroFilterFactoryBean factoryBean = new ShiroFilterFactoryBean();
        factoryBean.setSecurityManager(securityManager());
        factoryBean.setLoginUrl("/login");
        factoryBean.setSuccessUrl("/");
        factoryBean.setUnauthorizedUrl("/error");

        Map<String, Filter> filterMap = new LinkedHashMap<>();
        filterMap.put("myForm", myFormAuthenticationFilter());
        filterMap.put("sysUser", sysUserFilter());
        factoryBean.setFilters(filterMap);

        factoryBean.setFilterChainDefinitionMap(shiroFilterChainDefinition().getFilterChainMap());

        return factoryBean;
    }

    @Bean
    public ShiroFilterChainDefinition shiroFilterChainDefinition() {
        DefaultShiroFilterChainDefinition chainDefinition = new DefaultShiroFilterChainDefinition();
        chainDefinition.addPathDefinition("/pic/**", "anon");
        chainDefinition.addPathDefinition("/druid/**", "anon");
        chainDefinition.addPathDefinition("/static/**", "anon");
        chainDefinition.addPathDefinition("/register", "anon");
        chainDefinition.addPathDefinition("/error", "anon");
        chainDefinition.addPathDefinition("/login", "myForm");
        chainDefinition.addPathDefinition("/logout", "logout");
        chainDefinition.addPathDefinition("/**", "user,sysUser");
        return chainDefinition;
    }

    /**
     * 启用shrio授权注解拦截方式
     *
     * @return
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor() {
        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(securityManager());
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
