package com.example.common.utils;

import com.example.common.config.DevConfig;
import com.example.common.enums.HeaderEnum;
import com.example.common.pojo.TokenData;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Servlet工具类
 */
public class ServletUtils {

    /**
     * 获取响应对象
     *
     * @return 响应对象
     */
    public static HttpServletResponse getResponse() {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        assert servletRequestAttributes != null;
        HttpServletResponse response = servletRequestAttributes.getResponse();
        assert response != null;
        return response;
    }

    /**
     * 获取请求对象
     *
     * @return 请求对象
     */
    public static HttpServletRequest getRequest() {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        assert servletRequestAttributes != null;
        return servletRequestAttributes.getRequest();
    }

    /**
     * 获取请求头中的用户信息
     *
     * @param request 请求对象
     * @return 用户信息
     */
    public static TokenData getUserInfo(HttpServletRequest request) {
        if (DevConfig.ENABLE_GATEWAY) {
            return TokenData.fromJSONString(request.getHeader(HeaderEnum.LOGIN_USER.getValue()));
        } else {
            return TokenUtils.getAllInfoFromToken(request.getHeader(HeaderEnum.AUTHORIZATION.getValue()));
        }
    }

    /**
     * 获取请求头中的用户信息
     *
     * @return 用户信息
     */
    public static TokenData getUserInfo() {
        return getUserInfo(getRequest());
    }

    /**
     * 从请求头中获取用户id
     *
     * @return 用户id
     */
    public static Long getUserId() {
        TokenData userData = getUserInfo();
        return userData == null ? null : userData.userId;
    }

    /**
     * 从请求头中获取用户名
     *
     * @return 用户名
     */
    public static String getUsername() {
        TokenData userData = getUserInfo();
        return userData == null ? null : userData.username;
    }

    /**
     * 设置token数据
     *
     * @param userId      用户id
     * @param username    用户名
     * @param roles       角色
     * @param permissions 权限
     */
    public static void setTokenData(Long userId, String username, String roles, String permissions) {
        if (DevConfig.ENABLE_GATEWAY) {
            TokenData tokenData = new TokenData(userId, username, roles, permissions);
            getResponse().setHeader(HeaderEnum.TOKEN_DATA.getValue(), tokenData.toJSONString());
        } else {
            String token = TokenUtils.sign(new TokenData(userId, username, roles, permissions));
            getResponse().setHeader(HeaderEnum.AUTHORIZATION.getValue(), token);
        }
    }

    /**
     * 移除token
     */
    public static void removeToken() {
        getResponse().setHeader(HeaderEnum.REMOVE_TOKEN.getValue(), "1");
    }

    /**
     * 更新用户权限
     *
     * @param userIds 用户id列表
     */
    public static void updatePermission(List<Long> userIds) {
        if (userIds != null && userIds.size() > 0) {
            StringBuilder s = new StringBuilder(userIds.get(0) + "");
            for (int i = 1; i < userIds.size(); i++) {
                s.append(",").append(userIds.get(i));
            }
            getResponse().setHeader(HeaderEnum.UPDATE_PERMISSION.getValue(), s.toString());
        }
    }

}
