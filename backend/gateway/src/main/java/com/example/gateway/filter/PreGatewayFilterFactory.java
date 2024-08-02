package com.example.gateway.filter;

import com.example.gateway.config.DevConfig;
import com.example.gateway.constants.HeaderConstants;
import com.example.gateway.pojo.TokenData;
import com.example.gateway.utils.ResponseUtils;
import com.example.gateway.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.net.URI;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Component
public class PreGatewayFilterFactory extends AbstractGatewayFilterFactory<PreGatewayFilterFactory.Config> {

    @Resource
    @Qualifier("tokenBlacklistRedisTemplate")
    private RedisTemplate<String, String> tokenBlacklistRedisTemplate;

    @Resource
    @Qualifier("userInvalidTokenTimeRedisTemplate")
    private RedisTemplate<String, String> userInvalidTokenTimeRedisTemplate;

    public PreGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpRequest.Builder builder = request.mutate();

            // 添加随机id，否则传递到下游服务的Security后会被缓存，同一个请求反复发送会导致后续Servlet上下文被缓存不会更新
            // 例如: 连续以未登录和登录的状态请求同一个接口，会导致登录时的接口依旧获取不到用户信息，获取到的是未登录时的信息
            // 原因是第一次请求时，Security过滤器拦截了请求，但请求的上下文被缓存了，第二次请求时，Security过滤器不再拦截，通过过滤器后，直接从缓存中获取了未登录时的上下文
            String url = request.getURI().toString();
            builder.uri(URI.create(url + (url.contains("?") ? "&" : "?") +
                    "random-id=" + UUID.randomUUID().toString().substring(0, 8)));
            // 获取Authorization
            String token = request.getHeaders().getFirst(HeaderConstants.AUTHORIZATION);
            if (DevConfig.ENABLE_REDIS) {
                // 检查token是否有效，如果在黑名单中，就直接返回
                if (token != null && !token.isEmpty()) {
                    if (Boolean.TRUE.equals(tokenBlacklistRedisTemplate.hasKey(token))) {
                        return ResponseUtils.error(exchange, "token已失效，请重新登录", HttpStatus.UNAUTHORIZED);
                    }
                }
            }
            // 获取token中的信息，并验证token是否有效
            Map<String, Object> tokenData = TokenUtils.getAllInfoFromToken(token);
            if (tokenData != null) {
                Date expiresAt = (Date) tokenData.get("expiresAt");
                Date now = new Date();
                if (!TokenUtils.verify(token)) {
                    if (expiresAt.before(now)) {
                        return ResponseUtils.error(exchange, "用户认证信息已过期，请重新登陆", HttpStatus.UNAUTHORIZED);
                    } else {
                        return ResponseUtils.error(exchange, "签名异常，请重新登录", HttpStatus.UNAUTHORIZED);
                    }
                }
                TokenData user = (TokenData) tokenData.get("user");
                // 检查token是否在用户的失效token表中
                if (DevConfig.ENABLE_REDIS) {
                    String invalidTime = userInvalidTokenTimeRedisTemplate.opsForValue().get(user.userId + "");
                    if (invalidTime != null) {
                        long maxExpireTime = Long.parseLong(invalidTime);
                        if (maxExpireTime > expiresAt.getTime()) {
                            return ResponseUtils.error(exchange, "用户权限已更新，请重新登录", HttpStatus.UNAUTHORIZED);
                        }
                    }
                }
                // 检查token是否快要过期，如果快要过期，就签发新的token
                if (expiresAt.getTime() - now.getTime() < TokenUtils.MAX_IDLE_TIME) {
                    // 可以使用redis防止并发请求时重复签发token，但也需要考虑redis并发访问的问题
                    // 也可以不使用redis，直接生成新token
                    String newToken = TokenUtils.sign(user);
                    exchange.getAttributes().put(HeaderConstants.REFRESH_TOKEN, newToken);
                }
                // 将需要用到的信息放到exchange和header中
                exchange.getAttributes().put("token-expire-at", expiresAt);
                tokenData.remove("expiresAt");
                builder.headers(httpHeaders -> httpHeaders.add(HeaderConstants.LOGIN_USER, user.toJSONString()));
            } else {
                // 防止伪造的login-user信息传递到下游服务
                builder.headers(httpHeaders -> httpHeaders.remove(HeaderConstants.LOGIN_USER));
            }
            return chain.filter(exchange.mutate().request(builder.build()).build());
        };
    }

    public static class Config {
    }

}
