package com.example.common.utils;

import com.example.common.config.DevConfig;
import com.example.common.enums.CommonStatusEnum;
import com.example.common.pojo.TokenData;
import lombok.NonNull;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Spring Security工具类
 */
public class SecurityUtils {

    public static void setDefaultHttpSecurity(HttpSecurity http, String... permitUrls) throws Exception {

        // 禁用csrf
        http.csrf().disable();
        // 禁用cors
        http.cors().disable();

        if (DevConfig.ENABLE_SECURITY) {
            http.authorizeRequests()
                    .antMatchers("/swagger-ui.html/**",
                            "/webjars/**",
                            "/swagger-resources/**",
                            "/v2/**",
                            "/doc.html").permitAll()
                    .antMatchers(permitUrls).permitAll()
                    //其他任何请求需要验证权限
                    .anyRequest().authenticated()
                    .and().exceptionHandling()
                    .authenticationEntryPoint((request, response, authenticationException) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED, CommonStatusEnum.UNAUTHORIZED.getMessage()))
                    .accessDeniedHandler((request, response, accessDeniedException) -> response.sendError(HttpServletResponse.SC_FORBIDDEN, CommonStatusEnum.FORBIDDEN.getMessage()));
        } else {
            http.authorizeRequests().anyRequest().permitAll();
        }
        // 添加过滤器
        http.addFilterBefore(new LoginFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    private static class LoginFilter extends OncePerRequestFilter {
        @Override
        protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
            TokenData user = ServletUtils.getUserInfo(request);
            if (user != null) {
                // 已登录，创建认证对象
                List<SimpleGrantedAuthority> authorities = Arrays.stream(user.permission.split(",")).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
                authorities.addAll(Arrays.stream(user.role.split(",")).map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user.username, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                // 未登录，创建匿名认证对象
                AnonymousAuthenticationToken anonymousAuthentication = new AnonymousAuthenticationToken("key", "anonymousUser", AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"));
                SecurityContextHolder.getContext().setAuthentication(anonymousAuthentication);
            }
            filterChain.doFilter(request, response);
        }
    }

    /**
     * 检查权限
     *
     * @param needAuthority 需要的角色或权限
     * @return 是否有权限
     */
    public static boolean checkAuthority(String needAuthority) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        for (GrantedAuthority grantedAuthority : authentication.getAuthorities()) {
            if (needAuthority.trim().equals(grantedAuthority.getAuthority())) {
                return true;
            }
        }
        return false;
    }

}

