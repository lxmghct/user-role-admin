package com.example.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private int port;

    @Bean
    @Primary
    public RedisConnectionFactory tokenBlacklistRedisConnectionFactory() {
        return createRedisConnectionFactory(0);
    }

    @Bean
    public RedisConnectionFactory userInvalidTokenTimeRedisConnectionFactory() {
        return createRedisConnectionFactory(1);
    }

    /**
     * 使用0号库存储token黑名单
     * 用于存储用户的退出登录等操作手动失效的token
     * 存储键值对: 失效token-空字符串
     * 存储时间: 失效时间->当前时间
     */
    @Bean(name = "tokenBlacklistRedisTemplate")
    public RedisTemplate<String, Object> tokenBlacklistRedisTemplate() {
        return createRedisTemplate(tokenBlacklistRedisConnectionFactory());
    }

    /**
     * 使用1号库存储需要刷新权限的用户
     * 当用户的用户名、角色、权限等敏感信息发生变化时，需要让旧的token全部失效
     * 存储键值对: userId->(敏感信息更新时间+token失效时间)的时间戳
     * 存储时间: token最大有效时间
     * 如果新传入的token的失效时间小于该时间，则说明token的生成时间早于敏感信息更新时间，需要重新登录
     */
    @Bean(name = "userInvalidTokenTimeRedisTemplate")
    public RedisTemplate<String, Object> userInvalidTokenTimeRedisTemplate() {
        return createRedisTemplate(userInvalidTokenTimeRedisConnectionFactory());
    }

    private RedisConnectionFactory createRedisConnectionFactory(int dbIndex) {
        LettuceConnectionFactory factory = new LettuceConnectionFactory(host, port);
        factory.setDatabase(dbIndex);
        return factory;
    }

    private RedisTemplate<String, Object> createRedisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.afterPropertiesSet();
        return template;
    }
}
