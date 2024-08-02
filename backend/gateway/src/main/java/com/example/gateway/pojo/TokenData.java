package com.example.gateway.pojo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

public class TokenData {

    public Long userId;

    public String username;

    public String role;

    public String permission;

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
     * 小数据量时使用gson性能更好, 详见"https://zhuanlan.zhihu.com/p/344360857"
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

    /**
     * 复制非空值
     *
     * @param tokenData TokenData对象
     */
    public void copyNonNullValueFrom(TokenData tokenData) {
        try {
            for (java.lang.reflect.Field field : this.getClass().getDeclaredFields()) {
                Object value = field.get(tokenData);
                if (value != null) {
                    field.set(this, value);
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}
