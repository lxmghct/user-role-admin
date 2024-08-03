# 用户角色管理前端

## 项目介绍
本项目是用户角色管理前端，基于vue-element-admin框架开发([https://github.com/PanJiaChen/vue-element-admin](https://github.com/PanJiaChen/vue-element-admin))。目前仅实现了用户、角色、权限的基本管理功能，模型为RBAC模型（Role-Based Access Control）。

## 权限管理实现
### 动态路由(页面级权限)
- 通过后端接口获取用户权限列表，根据权限列表动态生成路由表，实现动态路由。
- router/index.js中定义了动态路由表，在其meta中定义了权限标识，通过权限标识判断用户是否有权限访问该路由。
- 通过router.beforeEach()方法，在路由跳转前判断用户是否有权限访问该路由。(src/permission.js)

### 权限指令(元素级权限, 操作权限)
- 通过自定义指令v-permission，根据用户权限判断是否显示该元素。(src/directive/permission/index.js)
- 在main.js中引入并注册该指令。`Vue.directive('permission', permission)`或`Vue.use(permission)`

### 权限刷新
- 用户权限可能会发生变化(如长时间未操作，修改密码，被管理员修改权限等)，不一定所有情况都需要重新登录。
- 本项目的实现方式是：在axios拦截器中，判断返回的状态码，如果是401则跳转到登录页。如果是403则刷新用户权限，重新获取用户信息和权限列表。(src/utils/request.js)

### 其他说明
vue-element-admin只是一种后台的前端解决方案，在界面显示上控制用户的行为，但并不能真正控制用户的权限（比如可以通过命令行直接访问接口）。真正的权限还是需要在后端进行控制（如Spring Security等）。

## 项目运行
先前往 [nodejs 官网](https://nodejs.org/zh-cn/download/package-manager/) 下载并安装 nodejs。然后执行以下命令：

```bash
# 安装依赖
npm install

# 启动前端
npm run dev
```

## 项目打包
```bash
npm run build:prod
```
