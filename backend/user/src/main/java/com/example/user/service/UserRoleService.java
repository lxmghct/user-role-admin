package com.example.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.user.entity.UserRole;

import java.util.*;

public interface UserRoleService extends IService<UserRole> {

    void addUserRole(Integer userId, List<Integer> roleIdList, boolean deleteOldRole);

    void batchAddUserRole(List<Map<String, Object>> userRoleList);

    List<Map<String, Object>> getUserIdsByRoleIds(List<Integer> roleIds);

}
