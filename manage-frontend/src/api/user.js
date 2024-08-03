import request from '@/utils/request'
import emptyAvatar from '@/assets/images/empty_avatar.jpg'
import md5 from 'js-md5'

/**
 * 获取用户自身信息
 */
export function getInfo() {
  return request.get('user-api/users/me')
}

/**
 * 获取用户自身头像
 * @returns {avatarUrl}
 */
export function getAvatar() {
  return new Promise((resolve, reject) => {
    request({
      url: 'user-api/users/me/avatar',
      method: 'get',
      responseType: 'blob'
    }).then(response => {
      resolve(generateAvatarUrl(response))
    }).catch(error => {
      reject(error)
    })
  })
}

/**
 * 更新用户自身头像
 * @param {File} file
 */
export function updateAvatar(file) {
  const url = 'user-api/users/me/avatar'
  const formData = new FormData()
  formData.append('avatar', file)
  return request.put(url, formData)
}

/**
 * 获取用户头像
 * @param {number} userId
 * @returns {avatarUrl}
 */
export function getUserAvatar(userId) {
  return new Promise((resolve, reject) => {
    request({
      url: `user-api/users/${userId}/avatar`,
      method: 'get',
      responseType: 'blob'
    }).then(response => {
      resolve(generateAvatarUrl(response))
    }).catch(error => {
      reject(error)
    })
  })
}

/**
 * 获取用户列表
 * @param {Object} data {userName, minCreateTime, maxCreateTime, orderBy, orderMethod, pageNum, pageSize}
 * @returns users
 */
export function getUsers(data) {
  const params = new URLSearchParams(data)
  // orderBy由驼峰转为下划线
  if (params.has('orderBy')) {
    params.set('orderBy', params.get('orderBy').replace(/([A-Z])/g, '_$1').toLowerCase())
  }
  const url = `user-api/users?${params.toString()}`
  return request.get(url)
}

/**
 * 更新用户头像
 * @param {number} userId
 * @param {File} file
 */
export function updateUserAvatar(userId, file) {
  const url = 'user-api/users/' + userId + '/avatar'
  const formData = new FormData()
  formData.append('avatar', file)
  return request.put(url, formData)
}

/**
 * 删除用户
 * @param {list} userIds
 */
export function deleteUsers(userIds) {
  const url = 'user-api/users?ids=' + userIds.join(',')
  return request.delete(url)
}

/**
 * 修改用户状态
 * @param {number} userId
 * @param {number} status
 */
export function changeUserStatus(userId, status) {
  const url = `user-api/users/${userId}/status?status=${status}`
  return request.put(url)
}

/**
 * 添加用户
 * @param {Object} data {userName, trueName, password, email, phone, gender, address, introduction, roleIds}
 */
export function addUser(data) {
  return request.post('user-api/users', data)
}

/**
 * 更新用户信息
 * @param {Object} data {id, userName, trueName, email, phone, gender, address, introduction, roleIds}
 */
export function updateUser(data) {
  return request.put('user-api/users', data)
}

/**
 * 批量检查用户名是否存在
 * @param {list} userNames
 */
export function checkUserNames(userNames) {
  const url = 'user-api/users/user-name/batch-check-existence'
  return request.post(url, userNames)
}

/**
 * 批量创建用户
 * @param {list} data {userName, trueName, password, email, phone, gender, address, introduction, roleIds}
 */
export function batchCreateUsers(data) {
  return request.post('user-api/users/batch', data)
}

/**
 * 修改密码
 * @param {string} oldPassword
 * @param {string} newPassword
 */
export function changePassword(oldPassword, newPassword) {
  const params = new URLSearchParams({
    oldPassword: md5(oldPassword),
    newPassword: md5(newPassword)
  })
  const url = 'user-api/users/me/password'
  return request.put(url, params)
}

/**
 * 修改用户自身信息
 * @param {Object} data {email, phone, address, gender, introduction}
 */
export function updateUserSelfInfo(data) {
  const url = 'user-api/users/me'
  // 驼峰改为下划线
  const newUserObj = {}
  for (const key in data) {
    newUserObj[key.replace(/([A-Z])/g, '_$1').toLowerCase()] = data[key]
  }
  return request.put(url, newUserObj)
}

function generateAvatarUrl(response) {
  if (response.status === 204) {
    return emptyAvatar
  }
  const url = window.URL.createObjectURL(
    new Blob([response.data], { type: 'image/jpg' })
  )
  return url
}

