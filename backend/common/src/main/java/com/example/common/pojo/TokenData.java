package com.example.common.pojo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

public class TokenData {

    public Long userId;

    public String username;

    public String role;

    public String permission;

    public TokenData() {
    }

    public TokenData(Long userId, String username, String role, String permission) {
        this.userId = userId;
        this.username = username;
        this.role = role;
        this.permission = permission;
    }

    /**
     * 转换为JSON字符串
     *
     * @return JSON字符串
     */
    public String toJSONString() {
        return "{\"userId\":" + userId + ",\"username\":" + getFormattedString(username) +
                ",\"role\":" + getFormattedString(role) + ",\"permission\":" + getFormattedString(permission) + "}";
    }

    private static String getFormattedString(String value) {
        return value == null ? null : "\"" + value + "\"";
    }


    private final static Gson gson = new GsonBuilder().create();

    /**
     * 根据JSON字符串生成TokenData对象
     *
     * @param jsonString JSON字符串
     * @return TokenData对象
     */
    public static TokenData fromJSONString(String jsonString) {
        if (jsonString == null) {
            return null;
        }
        try {
            return gson.fromJson(jsonString, TokenData.class);
        } catch (JsonSyntaxException e) {
            return null;
        }
    }

}
