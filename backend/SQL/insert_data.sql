
-- role
DELETE FROM `role`;
-- id, name, description, status, create_time, update_time
INSERT INTO `role`(`id`, `name`, `description`, `status`)
VALUES (1, '超级管理员', '拥有所有权限', 1),
       (2, '普通用户', '拥有部分权限', 1),
       (3, '管理员', '拥有管理权限', 1);

-- permission
DELETE FROM `permission`;
-- id, name, code, description, system, classify, parent_id, create_time
INSERT INTO `permission`(`id`, `name`, `code`, `description`, `platform`, `parent_id`)
VALUES (1, '展示平台', 'DISPLAY', '展示平台权限', 1, NULL),
       (2, '管理平台', 'MANAGE', '管理平台权限', 2, NULL),
       (101, '展示平台浏览权限', 'DISPLAY:BROWSE', '展示平台浏览权限', 1, 1),
       (102, '展示平台个人中心权限', 'DISPLAY:EDIT', '展示平台个人中心权限', 1, 1),
       (201, '数据管理权限', 'MANAGE:DATA', '数据管理权限', 2, 2),
       (202, '用户管理权限', 'MANAGE:USER', '用户管理权限', 2, 2);


-- role_permission
DELETE FROM `role_permission`;
-- id, role_id, permission_id

-- 超级管理员
INSERT INTO `role_permission`(`role_id`, `permission_id`, `create_time`)
SELECT 1, id, CURRENT_TIMESTAMP
FROM `permission`;

-- 其他角色
INSERT INTO `role_permission`(`role_id`, `permission_id`, `create_time`)
VALUES
-- 普通用户
(2, 1, CURRENT_TIMESTAMP),
(2, 101, CURRENT_TIMESTAMP),
(2, 102, CURRENT_TIMESTAMP),
(2, 103, CURRENT_TIMESTAMP),
(2, 104, CURRENT_TIMESTAMP),
-- 管理员
(3, 201, CURRENT_TIMESTAMP),
(3, 202, CURRENT_TIMESTAMP);

-- 用户
DELETE FROM `user`;
-- 这里使用的是MD5加密后的密码。MD5已被证明是不安全的，实际开发中，应该使用更安全的加密方式，这里只是为了演示。
-- id, user_name, password, status
INSERT INTO `user`(`id`, `user_name`, `password`, `status`)
VALUES (1, 'admin', 'e10adc3949ba59abbe56e057f20f883e', 1),
       (2, 'user', 'e10adc3949ba59abbe56e057f20f883e', 1),
       (3, 'system-manager', 'e10adc3949ba59abbe56e057f20f883e', 1);

-- user_role
DELETE FROM `user_role`;
-- id, user_id, role_id
INSERT INTO `user_role`(`user_id`, `role_id`, `create_time`)
VALUES
(1, 1, CURRENT_TIMESTAMP),
(2, 2, CURRENT_TIMESTAMP),
(3, 3, CURRENT_TIMESTAMP);

