package com.example.common.vo;

import com.example.common.interfaces.IStatusCode;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.example.common.enums.CommonStatusEnum;
import lombok.Getter;

@Getter
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@SuppressWarnings("unused")
public class ResponseVO<T> {

    private final Integer status;

    private final String message;

    private final T data;

    private ResponseVO(Integer status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    private ResponseVO(IStatusCode statusCode, T data) {
        this.status = statusCode.getCode();
        this.message = statusCode.getMessage();
        this.data = data;
    }

    /**
     * 构建返回对象
     *
     * @param status  状态码
     * @param message 消息
     * @param data    数据
     */
    public static <T> ResponseVO<T> build(Integer status, String message, T data) {
        return new ResponseVO<>(status, message, data);
    }

    /**
     * 构建成功返回对象
     */
    public static <T> ResponseVO<T> success() {
        return new ResponseVO<>(CommonStatusEnum.SUCCESS, null);
    }

    /**
     * 构建成功返回对象
     *
     * @param data 数据
     */
    public static <T> ResponseVO<T> success(T data) {
        return new ResponseVO<>(CommonStatusEnum.SUCCESS, data);
    }

    /**
     * 构建成功返回对象
     *
     * @param message 消息
     */
    public static <T> ResponseVO<T> success(String message) {
        return new ResponseVO<>(CommonStatusEnum.SUCCESS.getCode(), message, null);
    }

    /**
     * 构建成功返回对象
     *
     * @param data    数据
     * @param message 消息
     */
    public static <T> ResponseVO<T> success(T data, String message) {
        return new ResponseVO<>(CommonStatusEnum.SUCCESS.getCode(), message, data);
    }

    /**
     * 构建成功返回对象
     *
     * @param statusCode 状态码枚举
     */
    public static <T> ResponseVO<T> success(IStatusCode statusCode) {
        return new ResponseVO<>(statusCode, null);
    }

    /**
     * 构建成功返回对象
     *
     * @param statusCode 状态码枚举
     * @param data 数据
     */
    public static <T> ResponseVO<T> success(IStatusCode statusCode, T data) {
        return new ResponseVO<>(statusCode, data);
    }

    /**
     * 构建失败返回对象
     */
    public static <T> ResponseVO<T> error() {
        return new ResponseVO<>(CommonStatusEnum.ERROR, null);
    }

    /**
     * 构建失败返回对象
     *
     * @param message 消息
     */
    public static <T> ResponseVO<T> error(String message) {
        return new ResponseVO<>(CommonStatusEnum.ERROR.getCode(), message, null);
    }

    /**
     * 构建失败返回对象
     *
     * @param statusCode 状态码枚举
     */
    public static <T> ResponseVO<T> error(IStatusCode statusCode) {
        return new ResponseVO<>(statusCode, null);
    }

    /**
     * 构建失败返回对象
     *
     * @param statusCode 状态码枚举
     * @param message    消息
     */
    public static <T> ResponseVO<T> error(IStatusCode statusCode, String message) {
        return new ResponseVO<>(statusCode.getCode(), message, null);
    }

}
