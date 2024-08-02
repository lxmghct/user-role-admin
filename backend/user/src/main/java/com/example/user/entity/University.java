package com.example.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 学校表
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class University implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    // 学校代码
    private Integer code;

    // 学校名称
    private String name;

    // 学校名缩写
    private String abbreviation;

}
