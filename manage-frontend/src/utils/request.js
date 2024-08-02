import router from '@/router'
import axios from 'axios'
import { getToken, setToken, removeToken } from './auth'
import ElementUI from 'element-ui'

const service = axios.create({
  timeout: 10000 // request timeout
})

// axios拦截器, 默认增加请求头token
service.interceptors.request.use(
  config => {
    const token = getToken()
    if (token) {
      config.headers.Authorization = token
    }
    return config
  },
  err => {
    return Promise.reject(err)
  }
)

// 防止同时弹出多个错误提示
let hasErrorMessage = false
const showErrorMessage = (response) => {
  if (!hasErrorMessage && response.data.message && response.data.message !== '用户未登录，请先登录') {
    ElementUI.Message.error(response.data.message)
    hasErrorMessage = true
    setTimeout(() => {
      hasErrorMessage = false
    }, 1000)
  }
}
// 更新token
const updateToken = (response) => {
  const oldToken = getToken()
  const newToken = response.headers.authorization
  if (newToken && oldToken !== newToken) {
    setToken(newToken)
  }
  // 检查是否需要移除token
  if (response.headers['remove-token']) {
    removeToken()
  }
}
// axios 接口错误拦截
service.interceptors.response.use(
  function(res) {
    updateToken(res)
    if (res.data && res.data.status && (res.data.status < 200 || res.data.status >= 300)) {
      showErrorMessage(res)
      return Promise.reject(res)
    } else {
      return res
    }
  },
  function(error) {
    if (![500, 400, 404].includes(error.response.status)) {
      updateToken(error.response)
      showErrorMessage(error.response)
      // 401 Unauthorized
      if (error.response.status === 401) {
        // UserUtils.resetUserStore()
        setTimeout(() => {
          const path = router.app.$route.path
          if (path === '/login') {
            location.reload()
          } else {
            router.push({ path: '/login' })
          }
        }, 1000)
      }
    }
    return Promise.reject(error)
  }
)

export default service
