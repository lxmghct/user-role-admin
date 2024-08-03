import Vue from 'vue'

import Cookies from 'js-cookie'

import 'normalize.css/normalize.css' // a modern alternative to CSS resets

import Element from 'element-ui'
import './styles/element-variables.scss'

import '@/styles/index.scss' // global css

import App from './App'
import store from './store'
import router from './router'

import './icons' // icon
import './permission' // permission control

import axios from './utils/request'
import permission from '@/directive/permission'

Vue.use(Element, {
  size: Cookies.get('size') || 'medium' // set element-ui default size
})

Vue.config.productionTip = false

Vue.prototype.$http = axios
Vue.directive('permission', permission)

new Vue({
  el: '#app',
  router,
  store,
  render: h => h(App)
})
