<template>
  <el-dialog class="user-import-dialog" title="导入用户" :visible.sync="dialogVisible" width="80%">
    <el-button type="primary" icon="el-icon-download" size="small" @click="downloadImportExample"> 下载示例文件 </el-button>
    <el-upload
      class="upload-demo"
      action=""
      accept=".xlsx, .xls"
      :auto-upload="false"
      :on-change="handleFileChange"
      :show-file-list="false"
    >
      <el-button size="small" type="primary">点击上传 excel</el-button>
    </el-upload>
    <!-- 文件名 -->
    <div v-if="importFile" class="file-name">{{ importFile.name }}</div>
    <el-table
      id="user-import-preview-table"
      ref="table"
      :data="excelPreviewData"
      :cell-class-name="getCellClass"
      border
      max-height="500"
      @cell-mouse-enter="handleCellMouseEnter"
      @cell-dblclick="handleCellDblClick"
      @selection-change="handleSelectionChange"
    >
      <!-- selection -->
      <el-table-column type="selection" width="40" fixed="left" />
      <!-- index -->
      <el-table-column label="序号" width="55" fixed="left">
        <template slot-scope="scope">
          {{ scope.$index + 1 }}
          <el-popover placement="top" width="200" trigger="hover">
            <div v-for="(error, index) in scope.row.errors" :key="index">{{ error }}</div>
            <i v-if="scope.row.errors.length > 0" slot="reference" class="el-icon-warning error" />
          </el-popover>
        </template>
      </el-table-column>
      <!-- data -->
      <el-table-column
        v-for="(key, index) in allKeys"
        :key="key"
        :label="key"
        :prop="key"
        :width="columnWidths[index]"
        resizable
      >
        <template slot-scope="{ row }">
          <!-- 编辑模式 -->
          <template v-if="row[key].edit">
            <template v-if="key === '性别'">
              <el-radio-group v-model="row[key].value" size="small" @change="handleInputChange(row, key)">
                <el-radio label="男">男</el-radio>
                <el-radio label="女">女</el-radio>
              </el-radio-group>
            </template>
            <template v-else-if="key === '角色'">
              <el-select v-model="row[key].value" multiple placeholder="请选择角色" value-key="id" size="small" @change="handleInputChange(row, key)">
                <el-option v-for="role in allRoles" :key="role.id" :label="role.name" :value="role" />
              </el-select>
            </template>
            <template v-else>
              <div class="edit-input-container" :class="{ 'is-error': row[key].error }">
                <el-input
                  v-model="row[key].value"
                  size="small"
                  @input="handleInputChange(row, key)"
                  @keydown.enter.native="handleInputEnter(row, key)"
                />
                <div v-if="row[key].error" class="error-message">{{ row[key].error }}</div>
              </div>
            </template>
          </template>
          <!-- 非编辑模式 -->
          <template v-else>
            <template v-if="key !== '角色'">
              <span v-if="row[key].error" class="error">{{ row[key].value }}</span>
              <span v-else>{{ row[key].value }}</span>
            </template>
            <template v-else>
              <span v-for="(role, index1) in row[key].value" :key="index1" :class="{ 'role-span': true, 'error': !role.id }">
                {{ role.name }}
              </span>
            </template>
          </template>
        </template>
      </el-table-column>
    </el-table>
    <div v-if="importFile" class="operation-instruction">
      双击单元格可编辑，再次双击或回车退出编辑
    </div>
    <span slot="footer" class="dialog-footer">
      <el-button @click="dialogVisible = false">取 消</el-button>
      <el-button type="primary" @click="handleImport(true)">导入全部</el-button>
      <el-button type="primary" @click="handleImport(false)">导入已选择用户</el-button>
    </span>
  </el-dialog>
</template>

<script>
import XLSX from 'xlsx'
import md5 from 'js-md5'
import LoadingUtils from '@/utils/loading-utils'
import { checkUserNames, batchCreateUsers } from '@/api/user'

export default {
  data() {
    return {
      dialogVisible: false,
      importFile: null,
      excelPreviewData: [],
      allRoles: [],
      roleNameIdMap: {},
      allKeys: ['用户名', '真实姓名', '密码', '邮箱', '性别', '地址', '简介', '电话', '角色'],
      columnWidths: [100, 100, 120, 150, 60, 100, 150, 120, 150],
      requiredFields: ['用户名', '密码', '角色'],
      selectedRows: [],
      inputTimer: 0 // 输入框输入的定时器，防止向后端发送过多请求
    }
  },
  methods: {
    /**
     * 显示或隐藏对话框
     * @param {Boolean} isShow - 是否显示
     */
    show(isShow) {
      this.dialogVisible = isShow
    },

    /**
     * 初始化角色列表，在第一次打开对话框时调用
     * @param {Array} roles - 角色列表
     */
    setRoles(roles) {
      this.allRoles = roles
      this.roleNameIdMap = {}
      for (const role of roles) {
        this.roleNameIdMap[role.name] = role.id
      }
    },

    /**
     * 获取单元格样式，如果单元格有错误则显示红色背景
     * @param {Object} param0 - 行和列信息
     * @returns {String} - 单元格样式
     */
    getCellClass({ row, column }) {
      if (!column.property || !row[column.property]) {
        return ''
      }
      return row[column.property].error ? 'error-cell' : ''
    },

    /**
     * 处理文件上传，读取文件内容并解析后显示在表格中
     * @param {Object} file - 上传的文件
     */
    handleFileChange(file) {
      this.importFile = file.raw
      const reader = new FileReader()
      reader.readAsBinaryString(this.importFile)
      reader.onload = e => {
        const data = e.target.result
        const workbook = XLSX.read(data, { type: 'binary' })
        const sheet = workbook.Sheets[workbook.SheetNames[0]]
        const json = XLSX.utils.sheet_to_json(sheet)
        this.excelPreviewData = []
        for (const item of json) {
          // 先判断是否是空行
          if (this.allKeys.every(key => !item[key])) {
            continue
          }
          const row = { errors: [] }
          for (const key of this.allKeys) {
            row[key] = {
              value: item[key] ? (item[key] + '').trim() : '',
              error: ''
            }
          }
          row['角色'].value = row['角色'].value.split(/[,，]/).map(role => role.trim())
            .filter(role => role).map(role => ({ name: role, id: null }))
          this.excelPreviewData.push(row)
        }
        this.checkAllRows()
      }
    },

    /**
     * 检查重复值
     * @param {String} key - 键名(用户名)
     * @param {Array} rows - 行数据
     */
    checkRepeatValue(key, rows) {
      const valueList = rows.map(row => row[key].value)
      checkUserNames(valueList).then(res => {
        for (const row of rows) {
          if (res.data.data[row[key].value]) {
            row[key].error = `${key}已存在`
          }
          this.updateRowErrors(row)
        }
      })
    },

    /**
     * 检查所有行数据的格式
     */
    checkAllRows() {
      if (this.excelPreviewData.length === 0) {
        return
      }
      // 检查是否有重复用户名
      this.checkRepeatValue('用户名', this.excelPreviewData)
      const roleNameIdMap = {}
      this.allRoles.forEach(role => {
        roleNameIdMap[role.name] = role.id
      })
      for (const row of this.excelPreviewData) {
        for (const key of this.allKeys) {
          this.checkField(key, row[key], false)
        }
        this.updateRowErrors(row)
      }
    },

    /**
     * 检查某行数据的某个字段是否合法
     * @param {String} field - 字段名
     * @param {Object} item - 该字段对应的对象值
     * @param {Boolean} checkRepeat - 是否检查重复名称，如果已经统一检查过了就无需再向后端发送请求去检验
     */
    checkField(field, item, checkRepeat = true) {
      item.error = ''
      if (this.requiredFields.includes(field)) {
        if (!item.value || item.value.length === 0) {
          item.error = `${field}不能为空`
          return
        }
      }
      switch (field) {
        case '用户名':
          checkRepeat && this.checkRepeatValue('用户名', [{ '用户名': item }])
          break
        case '密码':
          if (item.value && item.value.length < 6) {
            item.error = '密码长度不能小于6位'
          }
          break
        case '性别':
          if (item.value && (item.value !== '男' && item.value !== '女')) {
            item.error = '性别只能为男或女'
          }
          break
        case '角色': {
          // 检查角色是否存在
          const unKnownRoles = []
          for (const role of item.value) {
            const id = this.roleNameIdMap[role.name]
            if (id) {
              role.id = id
            } else {
              unKnownRoles.push('"' + role.name + '"')
            }
          }
          if (unKnownRoles.length > 0) {
            item.error = `角色${unKnownRoles.join(',')}不存在`
          }
          break
        }
        default:
          break
      }
    },

    /**
     * 更新某一行的整体错误信息
     * @param {Object} row - 表格的某一行数据
     */
    updateRowErrors(row) {
      row.errors = []
      for (const key of this.allKeys) {
        if (row[key] && row[key].error) {
          row.errors.push(row[key].error)
        }
      }
    },

    /**
     * 下载用户导入示例文件
     */
    downloadImportExample() {
      this.$http.get('example-file/用户信息示例.xlsx', { responseType: 'blob' }).then(
        response => {
          const url = window.URL.createObjectURL(new Blob([response.data]))
          const link = document.createElement('a')
          link.href = url
          link.setAttribute('download', '用户信息示例.xlsx')
          document.body.appendChild(link)
          link.click()
          document.body.removeChild(link)
        }
      )
    },

    /**
     * 导入用户
     * @param {Boolean} importAll - 是否导入全部用户
     */
    handleImport(importAll) {
      const importData = importAll ? this.excelPreviewData : this.selectedRows
      if (importData.length === 0) {
        this.$message.error(importAll || this.excelPreviewData.length === 0 ? '请先上传格式正确的excel文件' : '请选择需要导入的数据')
        return
      }
      const correctData = []
      for (const data of importData) {
        if (data.errors.length === 0) {
          correctData.push(data)
        }
      }
      if (correctData.length === 0) {
        this.$message.error('待导入数据格式均存在格式错误，请修改后再导入')
        return
      }
      // 检查待导入数据中是否有重复用户名
      const userNameSet = new Set()
      const finalData = []
      for (const data of correctData) {
        if (!userNameSet.has(data['用户名'].value)) {
          userNameSet.add(data['用户名'].value)
          finalData.push({
            userName: data['用户名'].value,
            trueName: data['真实姓名'].value,
            password: md5(data['密码'].value),
            email: data['邮箱'].value,
            gender: data['性别'].value === '女' ? 1 : 0,
            address: data['地址'].value,
            introduction: data['简介'].value,
            phone: data['电话'].value,
            roleIds: data['角色'].value.map(role => role.id)
          })
        }
      }
      // 询问是否导入
      const errorCount = importData.length - correctData.length
      const repeatCount = correctData.length - finalData.length
      const message = ((errorCount > 0 || repeatCount > 0 ? '共有' + importData.length + '条数据，其中：<br>' : '') +
        (errorCount > 0 ? errorCount + '条数据格式错误，已过滤；<br>' : '') +
        (repeatCount > 0 ? repeatCount + '条数据存在重复用户名，已过滤除了第一条外的重复数据；<br>' : '') +
        '是否导入' + (importAll ? '全部' : '所选') + finalData.length + '条数据？')
      this.$confirm(message, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
        dangerouslyUseHTMLString: true
      }).then(() => {
        LoadingUtils.createFullScreenLoading()
        batchCreateUsers(finalData).then(res => {
          if (res.data.status === 200) {
            const result = res.data.data
            if (result.successCount === 0) {
              this.$message.error('导入失败')
            } else {
              this.$message.success(`成功导入${result.successCount}条数据`)
              this.$emit('import-success', result.successCount)
            }
            if (result.existingUserNameList.length > 0) {
              setTimeout(() => {
                this.$message.warning(`${result.existingUserNameList.length}个重复用户名导入失败: ${result.existingUserNameList}`)
              })
            }
          }
        }).finally(() => {
          LoadingUtils.closeFullScreenLoading()
          this.dialogVisible = false
        })
      }).catch(() => {
        this.$message.info('已取消导入')
      })
    },

    /**
     * 鼠标移入某个单元格的事件，主要用于显示错误单元格的错误信息
     */
    handleCellMouseEnter(row, column, cell, event) {
      if (!column.property || !row[column.property]) {
        return
      }
      const error = row[column.property].error
      if (error) {
        cell.setAttribute('title', error)
      } else {
        cell.removeAttribute('title')
      }
    },

    /**
     * 单元格双击事件，切换显示与编辑模式
     */
    handleCellDblClick(row, column, cell, event) {
      event.preventDefault()
      if (!column.property || !row[column.property]) {
        return
      }
      const item = row[column.property]
      // 双击单元格切换进入编辑模式，使用$set触发视图更新
      if (item.edit) {
        this.$set(item, 'edit', false)
      } else {
        if (column.property === '角色') {
          // 删除异常角色
          if (item.value.some(role => !this.roleNameIdMap[role.name])) {
            item.value = item.value.filter(role => this.roleNameIdMap[role.name])
            this.checkFieldAndUpdateRowErrors(row, '角色')
          }
        } else if (column.property === '性别') {
          if (item.value !== '男' && item.value !== '女') {
            item.value = ''
            this.checkFieldAndUpdateRowErrors(row, '性别')
          }
        }
        this.$set(item, 'edit', true)
      }
    },

    /**
     * 检查某一格的格式并更新整行的错误信息
     * @param {Object} row - 表格的某一行数据
     * @param {String} key - 字段名
     */
    checkFieldAndUpdateRowErrors(row, key) {
      this.checkField(key, row[key])
      this.updateRowErrors(row)
    },

    /**
     * 编辑模式下输入框值改变的事件
     * @param {Object} row - 某行数据
     * @param {String} key - 字段名
     */
    handleInputChange(row, key) {
      this.inputTimer++
      // 防止输入过快，减少向后端发送请求的次数
      setTimeout(() => {
        this.inputTimer--
        if (this.inputTimer === 0) {
          this.checkFieldAndUpdateRowErrors(row, key)
        }
      }, 300)
    },

    /**
     * 编辑模式下输入框回车事件，用于关闭编辑模式
     * @param {Object} row - 某行数据
     * @param {String} key - 字段名
     */
    handleInputEnter(row, key) {
      this.$set(row[key], 'edit', false)
    },

    /**
     * el-table选择修改事件
     * @param {*} rows - 选择的行
     */
    handleSelectionChange(rows) {
      this.selectedRows = rows
    }
  }
}
</script>

<style scoped>
.upload-demo {
  margin: 10px;
  display: inline-block;
}
.file-name {
  margin: 5px;
}
.error {
  color: #ff0000;
}
.role-span {
  margin-right: 10px;
}
.error-message {
  color: #f56c6c;
  font-size: 6px;
  line-height: 1;
  margin-top: 2px;
}
.operation-instruction {
  text-align: center;
  color: #0000ff;
  margin-top: 10px;
}
</style>

<style>
#user-import-preview-table .error-cell {
  background-color: #ffdddd;
}
.user-import-dialog .el-dialog__body {
  padding-top: 10px;
}
.edit-input-container.is-error .el-input__inner {
  border-color: #f56c6c;
}
</style>
