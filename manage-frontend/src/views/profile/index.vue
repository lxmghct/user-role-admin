<template>
  <div class="profile-main">
    <div class="user-information">
      <el-upload
        action="#"
        :http-request="uploadAvatar"
        :headers="{ Authorization: token }"
        name="avatar"
        :show-file-list="false"
        :on-success="onAvatarUploadSuccess"
        :before-upload="beforeAvatarUpload"
        accept=".jpg,.jpeg,.png,.gif"
      >
        <img :src="avatarUrl" class="avatar">
      </el-upload>
      <el-form label-width="100px">
        <el-form-item label="用户名">{{ userInfo.userName }}</el-form-item>
        <el-form-item label="真实姓名">{{ userInfo.trueName }}</el-form-item>
        <el-form-item label="地址">{{ userInfo.address }}</el-form-item>
        <el-form-item label="电话">{{ userInfo.phone }}</el-form-item>
        <el-form-item label="邮箱">{{ userInfo.email }}</el-form-item>
        <el-form-item label="性别">{{ userInfo.gender === 1 ? '男' : (userInfo.gender === 0 ? '女' : '') }}</el-form-item>
        <el-form-item label="简介">{{ userInfo.introduction }}</el-form-item>
        <el-form-item label="注册时间">{{ userInfo.createTime }}</el-form-item>
      </el-form>
      <div class="button-group">
        <el-button
          type="primary"
          @click="openUpdateUserInformationDialog"
        >修改信息</el-button>
        <el-button
          type="danger"
          @click="$refs.passwordUpdateDialog.show()"
        >修改密码</el-button>
      </div>
    </div>
    <el-dialog
      title="修改信息"
      :visible.sync="updateUserInformationDialogVisible"
      width="60%"
    >
      <el-form
        ref="userInformationUpdateForm"
        :model="userUpdateForm"
        :rules="rules"
      >
        <el-form-item label="地址">
          <el-input v-model="userUpdateForm.address" />
        </el-form-item>
        <el-form-item label="电话">
          <el-input v-model="userUpdateForm.phone" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="userUpdateForm.email" />
        </el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="userUpdateForm.gender">
            <el-radio label="1">男</el-radio>
            <el-radio label="0">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="简介">
          <el-input v-model="userUpdateForm.introduction" />
        </el-form-item>
      </el-form>
      <span
        slot="footer"
        class="dialog-footer"
      >
        <el-button @click="cancelUpdateUserInformation()">取 消</el-button>
        <el-button
          type="primary"
          @click="updateUserInformation()"
        >确 定</el-button>
      </span>
    </el-dialog>
    <password-update-dialog ref="passwordUpdateDialog" />
  </div>
</template>

<script>
import { getToken } from '@/utils/auth'
import { getInfo, updateUserSelfInfo, updateAvatar } from '@/api/user'
import PasswordUpdateDialog from '@/components/PasswordUpdateDialog'

export default {
  name: 'UserIndex',
  components: {
    PasswordUpdateDialog
  },
  data() {
    return {
      token: getToken(),
      userInfo: {},
      userUpdateForm: {
        address: '',
        phone: '',
        email: '',
        gender: null,
        introduction: ''
      },
      avatarUrl: '',
      updateUserInformationDialogVisible: false,
      rules: {
        userName: [
          { required: true, message: '请输入用户名', trigger: 'blur' }
        ]
      }
    }
  },
  mounted() {
    this.getAvatar()
    this.getUserInfo()
  },
  methods: {

    /**
     * 修改用户信息
     */
    updateUserInformation() {
      this.$refs['userInformationUpdateForm'].validate((valid) => {
        if (valid) {
          updateUserSelfInfo(this.userUpdateForm).then(() => {
            this.$message.success('修改成功')
            this.getUserInfo()
            this.updateUserInformationDialogVisible = false
          })
        }
      })
    },

    /**
     * 取消修改用户信息
     */
    cancelUpdateUserInformation() {
      this.updateUserInformationDialogVisible = false
    },

    /**
     * 打开修改用户信息对话框
     */
    openUpdateUserInformationDialog() {
      this.updateUserInformationDialogVisible = true
      this.userUpdateForm = JSON.parse(JSON.stringify(this.userInfo))
    },

    /**
     * 获取头像
     */
    getAvatar() {
      this.$store.dispatch('user/getAvatar').then((res) => {
        this.avatarUrl = res
      })
    },

    /**
     * 头像上传成功回调
     */
    onAvatarUploadSuccess() {
      this.$message.success('上传成功')
      this.getAvatar()
    },

    /**
     * 头像上传前校验
     * @param {File} file
     */
    beforeAvatarUpload(file) {
      const allowType = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif']
      const typeCheck = allowType.includes(file.type)
      const isLt2M = file.size / 1024 / 1024 <= 2
      if (!typeCheck) {
        this.$message.error('上传头像图片只能是 jpg, jpeg, png, gif 格式!')
      }
      if (!isLt2M) {
        this.$message.error('上传头像图片大小不能超过 2MB!')
      }
      return typeCheck && isLt2M
    },

    /**
     * 获取用户信息
     */
    getUserInfo() {
      getInfo().then((res) => {
        this.userInfo = res.data.data
      })
    },

    /**
     * 上传头像
     * @param {Object} { file }
     */
    uploadAvatar({ file }) {
      return updateAvatar(file)
    }

  }
}
</script>

<style scoped>
.profile-main {
  padding: 0 100px;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}

.avatar {
  border-radius: 50%;
  width: 150px;
  height: 150px;
  border: 1px #000 solid;
  margin-right: 20px;
}

.user-information {
  display: flex;
  align-items: center;
  border-bottom: 1px solid #dcdfe6;
  padding: 20px 0px;
}

.button-group {
  padding-left: 100px;
  display: flex;
  flex-direction: column;
}

.button-group,
.el-button {
  margin: 10px;
}

.el-form >>> .el-form-item__label {
  font-size: 16px;
}
</style>

