-- 用户表
DROP TABLE IF EXISTS user;
CREATE TABLE user
(
    -- 如果需要用于分布式系统，可以使用 BIGINT 类型，使用例如雪花算法生成的 ID
    id              BIGINT PRIMARY KEY,
    user_name       VARCHAR(50)  NOT NULL,
    password        VARCHAR(255) NOT NULL,
    avatar_path     VARCHAR(255) DEFAULT NULL,
    phone           VARCHAR(20)  DEFAULT NULL,
    email           VARCHAR(100) DEFAULT NULL,
    gender          TINYINT      DEFAULT NULL,
    address         VARCHAR(255) DEFAULT NULL,
    introduction    TEXT         DEFAULT NULL,
    true_name       VARCHAR(50)  DEFAULT NULL,
    create_time     DATETIME     DEFAULT CURRENT_TIMESTAMP,
    update_time     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    status          TINYINT      NOT NULL
);

-- 角色表
DROP TABLE IF EXISTS role;
CREATE TABLE role
(
    id          INT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(50) NOT NULL,
    description VARCHAR(255) DEFAULT NULL,
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    status      TINYINT     NOT NULL
);

-- 权限表
DROP TABLE IF EXISTS permission;
CREATE TABLE permission
(
    id          INT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(50) NOT NULL,
    code        VARCHAR(50) NOT NULL,
    description VARCHAR(255) DEFAULT NULL,
    parent_id   INT          DEFAULT NULL,
    platform    TINYINT     NOT NULL,
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 用户角色关联表
DROP TABLE IF EXISTS user_role;
CREATE TABLE user_role
(
    id          INT AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT NOT NULL,
    role_id     INT    NOT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 角色权限关联表
DROP TABLE IF EXISTS role_permission;
CREATE TABLE role_permission
(
    id            INT AUTO_INCREMENT PRIMARY KEY,
    role_id       INT NOT NULL,
    permission_id INT NOT NULL,
    create_time   DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 登录日志表
DROP TABLE IF EXISTS login_log;
CREATE TABLE login_log
(
    id         INT AUTO_INCREMENT PRIMARY KEY,
    user_id    BIGINT      NOT NULL,
    ip         VARCHAR(50) DEFAULT NULL,
    login_time DATETIME    DEFAULT CURRENT_TIMESTAMP,
    platform   VARCHAR(50) DEFAULT NULL
);

