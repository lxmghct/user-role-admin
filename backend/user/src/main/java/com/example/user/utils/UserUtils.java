package com.example.user.utils;

import com.example.common.enums.StorageEnum;
import com.example.common.utils.FileUtils;
import com.example.common.utils.ServletUtils;
import com.example.common.vo.ResponseVO;
import com.example.user.entity.LoginLog;
import com.example.user.entity.User;
import com.example.user.enums.StatusEnum;
import com.example.user.enums.PlatformEnum;
import com.example.user.service.LoginLogService;
import com.example.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.util.*;

@Component
public class UserUtils {

    private final UserService userService;
    private final LoginLogService loginLogService;

    private static UserUtils instance;

    @Autowired
    public UserUtils(UserService userService, LoginLogService loginLogService) {
        this.userService = userService;
        this.loginLogService = loginLogService;
    }

    @PostConstruct
    public void init() {
        instance = this;
    }

    /**
     * 检查用户是否有某个系统的权限
     *
     * @param roleAndPermissionInfo 用户角色和权限信息
     * @param platformId            系统id
     */
    public static boolean checkUserHasPermissionForPlatform(List<Map<String, Object>> roleAndPermissionInfo, Integer platformId) {
        if (roleAndPermissionInfo != null && roleAndPermissionInfo.size() > 0) {
            Map<String, Object> tempInfo = roleAndPermissionInfo.get(0);
            String platformList = (String) tempInfo.get("platformList");
            if (platformList != null) {
                for (String s : platformList.split(",")) {
                    if (s.equals(String.valueOf(platformId))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 设置用户角色和权限信息
     *
     * @param user                  用户
     * @param roleAndPermissionInfo 用户角色和权限信息
     */
    public static void setUserRoleAndPermissionInfo(User user, List<Map<String, Object>> roleAndPermissionInfo) {
        if (roleAndPermissionInfo != null && roleAndPermissionInfo.size() > 0) {
            Map<String, Object> tempInfo = roleAndPermissionInfo.get(0);
            user.setRoleList(Arrays.asList(((String) tempInfo.get("roleNameList")).split(",")));
            user.setPermissionList(Arrays.asList(((String) tempInfo.get("permissionCodeList")).split(",")));
        }
    }

    /**
     * 更新用户头像
     *
     * @param user   用户
     * @param avatar 头像
     */
    public static boolean updateUserAvatar(User user, MultipartFile avatar) {
        if (avatar == null) {
            return false;
        }
        // 获取头像后缀
        String suffix = Objects.requireNonNull(avatar.getOriginalFilename()).substring(avatar.getOriginalFilename().lastIndexOf("."));
        // 如果之前路径存在则删除
        String avatarName = user.generateAvatarName(suffix);
        if (!avatarName.equals(user.getAvatarPath())) {
            if (user.getAvatarPath() != null) {
                if (!FileUtils.deleteFileOrDirectory(StorageEnum.USER_AVATAR_PATH.getDesc() + user.getAvatarPath())) {
                    return false;
                }
            }
            user.setAvatarPath(avatarName);
            instance.userService.updateById(user);
        }
        return FileUtils.createOrUpdateMultipartFile(StorageEnum.USER_AVATAR_PATH.getDesc(), avatarName, avatar);
    }

    /**
     * 处理用户登录
     *
     * @param user       用户
     * @param password   密码
     * @param platformId 系统id
     */
    public static ResponseVO<Map<String, Object>> handleUserLogin(User user, String password, Integer platformId) {
        if (PlatformEnum.getNameById(platformId) == null) {
            return ResponseVO.error(StatusEnum.PLATFORM_NOT_EXISTS);
        }
        if (!User.Status.ENABLE.equals(user.getStatus())) {
            return ResponseVO.error(StatusEnum.USER_DISABLED);
        }
        if (!user.getPassword().equals(password)) {
            return ResponseVO.error(StatusEnum.PASSWORD_ERROR);
        }
        List<Map<String, Object>> roleAndPermissionInfo = instance.userService.getUserRoleAndPermissionsByUserId(Collections.singletonList(user.getId()));
        setUserRoleAndPermissionInfo(user, roleAndPermissionInfo);
        String roleNameList = String.join(",", user.getRoleList());
        String permissionCodeList = String.join(",", user.getPermissionList());
        if (!checkUserHasPermissionForPlatform(roleAndPermissionInfo, platformId)) {
            return ResponseVO.error(StatusEnum.PERMISSION_DENIED_FOR_PLATFORM);
        }
        String ip = ServletUtils.getRequest().getHeader("X-Real-IP");
        instance.loginLogService.save(new LoginLog(user.getId(), ip, new Date(), platformId));
        Map<String, Object> returnMap = new HashMap<>();
        returnMap.put("user", user);
        ServletUtils.setTokenData(user.getId(), user.getUserName(), roleNameList, permissionCodeList);
        return ResponseVO.success(returnMap);
    }

    private static final String USER_NAME_PATTERN = "^[a-zA-Z0-9._-]+$";

    /**
     * 检查用户名中是否包含非法字符
     *
     * @param userName 用户名
     */
    public static boolean isUserNameContainsIllegalCharacter(String userName) {
        return !userName.matches(USER_NAME_PATTERN);
    }

}
