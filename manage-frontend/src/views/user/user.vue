<template>
  <div class="app-container">

    <!-- 用户搜索表单 -->
    <el-form :model="tableData" size="small" :inline="true" label-width="68px">
      <el-form-item label="用户名称">
        <el-input
          v-model="tableData.userName"
          placeholder="请输入用户名称"
          clearable
          @keyup.enter.native="getUserList"
        />
      </el-form-item>
      <el-form-item label="创建时间">
        <el-date-picker
          v-model="tableData.minCreateTime"
          class="date-picker"
          value-format="yyyy-MM-dd HH:mm:ss"
          type="datetime"
          placeholder="起始日期"
        />
        <el-date-picker
          v-model="tableData.maxCreateTime"
          class="date-picker"
          value-format="yyyy-MM-dd HH:mm:ss"
          type="datetime"
          placeholder="截止日期"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="getUserList">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 用户操作按钮 -->
    <div>
      <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleCreateUser"> 新增 </el-button>
      <el-button type="danger" plain icon="el-icon-delete" size="mini" @click="handleBatchDelete"> 删除</el-button>
      <el-button type="info" plain icon="el-icon-upload2" size="mini" @click="handleImportUser"> 导入 </el-button>
    </div>

    <!-- 用户列表 -->
    <el-table
      :data="tableData.list"
      @selection-change="val => tableData.selection = val"
      @sort-change="handleSortChange"
    >
      <el-table-column type="index" width="60" />
      <el-table-column type="selection" width="50" />
      <el-table-column width="50">
        <template slot-scope="scope">
          <!-- <img class="table-avatar" :src="scope.row.avatar" /> -->
          <!-- 直接使用:src绑定会导致刷新失败 -->
          <img :id="'avatar-' + scope.row.id" class="table-avatar">
        </template>
      </el-table-column>
      <el-table-column prop="userName" label="用户名" sortable="custom" />
      <el-table-column prop="trueName" label="真实姓名" sortable="custom" />
      <el-table-column prop="roleList" label="角色" sortable="custom" />
      <el-table-column prop="createTime" label="创建时间" sortable="custom" />
      <el-table-column prop="status" label="是否激活" sortable="custom" width="100">
        <template slot-scope="scope">
          <el-switch v-model="scope.row.status" :active-value="1" :inactive-value="0" @change="handleSwitch(scope.row)" />
        </template>
      </el-table-column>
      <el-table-column label="操作">
        <template slot-scope="scope">
          <el-button type="text" size="small" icon="el-icon-edit" @click="handleEdit(scope.row)">
            编辑 </el-button>
          <el-button type="text" size="small" icon="el-icon-delete" style="color: red;" @click="handleDelete([scope.row.id])">
            删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <el-pagination
      class="pagination"
      :current-page.sync="tableData.pageNum"
      :page-sizes="[10, 20, 30, 40]"
      :page-size.sync="tableData.pageSize"
      layout="total, sizes, prev, pager, next, jumper"
      :total="tableData.total"
      @size-change="getUserList"
      @current-change="getUserList"
    />

    <!-- 用户编辑/创建窗口 -->
    <el-dialog class="user-edit-dialog" :title="userEditForm.id ? '用户编辑' : '新增用户'" :visible.sync="userEditDialogVisible" width="50%" top="8vh">
      <el-form
        ref="userEditForm"
        status-icon
        :model="userEditForm"
        label-width="80px"
        :rules="userEditForm.id ? userUpdateRules : userCreateRules"
      >
        <el-form-item label="用户名" prop="userName">
          <el-input v-model="userEditForm.userName" />
        </el-form-item>
        <el-form-item label="真实姓名">
          <el-input v-model="userEditForm.trueName" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="userEditForm.password" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="userEditForm.email" />
        </el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="userEditForm.gender">
            <el-radio :label="0">男</el-radio>
            <el-radio :label="1">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="地址">
          <el-input v-model="userEditForm.address" />
        </el-form-item>
        <el-form-item label="简介">
          <el-input v-model="userEditForm.introduction" />
        </el-form-item>
        <el-form-item label="电话">
          <el-input v-model="userEditForm.phone" />
        </el-form-item>
        <el-form-item label="角色" prop="roleIds">
          <el-select v-model="userEditForm.roleIds" multiple placeholder="请选择角色">
            <el-option v-for="role in allRoles" :key="role.id" :label="role.name" :value="role.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="头像">
          <el-upload
            class="avatar-uploader"
            action=""
            :auto-upload="false"
            :show-file-list="false"
            :on-change="file => handleAvatarChange(file)"
          >
            <img v-if="avatarUploadData.url" :src="avatarUploadData.url" class="avatar">
            <i v-else class="el-icon-plus avatar-uploader-icon" />
          </el-upload>
          <el-button v-if="avatarUploadData.raw" size="mini" @click="resetUploadData(false)">重置</el-button>
        </el-form-item></el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="userEditDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="addOrUpdateUser">确 定</el-button>
      </span>
    </el-dialog>

    <!-- 用户导入弹窗 -->
    <user-import-dialog ref="userImportDialog" @import-success="getUserList" />

  </div>
</template>

<script>
import md5 from 'js-md5'
import * as UserApi from '@/api/user'
import { getRoles } from '@/api/role'
import { checkUserName } from '@/api/auth'
import emptyAvatar from '@/assets/images/empty_avatar.jpg'
import LoadingUtils from '@/utils/loading-utils'
import UserImportDialog from './components/UserImportDialog.vue'

const copyObject = obj => JSON.parse(JSON.stringify(obj))

export default {
  name: 'User',
  components: {
    UserImportDialog
  },
  data() {
    return {
      tableData: {
        list: [],
        total: 0,
        pageNum: 1,
        pageSize: 10,
        userName: '',
        minCreateTime: '',
        maxCreateTime: '',
        orderBy: '',
        orderMethod: 'asc',
        selection: []
      },
      userEditForm: {
        id: '',
        userName: '',
        trueName: '',
        password: '',
        email: '',
        gender: '',
        address: '',
        introduction: '',
        phone: '',
        roleIds: []
      },
      userCreateRules: {
        userName: [{ required: true, trigger: 'blur', validator: this.userNameValidator }],
        password: [{ required: true, trigger: 'change', validator: this.passwordValidator }],
        roleIds: [{ required: true, trigger: 'change', validator: this.roleValidator }]
      },
      userUpdateRules: {
        userName: [{ required: true, trigger: 'blur', validator: this.userNameValidator }],
        password: [{ trigger: 'change', validator: this.passwordValidator }],
        roleIds: [{ required: true, trigger: 'change', validator: this.roleValidator }]
      },
      currentEditRow: {},
      allRoles: [],
      userEditDialogVisible: false,
      avatarMap: {}, // 缓存头像, userId -> blobUrl
      avatarUploadData: {
        raw: null,
        url: ''
      }
    }
  },
  mounted() {
    this.getAllRoles()
    this.getUserList()
  },
  methods: {
    userNameValidator(rule, value, callback) {
      if (!value) {
        callback(new Error('请输入用户名'))
      } else if (this.userEditForm.id && value === this.currentEditRow.userName) {
        callback()
      } else {
        checkUserName(value).then(res => {
          callback(res.data.data ? new Error('用户名已存在') : undefined)
        })
      }
    },
    passwordValidator(rule, value, callback) {
      if (!value && this.userEditForm.id) {
        callback()
      } else if (!value || value.length < 6) {
        callback(new Error('密码长度不能小于6位'))
      } else {
        callback()
      }
    },
    roleValidator(rule, value, callback) {
      if (!value || value.length === 0) {
        callback(new Error('角色不能为空'))
      } else {
        callback()
      }
    },

    /**
     * 获取所有角色
     */
    getAllRoles() {
      getRoles().then(res => {
        this.allRoles = res.data.data
        this.$refs.userImportDialog.setRoles(this.allRoles)
      })
    },

    /**
     * 获取用户列表
     */
    getUserList() {
      UserApi.getUsers(this.tableData).then(res => {
        this.tableData.list = res.data.data.content
        this.tableData.total = res.data.data.totalElements
        this.$nextTick(() => {
          this.tableData.list.forEach(row => {
            this.getAvatar(row.id, row)
          })
        })
      })
    },

    /**
     * 获取用户头像
     * @param {number} userId 用户ID
     * @param {object} row 行数据
     */
    getAvatar(userId, row) {
      const avatarObj = document.getElementById('avatar-' + userId)
      if (this.avatarMap[userId]) {
        row.avatar = this.avatarMap[userId]
        avatarObj.src = this.avatarMap[userId]
      } else {
        UserApi.getUserAvatar(userId).then(blobUrl => {
          this.avatarMap[userId] = blobUrl
          row.avatar = blobUrl
        }).catch(() => {
          this.avatarMap[userId] = emptyAvatar
          row.avatar = emptyAvatar
        }).finally(() => {
          avatarObj.src = row.avatar
        })
      }
    },

    /**
     * 更新用户头像
     */
    updateAvatar() {
      if (!this.avatarUploadData.raw) {
        return
      }
      this.avatarMap[this.userEditForm.id] = this.avatarUploadData.url
      UserApi.updateUserAvatar(this.userEditForm.id, this.avatarUploadData.raw).catch(() => {
        this.avatarMap[this.userEditForm.id] = emptyAvatar
      }).finally(() => {
        this.resetUploadData()
      })
    },

    /**
     * 重置查询条件
     */
    resetQuery() {
      this.tableData.userName = ''
      this.tableData.minCreateTime = ''
      this.tableData.maxCreateTime = ''
    },

    /**
     * 编辑用户
     * @param {object} row 行数据
     */
    handleEdit(row) {
      this.currentEditRow = row
      for (const key in this.userEditForm) {
        this.userEditForm[key] = row[key]
      }
      this.userEditForm.roleIds = row.roleList ? row.roleList.map(item => {
        const role = this.allRoles.find(role => role.name === item)
        return role && role.id
      }) : []
      this.userEditForm.roleIds.filter(id => id)
      this.openUserEditForm()
      this.resetUploadData()
      if (row.avatar && row.avatar !== emptyAvatar) {
        this.avatarUploadData.url = row.avatar
      }
    },

    /**
     * 重置用户编辑表单
     */
    resetUserEditForm() {
      for (const key in this.userEditForm) {
        this.userEditForm[key] = ''
      }
      this.userEditForm.roleIds = []
    },

    /**
     * 删除用户
     * @param {array} userIds 用户ID数组
     */
    handleDelete(userIds) {
      this.$confirm('此操作将永久删除该用户, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        LoadingUtils.createFullScreenLoading('正在删除...')
        UserApi.deleteUsers(userIds).then(() => {
          this.$message.success('删除成功')
          this.getUserList()
        }).finally(() => {
          LoadingUtils.closeFullScreenLoading()
        })
      })
    },

    /**
     * 切换用户状态
     * @param {object} row 行数据
     */
    handleSwitch(row) {
      UserApi.changeUserStatus(row.id, row.status).then(() => {
        this.$message.success('操作成功')
      })
    },

    /**
     * 添加或更新用户
     */
    addOrUpdateUser() {
      this.$refs.userEditForm.validate(valid => {
        if (valid) {
          const params = copyObject(this.userEditForm)
          if (!params.password) {
            delete params.password
          } else {
            params.password = md5(params.password)
          }
          LoadingUtils.createFullScreenLoading('正在保存...')
          const tempApi = this.userEditForm.id ? UserApi.updateUser : UserApi.addUser
          tempApi(params).then(res => {
            this.$message.success('操作成功')
            if (!this.userEditForm.id) {
              this.userEditForm.id = res.data.data.id
            }
            this.updateAvatar()
            this.getUserList()
          }).finally(() => {
            this.userEditDialogVisible = false
            LoadingUtils.closeFullScreenLoading()
          })
        }
      })
    },

    /**
     * 创建用户
     */
    handleCreateUser() {
      this.resetUserEditForm()
      this.resetUploadData()
      this.openUserEditForm()
    },

    /**
     * 批量删除用户
     */
    handleBatchDelete() {
      if (this.tableData.selection.length === 0) {
        this.$message.warning('请选择要删除的用户')
        return
      }
      const userIds = this.tableData.selection.map(item => item.id)
      this.handleDelete(userIds)
    },

    /**
     * 处理排序变化
     * @param {object} column 列对象
     * @param {string} prop 列属性
     * @param {string} order 排序方式
     */
    handleSortChange({ column, prop, order }) {
      this.tableData.orderBy = prop
      this.tableData.orderMethod = order === 'ascending' ? 'asc' : 'desc'
      this.getUserList()
    },

    /**
     * 处理头像变化
     * @param {object} file 文件对象
     */
    handleAvatarChange(file) {
      this.avatarUploadData.raw = file.raw
      this.avatarUploadData.url = URL.createObjectURL(file.raw)
    },

    /**
     * 重置上传数据
     * @param {boolean} clear 是否清空数据
     */
    resetUploadData(clear = true) {
      this.avatarUploadData.raw = null
      this.avatarUploadData.url = clear ? '' : this.avatarMap[this.userEditForm.id] || ''
    },

    /**
     * 打开用户编辑窗口
     */
    openUserEditForm() {
      this.userEditDialogVisible = true
      this.$nextTick(() => {
        this.$refs.userEditForm.clearValidate()
      })
    },

    /**
     * 打开用户导入窗口
     */
    handleImportUser() {
      this.$refs.userImportDialog.show(true)
    }
  }
}
</script>

<style scoped>
.pagination {
  text-align: center;
}
.date-picker {
  width: 160px;
  margin-right: 10px;
}
.table-avatar {
  width: 30px;
  height: 30px;
  border-radius: 50%;
}
.avatar-uploader {
  display: inline-block;
  margin-right: 10px;
  vertical-align: middle;
}
.avatar-uploader img {
  max-height: 100px;
}
</style>
