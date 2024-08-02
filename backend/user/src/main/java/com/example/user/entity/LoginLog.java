package com.example.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;


/**
 * 登录日志
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class LoginLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    // 用户id
    private Long userId;
    // 登录ip
    private String ip;
    // 登录时间
    private Date loginTime;
    // 登录系统
    private Integer platform;

    public LoginLog(Long userId, String ip, Date loginTime, Integer platform) {
        this.userId = userId;
        this.ip = ip;
        this.loginTime = loginTime;
        this.platform = platform;
    }

}
