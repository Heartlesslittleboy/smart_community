package com.zqq.config.security.filter;

import com.zqq.config.jwt.JwtUtils;
import com.zqq.config.security.customerdetailservice.CustomerUserDetailService;
import com.zqq.config.security.exception.CustomerAuthenionException;
import com.zqq.config.security.handler.LoginFailureHandler;
import io.jsonwebtoken.Claims;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * token验证过滤器
 */
@Component("checkTokenFilter")
public class CheckTokenFilter extends OncePerRequestFilter {
    //登录请求的地址
    @Value("${itmk.loginUrl}")
    private String loginUrl;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private CustomerUserDetailService customerUserDetailService;
    @Autowired
    private LoginFailureHandler loginFailureHandler;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        System.out.println("doFilterInternal");
        try {
            //获取当前请求的地址
            String url = request.getRequestURI();
            //登录不需要token验证
            if(!url.equals(loginUrl)){
                //token验证
                tokenValidate(request);
            }
        }catch (AuthenticationException e){
            loginFailureHandler.commence(request,response,e);
            return;
        }
        filterChain.doFilter(request,response);
    }

    private void tokenValidate(HttpServletRequest request){
        //从头部获取token
        String token = request.getHeader("token");
        if(StringUtils.isEmpty(token)){
            token = request.getParameter("token");
        }
        if(StringUtils.isEmpty(token)){
            throw new CustomerAuthenionException("token获取失败!");
        }
        //解析token
        String username = jwtUtils.getUsernameFromToken(token);
        if(StringUtils.isEmpty(username)){
            throw new CustomerAuthenionException("token验证失败!");
        }
        //获取用户类型
        Claims claims = jwtUtils.getClaimsFromToken(token);
        String userType = (String) claims.get("userType");
        //查询用户信息,交给spring security
        UserDetails details = customerUserDetailService.loadUserByUsername(username + ":" + userType);
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(details,null,details.getAuthorities());
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }
}

