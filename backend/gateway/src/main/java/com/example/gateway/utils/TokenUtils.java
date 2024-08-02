package com.example.gateway.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.gateway.pojo.TokenData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class TokenUtils {

    // 有效时间为10小时
    public static long EXPIRE_TIME;
    public static String TOKEN_SECRET;
    public static String ISSUER;
    public static long MAX_IDLE_TIME;

    @Value("${jwt.expire-second:36000}")
    public void setExpireTime(long expireTime) { EXPIRE_TIME = expireTime * 1000; }

    @Value("${jwt.secret:token-secret-001}")
    public void setTokenSecret(String tokenSecret) { TOKEN_SECRET = tokenSecret; }

    @Value("${jwt.issuer:auth0}")
    public void setIssuer(String issuer) { ISSUER = issuer; }

    @Value("${jwt.max-idle-second:36000}")
    public void setMaxIdleTime(long maxIdleTime) { MAX_IDLE_TIME = maxIdleTime * 1000; }

    /**
     * 签名生成
     *
     * @param userId     用户ID
     * @param username   用户名
     * @param role       用户角色
     * @param permission 用户权限
     * @return token
     */
    public static String sign(Long userId, String username, String role, String permission) {
        Date expiresAt = new Date(System.currentTimeMillis() + EXPIRE_TIME);
        return JWT.create()
                .withIssuer(ISSUER)
                .withClaim("userId", userId)
                .withClaim("username", username)
                .withClaim("role", role)
                .withClaim("permission", permission)
                .withExpiresAt(expiresAt)
                .sign(Algorithm.HMAC256(TOKEN_SECRET));
    }

    /**
     * 签名生成
     *
     * @param tokenData 包含userId, username, role, permission
     * @return token
     */
    public static String sign(TokenData tokenData) {
        if (tokenData == null) {
            return null;
        }
        return sign(tokenData.userId, tokenData.username, tokenData.role, tokenData.permission);
    }

    /**
     * 签名验证
     *
     * @param token token
     * @return 是否验证通过
     */
    public static boolean verify(String token) {
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(TOKEN_SECRET)).withIssuer(ISSUER).build();
            verifier.verify(token);
            return true;
        } catch (JWTVerificationException e) {
            return false;
        }
    }

    /**
     * 获取token中的所有信息
     *
     * @param token token
     * @return token中包含的所有信息
     */
    public static Map<String, Object> getAllInfoFromToken(String token) {
        if (token == null || "".equals(token)) {
            return null;
        }
        try {
            DecodedJWT jwt = JWT.decode(token);
            Map<String, Object> result = new HashMap<>();
            TokenData tokenData = new TokenData();
            tokenData.userId = jwt.getClaim("userId").asLong();
            tokenData.username = jwt.getClaim("username").asString();
            tokenData.role = jwt.getClaim("role").asString();
            tokenData.permission = jwt.getClaim("permission").asString();
            result.put("user", tokenData);
            result.put("expiresAt", jwt.getExpiresAt());
            return result;
        } catch (JWTDecodeException e) {
            return null;
        }
    }

}
