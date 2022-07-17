<template>
  <el-main>
    <!-- 搜索表单 -->
    <el-form
      :model="parms"
      ref="searchForm"
      label-width="80px"
      :inline="true"
      size="small"
    >
      <el-form-item label="角色名称">
        <el-input v-model="parms.roleName"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button icon="el-icon-search" @click="searchBtn">查询</el-button>
        <el-button
          icon="el-icon-delete"
          style="color: #ff7670"
          @click="resetBtn"
          >重置</el-button
        >
        <el-button
          v-if="hasPerm('sys:role:add')" 
          type="primary" 
          icon="el-icon-plus" 
          @click="addBtn"
          >新增</el-button
        >
      </el-form-item>
    </el-form>
    <!-- 角色列表 -->
    <el-table :height="tableHeight" size="small" :data="roleList" border stripe>
      <el-table-column label="角色名称" prop="roleName"></el-table-column>
      <el-table-column label="备注" prop="remark"></el-table-column>
      <el-table-column align="center" width="290" label="操作">
        <template slot-scope="scope">
          <el-button
            v-if="hasPerm('sys:role:edit')"
            type="primary"
            icon="el-icon-edit"
            size="small"
            @click="editRole(scope.row)"
            >编辑</el-button
          >
          <el-button
            v-if="hasPerm('sys:role:assignMenu')"
            type="primary"
            icon="el-icon-edit"
            size="small"
            @click="assignRoleBtn(scope.row)"
            >分配权限</el-button
          >
          <el-button
            v-if="hasPerm('sys:role:delete')"
            type="danger"
            icon="el-icon-delete"
            size="small"
            @click="deleteRole(scope.row)"
            >删除</el-button
          >
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页 -->
    <el-pagination
      @size-change="sizeChange"
      @current-change="currentChange"
      :current-page.sync="parms.currentPage"
      :page-sizes="[10, 20, 40, 80, 100]"
      :page-size="parms.pageSize"
      layout="total, sizes, prev, pager, next, jumper"
      :total="parms.total"
      background
    >
    </el-pagination>
    <!-- 新增或编辑弹框 -->
    <sys-dialog
      :title="dialog.title"
      :height="dialog.height"
      :width="dialog.width"
      :visible="dialog.visible"
      @onConfirm="onConfirm"
      @onClose="onClose"
    >
      <div slot="content">
        <el-form
          :model="addModule"
          ref="addForm"
          :rules="rules"
          label-width="80px"
          :inline="true"
          size="normal"
        >
          <el-form-item prop="roleName" label="角色名称">
            <el-input v-model="addModule.roleName"></el-input>
          </el-form-item>
          <el-form-item label="备注">
            <el-input v-model="addModule.remark"></el-input>
          </el-form-item>
        </el-form>
      </div>
    </sys-dialog>
  
    <!-- 分配权限弹框 -->
    <sys-dialog
      :title="assignDialog.title"
      :width="assignDialog.width"
      :height="assignDialog.height"
      :visible="assignDialog.visible"
      @onClose="assignClose"
      @onConfirm="assignConfirm"
    >
      <template slot="content">
        <el-tree
          ref="assignTree"
          :data="assignTreeData"
          node-key="menuId"
          :props="defaultProps"
          empty-text="暂无数据"
          :show-checkbox="true"
          default-expand-all
          :default-checked-keys="assignTreeChecked"
        ></el-tree>
      </template>
    </sys-dialog>
  </el-main>
</template>

<script>
import { getRoleListApi,addRoleApi,editRoleApi,deleteRoleApi,getAssignTreeApi,assignSaveApi } from "@/api/role";
import SysDialog from "@/components/system/SysDialog";
import {getUserId} from "@/utils/auth"
export default {
  //注册组件
  components: {
    SysDialog,
  },
  //所有需要在页面展示的数据，都要显示的在data里面进行定义
  data() {
    return {
      defaultProps: {
        children: "children",
        label: "menuLabel",
      },
      //角色id
      roleId: "",
      //分配权限树数据
      assignTreeData: [],
      //树默认选中的节点
      assignTreeChecked: [],
      //分配权限弹框属性
      assignDialog: {
        title: "",
        width: 300,
        height: 500,
        visible: false,
      },

      //表单验证规则
      rules:{
        roleName:[{
          required:true,
          trigger:'change',
          message:'请填写角色名称'
        }]
      },
      //新增或编辑数据绑定
      addModule:{
        roleId:'',
        roleName:'',
        remark:'',
        type:''
      },
      //弹框属性
      dialog: {
        title: "",
        height: 120,
        width: 610,
        visible: false,
      },
      //表格数据
      roleList: [],
      //表格的高度
      tableHeight: 0,
      //查询参数
      parms: {
        pageSize: 10, //每页显示几条数据
        currentPage: 1, //当前第几页
        roleName: "",
        total: 0, //总条数
      },
    };
  },
  created() {
    this.getRoleList();
  },
  mounted() {
    this.$nextTick(() => {
      this.tableHeight = window.innerHeight - 220;
    });
  },
  methods: {
    //分配权限确认
    async assignConfirm() {
      //获取选中的节点id
      let ids = this.$refs.assignTree
        .getCheckedKeys()
        .concat(this.$refs.assignTree.getHalfCheckedKeys());
      console.log(ids);
      let parm = {
        roleId: this.roleId,
        list: ids,
      };
      let res = await assignSaveApi(parm);
      console.log(res);
      if (res && res.code == 200) {
        this.$message.success(res.msg);
        this.assignDialog.visible = false;
        console.log("reload")
        location.reload()
        //getRoleList();
      }
    },
    //分配权限取消
    assignClose() {
      this.assignDialog.visible = false;
    },
    //分配权限按钮
    async assignRoleBtn(row) {
      
      //清空数据
      this.roleId = "";
      this.assignTreeData = [];
      this.assignTreeChecked = [];
      this.roleId = row.roleId;
      //弹框属性设置
      this.assignDialog.title = "为【" + row.roleName + "】分配权限";
      this.assignDialog.visible = true;
      
      //获取树的数据
      let parm = {
        roleId: this.roleId,
        userId: getUserId(),
      };
      //console.log("assignRoleBtn");
      let res = await getAssignTreeApi(parm);
      //console.log("assignRoleBtn1");
      if (res && res.code == 200) {
        console.log(res);
        //赋值
        this.assignTreeData = res.data.listmenu;
        this.assignTreeChecked = res.data.checkList;
        //如果默认选中有数据
        if (this.assignTreeChecked.length > 0) {
          let newArr = [];
          this.assignTreeChecked.forEach((item) => {
            this.checked(item, this.assignTreeData, newArr);
          });
          this.assignTreeChecked = newArr;
        }
      }
    },
    checked(id, data, newArr) {
      data.forEach((item) => {
        if (item.menuId == id) {
          if (item.children && item.children.length == 0) {
            newArr.push(item.menuId);
          }
        } else {
          if (item.children && item.children.length != 0) {
            this.checked(id, item.children, newArr);
          }
        }
      });
    },

    //删除按钮
    async deleteRole(row) {
      let confrim = await this.$myconfirm("确定删除该数据吗？");
      if (confrim) {
        let res = await deleteRoleApi({ roleId: row.roleId });
        if (res && res.code == 200) {
          //刷新列表
          this.getRoleList();
          this.$message.success(res.msg);
        }
      }
    },
    //编辑按钮
    editRole(row) {
      //清空表单
      this.$resetForm("addForm", this.addModule);
      //设置为编辑
      this.addModule.type = "1";
      //设置弹框属性
      this.dialog.title = "编辑角色";
      this.$objCoppy(row, this.addModule);
      this.dialog.visible = true;
    },
    //新增或编辑取消事件
    onClose() {
      this.dialog.visible = false;
    },
    //新增或编辑确认事件
    onConfirm() {
      this.$refs.addForm.validate(async (valid) => {
        if (valid) {
          let res = null;
          if (this.addModule.type == "0") {
            res = await addRoleApi(this.addModule);
          } else {
            res = await editRoleApi(this.addModule);
          }
          if (res && res.code == 200) {
            //刷新列表
            this.getRoleList();
            this.dialog.visible = false;
            this.$message.success(res.msg);
          }
        }
      });
    },
    //页数改变时触发
    currentChange(val) {
      this.parms.currentPage = val;
      this.getRoleList();
    },
    //页容量改变时触发
    sizeChange(val) {
      this.parms.pageSize = val;
      this.getRoleList();
    },
    //新增按钮
    addBtn() {
      //清空表单数据
      this.$resetForm("addForm", this.addModule);
      this.addModule.type = "0";
      this.dialog.title = "新增角色";
      this.dialog.visible = true;
    },
    //重置按钮
    resetBtn() {
      this.parms.roleName = "";
      this.getRoleList();
    },
    //搜索按钮
    searchBtn() {
      this.getRoleList();
    },
    //获取角色列表
    async getRoleList() {
      let res = await getRoleListApi(this.parms);
      
      console.log(res);
      if (res && res.code == 200) {
        this.roleList = res.data.records;
        this.parms.total = res.data.total;
      }
    },
  },
};
</script>

<style lang="scss" scoped>
</style>