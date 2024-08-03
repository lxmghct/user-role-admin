<template>
  <div class="app-container">
    <el-select
      v-model="roleForm.currentRoleName"
      filterable
      placeholder="请选择"
      remote
      :remote-method="query => {roleForm.searchName = query; getRoles()}"
      @change="handleCurrentRoleChange"
    >
      <el-option
        v-for="item in roleForm.roleList"
        :key="item.id"
        :label="item.name"
        :value="item.name"
      />
    </el-select>
    <!--重置、保存-->
    <el-button type="primary" style="margin-left: 50px" @click="resetPermission">重置</el-button>
    <el-button type="primary" @click="savePermission">保存</el-button>
    <div style="height: 20px" />
    <el-table
      :data="permissionTree"
      style="margin-bottom: 20px;"
      row-key="id"
      border
      default-expand-all
      :tree-props="{children: 'children', hasChildren: 'hasChildren'}"
    >
      <el-table-column label="权限名称" width="300">
        <template slot-scope="scope">
          <el-checkbox
            v-model="scope.row.checked"
            :indeterminate="scope.row.indeterminate"
            style="margin-left: 5px; margin-right: 10px;"
            @change="handleCheckChange(scope.row)"
          />
          <span v-if="scope.row.classify === 'platform'" style="font-weight: bold;">
            {{ scope.row.name }}</span>
          <span v-else>{{ scope.row.name }}</span>
        </template>
      </el-table-column>
      <el-table-column label="权限代码" prop="code" width="200" />
      <el-table-column label="描述" prop="description" width="300" />
      <el-table-column label="创建时间" prop="createTime" width="200" />
    </el-table>

  </div>
</template>

<script>
import * as RoleApi from '@/api/role'

export default {
  name: 'Permission',
  data() {
    return {
      roleForm: {
        searchName: '',
        roleList: [],
        currentRoleName: '',
        currentRole: {},
        loading: false
      },
      roleList: [],
      permissionTree: [],
      permissionIdList: [],
      platformMap: {
        1: '展示平台',
        2: '管理平台'
      }
    }
  },
  mounted() {
    this.getRoles()
    this.getAllPermission()
  },
  methods: {

    /**
     * 根据列表构建树
     * @param {Array} list 列表
     * @param {String} idField id字段名
     * @param {String} pidField 父id字段名
     */
    buildTreeByList(list, idField = 'id', pidField = 'parentId') {
      const listData = Array.from(list)
      const tree = []
      const idTreeMap = {}
      for (let i = 0; i < listData.length; i++) {
        const item = listData[i]
        if (!item[pidField] || listData.findIndex(it => it[idField] === item[pidField]) === -1) {
          tree.push(item)
          idTreeMap[item[idField]] = item
        }
      }
      // 删除根节点
      for (let i = 0; i < listData.length; i++) {
        if (idTreeMap[listData[i][idField]]) {
          listData.splice(i, 1)
          i--
        }
      }
      let oldLength = listData.length + 1
      while (listData.length > 0) {
        for (let i = 0; i < listData.length; i++) {
          const item = listData[i]
          const parent = idTreeMap[item[pidField]]
          if (parent) {
            if (!parent.children) {
              parent.children = []
            }
            parent.children.push(item)
            idTreeMap[item[idField]] = item
            listData.splice(i, 1)
            i--
          }
        }
        if (oldLength === listData.length) {
          break
        }
        oldLength = listData.length
      }
      return tree
    },

    /**
     * 获取所有权限
     */
    getAllPermission() {
      RoleApi.getAllPermissions().then(res => {
        const data = res.data.data
        data.forEach(item => {
          item.checked = false
          item.indeterminate = false
        })
        const platforms = [...new Set(data.map(item => item.platform))]
        this.permissionTree = []
        for (let i = 0; i < platforms.length; i++) {
          const platform = platforms[i]
          const platformData = data.filter(item => item.platform === platform)
          this.permissionTree.push({ id: -1 - i, name: this.platformMap[platform], classify: 'platform', children: this.buildTreeByList(platformData) })
        }
        if (this.permissionIdList.length > 0) {
          this.changePermissionCheckStatus(this.permissionIdList)
        }
        console.log(this.permissionTree)
      })
    },

    /**
     * 遍历树
     * @param {Array} tree 树
     * @param {Function} callback 回调
     */
    traverseTree(tree, callback) {
      for (let i = 0; i < tree.length; i++) {
        const item = tree[i]
        callback(item)
        if (item.children) {
          this.traverseTree(item.children, callback)
        }
      }
    },

    /**
     * 修改权限树的勾选状态
     * @param {Array} permissionList 权限列表
     */
    changePermissionCheckStatus(permissionList) {
      this.traverseTree(this.permissionTree, item => {
        if (permissionList.includes(item.id)) {
          item.checked = true
        } else {
          item.checked = false
        }
      })
      this.permissionTree.forEach(item => {
        this.modifyNodeCheckStatus(item)
      })
    },

    /**
     * 修改节点的勾选状态
     * @param {Object} node 节点
     */
    modifyNodeCheckStatus(node) {
      const children = node.children
      if (children) {
        node.children.forEach(item => {
          this.modifyNodeCheckStatus(item)
        })
        node.checked = node.check || children.some(item => item.checked)
        node.indeterminate = children.some(item => item.indeterminate) || children.some(item => item.checked) && children.some(item => !item.checked)
      }
    },

    /**
     * 获取角色的权限
     * @param {Number} roleId 角色id
     */
    getPermissionOfRole(roleId) {
      RoleApi.getPermissionOfRole(roleId).then(res => {
        const permissionList = res.data.data
        this.permissionIdList = permissionList.map(item => item.id)
        this.changePermissionCheckStatus(this.permissionIdList)
      })
    },

    /**
     * 重置权限
     */
    resetPermission() {
      this.changePermissionCheckStatus(this.permissionIdList)
    },

    /**
     * 获取角色列表
     */
    getRoles() {
      this.roleForm.loading = true
      RoleApi.getRoles(this.roleForm.searchName).then((res) => {
        this.roleForm.roleList = res.data.data
        this.roleForm.total = res.data.data.length
        // 从页面的params中获取roleId
        const roleId = this.$route.params.roleId
        if (roleId) {
          this.getPermissionOfRole(roleId)
          this.roleForm.currentRole = this.roleForm.roleList.find(item => item.id === roleId)
          this.roleForm.currentRoleName = this.roleForm.currentRole.name
          this.$route.params.roleId = undefined
        }
      }).finally(() => {
        this.roleForm.loading = false
      })
    },

    /**
     * 递归勾选/取消勾选所有子节点
     * @param {Object} node 节点
     * @param {Boolean} checked 是否勾选
     */
    checkAllChildren(node, checked) {
      if (node.children) {
        node.children.forEach(item => {
          item.checked = checked
          this.checkAllChildren(item, checked)
        })
      }
    },

    /**
     * 处理勾选状态改变
     * @param {Object} row 行数据
     */
    handleCheckChange(row) {
      // 将所有与当前节点permissionCode相同的节点勾选/取消勾选
      if (row.code) {
        this.traverseTree(this.permissionTree, item => {
          if (item.code === row.code) {
            item.checked = row.checked
          }
        })
      }
      this.checkAllChildren(row, row.checked)
      this.permissionTree.forEach(item => {
        this.modifyNodeCheckStatus(item)
      })
    },

    /**
     * 处理当前角色改变
     */
    handleCurrentRoleChange() {
      this.roleForm.currentRole = this.roleForm.roleList.find(item => item.name === this.roleForm.currentRoleName)
      this.getPermissionOfRole(this.roleForm.currentRole.id)
    },

    /**
     * 获取权限列表
     * @returns {Array} 权限列表
     */
    getPermissionListOfTree() {
      const permissionList = []
      this.traverseTree(this.permissionTree, item => {
        if (item.checked && item.classify !== 'platform') {
          permissionList.push(item.id)
        }
      })
      return permissionList
    },

    /**
     * 保存权限
     */
    savePermission() {
      if (!this.roleForm.currentRole || !this.roleForm.currentRole.id) {
        this.$message({
          message: '请选择角色',
          type: 'warning'
        })
        return
      }
      RoleApi.updatePermissionOfRole(this.roleForm.currentRole.id, this.getPermissionListOfTree()).then(res => {
        if (res.data.status === 200) {
          this.$message({
            message: '修改成功',
            type: 'success'
          })
          this.getPermissionOfRole(this.roleForm.currentRole.id)
        } else {
          this.$message({
            message: '修改失败',
            type: 'error'
          })
        }
      })
    }

  }
}
</script>

<style scoped>
</style>
