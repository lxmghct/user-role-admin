import Vue from 'vue'

let loading = null

/**
 * 创建全屏加载
 * @param {String} text - 加载文本
 */
export function createFullScreenLoading(text) {
  loading = Vue.prototype.$loading({
    lock: true,
    text: text || 'Loading',
    spinner: 'el-icon-loading',
    background: 'rgba(255, 255, 255, 0.7)'
  })
}

/**
 * 关闭全屏加载
 */
export function closeFullScreenLoading() {
  if (loading) {
    loading.close()
  }
}

export default {
  createFullScreenLoading,
  closeFullScreenLoading
}
