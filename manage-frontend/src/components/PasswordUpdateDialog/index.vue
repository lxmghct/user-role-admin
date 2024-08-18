<template>
  <el-dialog
    title="修改密码"
    :visible.sync="updatePasswordDialogVisible"
    width="60%"
  >
    <el-form
      ref="passwordUpdateForm"
      :model="passwordUpdateForm"
      :rules="passwordUpdateRules"
    >
      <el-form-item label="原密码" prop="oldPassword">
        <el-input v-model="passwordUpdateForm.oldPassword" type="password" autocomplete="off" />
      </el-form-item>
      <el-form-item label="新密码" prop="newPassword">
        <el-input v-model="passwordUpdateForm.newPassword" type="password" autocomplete="off" />
        <password-strength-bar ref="passwordStrengthBar" :password="passwordUpdateForm.newPassword" />
      </el-form-item>
      <el-form-item label="确认新密码" prop="newPasswordCheck">
        <el-input v-model="passwordUpdateForm.newPasswordCheck" type="password" autocomplete="off" />
      </el-form-item>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="cancelUpdatePassword()">取 消</el-button>
      <el-button
        :disabled="isSubmitting"
        type="primary"
        @click="updatePassword()"
      >
        <i v-if="isSubmitting" class="el-icon-loading" />
        确 定
      </el-button>
    </span>
  </el-dialog>
</template>

<script>
import PasswordStrengthBar from '@/components/PasswordStrengthBar'
import { changePassword } from '@/api/user'

export default {
  name: 'UserIndex',
  components: {
    PasswordStrengthBar
  },
  data() {
    var validateNewPassword = (rule, value, callback) => {
      const checkData = this.$refs.passwordStrengthBar.getCheckPasswordInfo()
      if (!checkData.valid) {
        callback(new Error(checkData.message))
      } else {
        if (this.passwordUpdateForm.oldPassword === value) {
          callback(new Error('新密码不能与原密码相同'))
        } else {
          callback()
        }
      }
    }
    var validateNewPasswordCheck = (rule, value, callback) => {
      if (value.length < 8) {
        callback(new Error('请输入大于等于8位的密码'))
      } else if (value !== this.passwordUpdateForm.newPassword) {
        callback(new Error('两次输入密码不一致!'))
      } else {
        callback()
      }
    }
    return {
      updatePasswordDialogVisible: false,
      passwordUpdateForm: {
        oldPassword: '',
        newPassword: '',
        newPasswordCheck: '',
        passwordStrength: 0
      },
      isSubmitting: false,
      passwordUpdateRules: {
        oldPassword: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 6, message: '长度大于等于6个字符', trigger: 'change' }
        ],
        newPassword: [{ required: true, validator: validateNewPassword, trigger: 'change' }],
        newPasswordCheck: [
          { required: true, validator: validateNewPasswordCheck, trigger: 'blur' }
        ]
      }
    }
  },
  mounted() {
  },
  methods: {
    show() {
      this.updatePasswordDialogVisible = true
    },
    updatePassword() {
      this.$refs['passwordUpdateForm'].validate((valid) => {
        if (valid) {
          this.isSubmitting = true
          changePassword(this.passwordUpdateForm.oldPassword, this.passwordUpdateForm.newPassword).then(() => {
            this.$message.success('密码修改成功')
            this.cancelUpdatePassword()
          }).finally(() => {
            this.isSubmitting = false
          })
        }
      })
    },
    cancelUpdatePassword() {
      this.passwordUpdateForm = {
        oldPassword: '',
        newPassword: '',
        newPasswordCheck: ''
      }
      this.updatePasswordDialogVisible = false
    }
  }
}
</script>

<style scoped>

.password-strength {
  display: flex;
  flex-direction: row;
  padding: 0 50px;
}
.password-strength-container {
  display: flex;
  flex-direction: row;
  margin-top: 10px;
  margin-left: 10px;
  flex: 1;
}
.password-strength-bar {
  height: 26px;
  margin-right: 5px;
  border-radius: 13px;
  background-color: #dadada;
  width: 100%;
  color: #fff;
  font-size: 11px;
  line-height: 26px;
}

</style>
