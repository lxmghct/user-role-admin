package com.example.gateway.config;

import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    private final RouteDefinitionLocator routeDefinitionLocator;

    public GatewayConfig(RouteDefinitionLocator routeDefinitionLocator) {
        this.routeDefinitionLocator = routeDefinitionLocator;
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        // 获取所有在application.yml中配置的路由定义
        routeDefinitionLocator.getRouteDefinitions().subscribe(routeDefinition -> {
            // 为每个路由添加几个额外的过滤器
            routeDefinition.getFilters().add(new FilterDefinition("Pre"));
            routeDefinition.getFilters().add(new FilterDefinition("Post"));
            // 去掉路径中的前两个段
            routeDefinition.getFilters().add(new FilterDefinition("RewritePath=/(?<segment1>[^/]*)/(?<segment2>[^/]*)/(?<segment3>.*)$, /$\\{segment3}"));
        });
        return builder.routes().build();
    }
}
