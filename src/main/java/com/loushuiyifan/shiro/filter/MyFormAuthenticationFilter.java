package com.loushuiyifan.shiro.filter;

import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.util.SavedRequest;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * 自定义表单认证过滤器
 *
 * @author 漏水亦凡
 * @create 2017-04-26 14:13.
 */
public class MyFormAuthenticationFilter extends FormAuthenticationFilter {


    /**
     * 在登录成功过后，取savedRequest中保存的url，如果为null，则为设置的successUrl
     * 然后跳转到该url
     *
     * @param token
     * @param subject
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @Override
    protected boolean onLoginSuccess(AuthenticationToken token, Subject subject, ServletRequest request, ServletResponse response) throws Exception {

        String successUrl = null;
        SavedRequest savedRequest = WebUtils.getAndClearSavedRequest(request);
        if (savedRequest != null && savedRequest.getMethod().equalsIgnoreCase(AccessControlFilter.GET_METHOD)) {
            successUrl = savedRequest.getRequestUrl();
        }

        //由于系统框架变更，不再跳转到上次请求url
        //如改回，则解注释下列代码
        successUrl = ((HttpServletRequest) request).getContextPath() + getSuccessUrl();

//        if (successUrl == null) {
//            successUrl = ((HttpServletRequest) request).getContextPath() + getSuccessUrl();
//        }


        request.setAttribute("successUrl", successUrl);
        //登录认证成功后 继续执行请求,并不会重定向指定路径
        return true;
    }

    /**
     * shiro的原本实现是，如果用户登录过，再登录时，由于isAuthenticated为true，则直接不拦截，又跳转到登录页面去了。
     * 修改后，如果是post登录操作，则会重新执行登录过程，刷新当前session中的用户信息
     *
     * @param request
     * @param response
     * @param mappedValue
     * @return
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {

        // 如果是登录操作，直接拦截不通过；
        if (isLoginRequest(request, response)) {
            return false;
        } else {
            return super.isAccessAllowed(request, response, mappedValue);
        }
    }
}
