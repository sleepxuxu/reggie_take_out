package com.rui.reggie.filter;

import com.alibaba.fastjson2.JSON;
import com.rui.reggie.common.BaseContext;
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

@Slf4j
@WebFilter(urlPatterns = "/*")
public class LoginCheckFilter implements Filter {

    private String[] whiteUrls = new String[] {"/employee/login", "/employee/logout", "/common/**"};

    public LoginCheckFilter() {}

    public LoginCheckFilter(String[] whiteUrls) {
        this.whiteUrls = whiteUrls;
    }

    public void setWhiteUrls(String[] whiteUrls) {
        this.whiteUrls = whiteUrls;
    }
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        // 本次请求的url
        String requestUrl = request.getRequestURI();

        log.info("当前请求{}",requestUrl);
        // 使用配置的白名单
        if (whiteUrls == null) {
            whiteUrls = new String[] {"/employee/login", "/employee/logout", "/common/**"};
        }

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
           // 设置线程Id
           Long empId = (Long) request.getSession().getAttribute("employee");
           BaseContext.setThreadLocalId(empId);
           try {
               filterChain.doFilter(request, response);
           } finally {
               BaseContext.remove(); // 清理ThreadLocal，防止内存泄漏
           }
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
        for (String urlPattern : whiteUrls) {
            // 处理通配符模式
            if (urlPattern.endsWith("/**")) {
                String prefix = urlPattern.substring(0, urlPattern.length() - 2);
                if (requestUrl.startsWith(prefix)) {
                    return true;
                }
            }
            // 精确匹配
            else if (requestUrl.equals(urlPattern)) {
                return true;
            }
        }
        return false;
    }
}
