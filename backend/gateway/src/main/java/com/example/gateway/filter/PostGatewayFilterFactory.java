package com.example.gateway.filter;

import com.example.gateway.config.DevConfig;
import com.example.gateway.constants.HeaderConstants;
import com.example.gateway.pojo.TokenData;
import com.example.gateway.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
public class PostGatewayFilterFactory extends AbstractGatewayFilterFactory<PostGatewayFilterFactory.Config> {

    @Resource
    @Qualifier("tokenBlacklistRedisTemplate")
    private RedisTemplate<String, String> tokenBlacklistRedisTemplate;

    @Resource
    @Qualifier("userInvalidTokenTimeRedisTemplate")
    private RedisTemplate<String, String> userInvalidTokenTimeRedisTemplate;

    public PostGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> chain.filter(exchange).then(Mono.fromRunnable(() -> {
            ServerHttpResponse response = exchange.getResponse();
            ServerHttpRequest request = exchange.getRequest();
            // 判断是否需要刷新token，refresh-token是在PreGatewayFilterFactory中添加的
            // 这里指token自然失效，但用户还在操作，所以只需要刷新token，不需要重新登录
            if (exchange.getAttributes().containsKey(HeaderConstants.REFRESH_TOKEN)) {
                response.getHeaders().set("Authorization", exchange.getAttribute(HeaderConstants.REFRESH_TOKEN));
                response.getHeaders().remove(HeaderConstants.REFRESH_TOKEN);
            }
            // 判断是否需要让token失效，比如退出登录接口，如果需要则在response添加remove-token请求头，值为任意值
            if (response.getHeaders().containsKey(HeaderConstants.REMOVE_TOKEN)) {
                if (DevConfig.ENABLE_REDIS) {
                    String token = request.getHeaders().getFirst(HeaderConstants.AUTHORIZATION);
                    if (token != null && !token.isEmpty()) {
                        Date expiresAt = exchange.getAttribute("token-expire-at");
                        Date now = new Date();
                        // 计算token的失效时间，当超过时间，就自动从redis中删除
                        if (expiresAt != null) {
                            long expireTime = expiresAt.getTime() - now.getTime();
                            tokenBlacklistRedisTemplate.opsForValue().set(token, "", expireTime, TimeUnit.MILLISECONDS);
                        }
                    }
                }
                response.getHeaders().set(HeaderConstants.REMOVE_TOKEN, "1");
                response.getHeaders().remove(HeaderConstants.AUTHORIZATION);
            }
            // 判断是否需要刷新权限，如果需要则在后端接口中给response添加update-permission请求头，值为任意值
            if (response.getHeaders().containsKey(HeaderConstants.UPDATE_PERMISSION)) {
                if (DevConfig.ENABLE_REDIS) {
                    String userIdStr = response.getHeaders().getFirst(HeaderConstants.UPDATE_PERMISSION);
                    if (userIdStr != null) {
                        Date now = new Date();
                        String[] userIds = userIdStr.split(",");
                        Map<String, String> keyValueMap = new HashMap<>();
                        for (String userId : userIds) {
                            long expireTime = now.getTime() + TokenUtils.EXPIRE_TIME;
                            keyValueMap.put(userId, String.valueOf(expireTime));
                        }
                        userInvalidTokenTimeRedisTemplate.opsForValue().multiSet(keyValueMap);
                        // 批量设置过期时间
                        for (String userId : userIds) {
                            userInvalidTokenTimeRedisTemplate.expire(userId, TokenUtils.EXPIRE_TIME, TimeUnit.MILLISECONDS);
                        }
                    }
                }
                response.getHeaders().remove(HeaderConstants.UPDATE_PERMISSION);
            }
            // 判断是否需要生成token，需要生成时就在某些接口中给response的请求头添加token-data，格式为:
            // {"userId":1,"username":"admin","roles":"admin,user","permissions":"test1,test2"}
            if (response.getHeaders().containsKey(HeaderConstants.TOKEN_DATA)) {
                TokenData newTokenData = TokenData.fromJSONString(response.getHeaders().getFirst(HeaderConstants.TOKEN_DATA));
                if (newTokenData != null) {
                    // 先判断原来是否有登录信息，有则更新，没有则添加
                    TokenData tokenDataObj = TokenData.fromJSONString(request.getHeaders().getFirst(HeaderConstants.LOGIN_USER));
                    if (tokenDataObj != null) {
                        tokenDataObj.copyNonNullValueFrom(newTokenData);
                        response.getHeaders().set(HeaderConstants.AUTHORIZATION, TokenUtils.sign(tokenDataObj));
                    } else {
                        response.getHeaders().set(HeaderConstants.AUTHORIZATION, TokenUtils.sign(newTokenData));
                    }
                }
                response.getHeaders().remove(HeaderConstants.TOKEN_DATA);
            }
        }));
    }

    public static class Config {
    }

}