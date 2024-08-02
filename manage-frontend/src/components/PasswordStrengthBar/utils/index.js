/**
 * 密码强度检测
 * 密码强度分数计算详见github: https://github.com/EYHN/PasswordQualityCalculator
 * @date 2023-03-29
 */

import PasswordQualityCalculator from './PasswordQualityCalculator'

// [ 可选的 ] 大约 10000 个常用密码的列表, 86kb (gzip 32kb)
import MostPopularPasswords from './MostPopularPasswords'

// 加载常用密码列表
PasswordQualityCalculator.PopularPasswords.load(MostPopularPasswords)

export function checkPasswordFormat(str) {
  const returnMap = (valid, message) => { return { valid: valid, message: message } }
  if (!str) { return returnMap(false, '密码不能为空') }
  const specialCharSet = new Set([
    '!', '@', '#', '$', '%', '^', '&', '*', '_', '-', '+', '=', '?', ',', '.'
  ])
  let numberCount = 0 // 数字
  let lowerCaseCount = 0 // 小写字母
  let upperCaseCount = 0 // 大写字母
  for (let i = 0; i < str.length; i++) {
    const c = str.charCodeAt(i)
    if (c >= 48 && c <= 57) {
      numberCount++
    } else if (c >= 65 && c <= 90) {
      upperCaseCount++
    } else if (c >= 97 && c <= 122) {
      lowerCaseCount++
    } else {
      return returnMap(false, `密码只能包含字母、数字和以下特殊字符：${Array.from(specialCharSet).join('')}`)
    }
  }
  if (str.length < 8 || str.length > 20) {
    return returnMap(false, '密码长度必须在8-20位之间')
  }
  if (numberCount === 0) {
    return returnMap(false, '密码必须包含数字')
  }
  if (lowerCaseCount === 0 && upperCaseCount === 0) {
    // return returnMap(false, '密码必须包含大小写字母')
    return returnMap(false, '密码必须包含字母')
  }
  // if (specialCharCount === 0) {
  //   return returnMap(false, `密码必须至少包含以下特殊字符之一：${Array.from(specialCharSet).join('')}`)
  // }
  return returnMap(true, '密码格式正确')
}

export function getPasswordScore(str) {
  return PasswordQualityCalculator.calculate(str)
}

export function getPasswordStrength(str) {
  const score = PasswordQualityCalculator(str)
  if (score < 24) {
    return { score: score, strength: 0, strengthMessage: '极弱' }
  } else if (score < 32) {
    return { score: score, strength: 1, strengthMessage: '弱' }
  } else if (score < 48) {
    return { score: score, strength: 2, strengthMessage: '中' }
  } else {
    return { score: score, strength: 3, strengthMessage: '强' }
  }
}

export default {
  checkPasswordFormat,
  getPasswordScore,
  getPasswordStrength
}

