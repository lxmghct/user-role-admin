package com.example.user.config;

import com.example.common.utils.SecurityUtils;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        SecurityUtils.setDefaultHttpSecurity(http, "/user/login",
                "/home/**",
                "/user/register",
                "/user/checkUserName");
    }

}
