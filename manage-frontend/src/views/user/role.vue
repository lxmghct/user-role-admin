<template>
  <div class="app-container">
    <!-- 角色搜索表单 -->
    <el-form :model="tableData" size="small" :inline="true" label-width="68px">
      <el-form-item label="搜索内容">
        <el-input
          v-model="tableData.searchContent"
          placeholder="请输入搜索内容"
          clearable
          @keyup.enter.native="getRoleList"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="getRoleList"> 搜索 </el-button>
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleCreateRole"> 新增 </el-button>
      </el-form-item>
    </el-form>

    <!-- 角色列表 -->
    <el-table
      :data="tableData.list"
      @sort-change="handleSortChange"
    >
      <el-table-column type="index" width="60" />
      <el-table-column prop="name" label="角色名称" sortable="custom" />
      <el-table-column prop="description" label="角色描述" sortable="custom" />
      <el-table-column prop="createTime" label="创建时间" sortable="custom" />
      <el-table-column prop="updateTime" label="更新时间" sortable="custom" />
      <el-table-column label="操作">
        <template slot-scope="scope">
          <el-button type="text" size="small" icon="el-icon-edit" @click="handleEdit(scope.row)">
            编辑 </el-button>
          <el-button type="text" size="small" icon="el-icon-delete" style="color: red;" @click="handleDelete(scope.row.id)">
            删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <el-pagination
      class="pagination"
      :current-page.sync="tableData.currentPage"
      :page-sizes="[10, 20, 30, 40]"
      :page-size.sync="tableData.pageSize"
      layout="total, sizes, prev, pager, next, jumper"
      :total="tableData.total"
      @size-change="startPagination"
      @current-change="startPagination"
    />

    <!-- 角色编辑/创建窗口 -->
    <el-dialog :title="roleEditForm.id ? '新增角色' : '角色编辑'" :visible.sync="roleEditDialogVisible" width="50%">
      <el-form ref="roleEditForm" :model="roleEditForm" :rules="roleEditFormRules" label-width="80px">
        <el-form-item label="角色名称" prop="name">
          <el-input v-model="roleEditForm.name" />
        </el-form-item>
        <el-form-item label="角色描述" prop="description">
          <el-input v-model="roleEditForm.description" />
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="roleEditDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="addOrUpdateRole">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import * as RoleApi from '@/api/role'
export default {
  name: 'Role',
  data() {
    return {
      tableData: {
        allData: [],
        list: [],
        total: 0,
        currentPage: 1,
        pageSize: 10,
        searchContent: '',
        selection: []
      },
      roleEditForm: {
        id: '',
        name: '',
        description: ''
      },
      roleEditFormRules: {
        name: [
          { required: true, trigger: 'blur', validator: this.validateName }
        ]
      },
      roleEditDialogVisible: false
    }
  },
  mounted() {
    this.getRoleList()
  },
  methods: {
    validateName(rule, value, callback) {
      if (!value) {
        callback(new Error('请输入角色名称'))
      } else {
        if (this.roleEditForm.id && this.roleEditForm.name === value) {
          callback()
        } else {
          RoleApi.checkRoleName(value).then(res => {
            callback(res.data.data ? new Error('角色名称已存在') : undefined)
          })
        }
      }
    },

    /**
     * 获取角色列表
     */
    getRoleList() {
      RoleApi.getRoles(this.tableData.searchContent).then(res => {
        this.tableData.allData = res.data.data
        this.tableData.total = res.data.data.length
        this.startPagination()
      })
    },

    /**
     * 前端分页
     */
    startPagination() {
      // 角色数量较少，所以采用前端分页
      const start = (this.tableData.currentPage - 1) * this.tableData.pageSize
      const end = this.tableData.currentPage * this.tableData.pageSize
      this.tableData.list = this.tableData.allData.slice(start, end)
    },

    /**
     * 排序
     * @param {Object} { prop, order }
     */
    handleSortChange({ prop, order }) {
      const timeComparator = (a, b) => {
        return new Date(a).getTime() - new Date(b).getTime()
      }
      const stringComparator = (a, b) => {
        return a.localeCompare(b)
      }
      const sort = (a, b, prop, comparator) => {
        const result = comparator(a[prop], b[prop])
        return order === 'ascending' ? result : -result
      }
      switch (prop) {
        case 'name':
        case 'description':
          this.tableData.allData.sort((a, b) => sort(a, b, prop, stringComparator))
          break
        case 'createTime':
        case 'updateTime':
          this.tableData.allData.sort((a, b) => sort(a, b, prop, timeComparator))
          break
      }
      this.startPagination()
    },

    /**
     * 新增角色
     */
    handleCreateRole() {
      for (const key in this.roleEditForm) {
        this.roleEditForm[key] = ''
      }
      this.openRoleEditDialog()
    },

    /**
     * 编辑角色
     * @param {Object} row
     */
    handleEdit(row) {
      for (const key in this.roleEditForm) {
        this.roleEditForm[key] = row[key]
      }
      this.openRoleEditDialog()
    },

    /**
     * 删除角色
     * @param {Number} id
     */
    handleDelete(id) {
      this.$confirm('此操作将永久删除该角色, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        RoleApi.deleteRoles([id]).then(() => {
          this.$message.success('删除成功')
          this.getRoleList()
        })
      }).catch(() => {
        this.$message.info('已取消删除')
      })
    },

    /**
     * 新增/编辑角色(后端请求)
     */
    addOrUpdateRole() {
      this.$refs.roleEditForm.validate(valid => {
        if (valid) {
          const tempApi = this.roleEditForm.id ? RoleApi.updateRole : RoleApi.addRole
          tempApi(this.roleEditForm).then(res => {
            this.$message.success('操作成功')
            this.getRoleList()
            this.roleEditDialogVisible = false
          })
        }
      })
    },

    /**
     * 打开角色编辑窗口
     */
    openRoleEditDialog() {
      this.roleEditDialogVisible = true
      this.$nextTick(() => {
        this.$refs.roleEditForm.clearValidate()
      })
    }
  }
}
</script>

<style scoped>
</style>
