<template>
  <div v-if="passwordStrength > 0" class="password-strength">
    <div class="label">密码强度</div>
    <div class="password-strength-container">
      <!-- 1: 绿色 2: 蓝色 3: 橙色 -->
      <div
        class="password-strength-bar"
        :style="passwordStrength > 0 ? 'background-color: #13ce66' : ''"
      > 弱 </div>
      <div
        class="password-strength-bar"
        :style="passwordStrength > 1 ? 'background-color: #409eff' : ''"
      > 中 </div>
      <div
        class="password-strength-bar"
        :style="passwordStrength > 2 ? 'background-color: #f7ba2a' : ''"
      > 强 </div>
    </div>
  </div>
</template>

<script>
import PasswordUtils from './utils'
export default {
  props: {
    password: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      passwordStrength: 0,
      message: ''
    }
  },
  watch: {
    password: {
      handler() {
        this.checkPassword()
      },
      immediate: true
    }
  },
  methods: {
    getCheckPasswordInfo() {
      this.checkPassword()
      return {
        valid: this.passwordStrength > 0,
        message: this.message
      }
    },
    checkPassword() {
      const checkData = PasswordUtils.checkPasswordFormat(this.password)
      if (!checkData.valid) {
        this.passwordStrength = 0
        this.message = checkData.message
      } else {
        this.passwordStrength = PasswordUtils.getPasswordStrength(this.password).strength
        this.message = this.passwordStrength > 0 ? '' : '密码过于简单'
      }
    }
  }

}
</script>

<style scoped>
.password-strength {
  display: flex;
  flex-direction: row;
  padding: 0 50px;
  align-items: center;
}
.password-strength .label {
  font-size: 15px;
}
.password-strength-container {
  display: flex;
  flex-direction: row;
  margin-left: 10px;
  flex: 1;
}
.password-strength-bar {
  margin-right: 5px;
  border-radius: 13px;
  background-color: #dadada;
  width: 100%;
  color: #fff;
  font-size: 11px;
  line-height: 20px;
  text-align: center;
}
</style>
