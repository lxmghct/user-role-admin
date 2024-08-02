import request from '@/utils/request'
import md5 from 'js-md5'

/**
 * 登录
 * @param {Object} data{userName, password}
 */
export function login(data) {
  const params = new URLSearchParams()
  params.append('userName', data.userName)
  // 这里使用md5加密密码。md5已被证明是不安全的，实际开发中应使用更安全的加密方式
  params.append('password', md5(data.password))
  params.append('platform', 2)
  return request.post('user-api/auth/login', params)
}

/**
 * 退出登录
 */
export function logout() {
  return request.post('user-api/auth/logout')
}

/**
 * 检查用户名是否存在
 * @param {string} userName
 */
export function checkUserName(userName) {
  const url = 'user-api/auth/user-name/exists?userName=' + userName
  return request.post(url)
}
