package com.example.user.config;

import com.example.common.utils.SecurityUtils;
import com.example.user.constant.ApiConstants;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import java.util.List;

@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        String[] permitUrls = {
                "/auth/login",
                "/auth/user-name/exists",
                "/auth/register"
        };
        for (int i = 0; i < permitUrls.length; i++) {
            permitUrls[i] = ApiConstants.API_PREFIX + permitUrls[i];
        }
        SecurityUtils.setDefaultHttpSecurity(http, permitUrls);
    }

}
