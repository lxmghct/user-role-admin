package com.example.common.utils;

import com.example.common.config.DevConfig;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.ArrayList;
import java.util.List;

/**
 * Swagger工具类
 */
public class SwaggerUtils {

    public static Docket createDefaultRestApi(ApiInfo apiInfo) {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.example"))
                .paths(PathSelectors.any())
                .build()
                .securitySchemes(securitySchemes())
                .securityContexts(securityContexts());
    }

    private static List<ApiKey> securitySchemes() {
        List<ApiKey> apiKeyList = new ArrayList<>();
        if (DevConfig.ENABLE_GATEWAY) {
            apiKeyList.add(new ApiKey("login-user", "login-user", "header"));
        } else {
            apiKeyList.add(new ApiKey("Authorization", "Authorization", "header"));
        }
        return apiKeyList;
    }

    private static List<SecurityContext> securityContexts() {
        List<SecurityContext> securityContexts = new ArrayList<>();
        securityContexts.add(SecurityContext.builder().build());
        return securityContexts;
    }
}
