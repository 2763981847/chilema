package com.chilema.filter;

import com.alibaba.fastjson2.JSON;
import com.chilema.common.BaseContext;
import com.chilema.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
@Component
public class LoginCheckFilter implements Filter {
    private static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        //获取本次请求的URI
        String requestURI = request.getRequestURI();

        String[] uris = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/swagger-ui/**",
                "/swagger-resources/**",
                "/v3/**",
                "/user/login",
                "/user/sendMsg"
        };
        boolean check1 = check(uris, requestURI);
        //判断该请求是否需要拦截
        if (check1) {
            log.info("拦截的请求 " + requestURI + "不需要处理");
            filterChain.doFilter(request, response);
            return;
        }

        //管理端用户登录校验
        //根据session中的值判断用户是否登陆
        Long empId = (Long) request.getSession().getAttribute(("employee"));
        //用户已登录，放行
        if (empId != null) {
            //向线程中存入当前登录用户id
            BaseContext.set(empId);
            filterChain.doFilter(request, response);
            return;
        }

        //客户端用户登录校验
        //根据session中的值判断用户是否登陆
        Long userId = (Long) request.getSession().getAttribute(("user"));
        //用户已登录，放行
        if (userId != null) {
            //向线程中存入当前登录用户id
            BaseContext.set(userId);
            filterChain.doFilter(request, response);
            return;
        }

        //用户未登录，拦截，通过输出流方式向客户端响应数据
        response.getWriter().write(JSON.toJSONString(Result.error("NOTLOGIN")));
        log.error("未登录,拦截页面 {}", requestURI);
    }

    public boolean check(String[] urls, String requestURI) {
        for (String url : urls) {
            boolean match = ANT_PATH_MATCHER.match(url, requestURI);
            if (match) {
                return true;
            }
        }
        return false;
    }
}