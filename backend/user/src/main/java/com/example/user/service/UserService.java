package com.example.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.user.entity.User;
import com.example.user.dto.UserDTO;
import org.springframework.data.domain.Page;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface UserService extends IService<User> {

    List<Map<String, Object>> getUserRoleAndPermissionsByUserId(List<Integer> userIds);

    Page<User> getUserList(String userName, String minCreateTime, String maxCreateTime, String orderBy, String orderMethod, Integer page, Integer pageSize);

    User addOneUser(UserDTO userDTO);

    Map<String, Object> batchCreateUser(List<UserDTO> userDTOS);

    /**
     * 判断用户名是否存在
     * @param userName 用户名
     * @param throwExceptionWhenExists 什么情况下抛出异常
     *                                 null: 不抛出异常, true: 用户名存在时抛出异常, false: 用户名不存在时抛出异常
     */
    boolean existsByUserName(String userName, Boolean throwExceptionWhenExists);

}
