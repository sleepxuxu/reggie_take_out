package com.rui.reggie.filter;

import com.alibaba.fastjson2.JSON;
import com.rui.reggie.common.Result;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 检查用户是否已经完成登录
 */

@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        // 本次请求的url
        String requestUrl = request.getRequestURI();

        log.info("当前请求{}",requestUrl);
        // 白名单不做拦截
        String[] whiteUrls = new String[] {
                "/employee/login",
                "/employee/logout"
        };

        // 判断本次请求是否需要处理
        boolean check = check(whiteUrls, requestUrl);

        // 不需要处理直接放行
        if(check) {
            log.info("不用处理{}",requestUrl);
            filterChain.doFilter(request,response);
            return;
        }

        // 判断登录状态
       if(request.getSession().getAttribute("employee") != null) {
           filterChain.doFilter(request, response);
           return;
       }

       response.getWriter().write(JSON.toJSONString(Result.error("NOTLOGIN")));
    }

    /**
     * 检查本次请求是否需要放行
     * @param whiteUrls
     * @param requestUrl
     * @return
     */
    public boolean check(String[] whiteUrls, String requestUrl) {
        for(String url: whiteUrls) {
            boolean match = requestUrl.equals(url);
            if(match) return true;
        }
        return false;
    };
}
