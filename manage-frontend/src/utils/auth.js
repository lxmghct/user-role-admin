// import Cookies from 'js-cookie'

const tokenKey = 'Admin-Token'

export function getToken() {
  // return Cookies.get(tokenKey)
  return localStorage.getItem(tokenKey)
}

export function setToken(token) {
  // return Cookies.set(tokenKey, token)
  localStorage.setItem(tokenKey, token)
}

export function removeToken() {
  // return Cookies.remove(tokenKey)
  localStorage.removeItem(tokenKey)
}
