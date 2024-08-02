package com.example.user.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.common.vo.ResponseVO;
import com.example.user.constant.ApiConstants;
import com.example.user.entity.Permission;
import com.example.user.service.PermissionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Api(tags = "permission-api")
@RequestMapping(ApiConstants.API_PREFIX + "/permissions")
public class PermissionController {

    private final PermissionService permissionService;

    @Autowired
    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    /**
     * 获取权限列表
     */
    @GetMapping
    @ApiOperation(value = "获取权限列表")
    public ResponseVO<List<Permission>> getPermissionList() {
        List<Permission> permissionList = permissionService.list(new QueryWrapper<>());
        return ResponseVO.success(permissionList);
    }

}