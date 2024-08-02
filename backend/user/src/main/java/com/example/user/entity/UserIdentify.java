package com.example.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户认证表
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class UserIdentify implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    // 用户id
    private Long userId;

    // 学校代码
    private Integer universityCode;

    // 认证时间
    private Date identifyTime;

    // 过期时间
    private Date expireTime;

    // 学历
    private Integer qualification;

}
