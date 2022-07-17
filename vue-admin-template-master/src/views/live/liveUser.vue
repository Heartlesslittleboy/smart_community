<template>
  <el-main>
    <!-- 搜索框 -->
    <el-form
      :model="parms"
      ref="searchForm"
      label-width="80px"
      :inline="true"
      size="small"
    >
      <el-form-item label="姓名">
        <el-input v-model="parms.loginName"></el-input>
      </el-form-item>
      <el-form-item label="电话">
        <el-input v-model="parms.phone"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button icon="el-icon-search" @click="searchBtn">查询</el-button>
        <el-button style="color: #ff7670" icon="el-icon-close" @click="resetBtn">重置</el-button>
        <el-button 
          v-if="hasPerm('sys:liveUser:add')"
          icon="el-icon-plus" 
          type="primary" 
          @click="addBtn"
          >新增</el-button>
      </el-form-item>
    </el-form>
    <!-- 表格 -->
    <el-table :height="tableHeight" :data="tableList" border stripe>
      <el-table-column prop="loginName" label="姓名"></el-table-column>
      <el-table-column prop="sex" label="性别">
        <template slot-scope="scope">
          <el-tag type="success" size="normal" v-if="scope.row.sex == '0'">男</el-tag>
          <el-tag type="danger" size="normal" v-if="scope.row.sex == '1'">女</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="phone" label="电话"></el-table-column>
      <el-table-column prop="name" label="栋数"></el-table-column>
      <el-table-column prop="unitName" label="单元"></el-table-column>
      <el-table-column prop="houseNum" label="房屋编号"></el-table-column>
      <el-table-column prop="houseArea" label="使用面积"></el-table-column>
      <el-table-column prop="parkName" label="车位"></el-table-column>
      <el-table-column align="center" prop="status" label="是否启用">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.status == '1'" type="danger" size="small">禁用</el-tag>
          <el-tag v-else type="success" size="small">启用</el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" width="480" label="操作">
        <template slot-scope="scope">
          <el-button 
            v-if="hasPerm('sys:liveUser:add')"
            type="success" 
            size="small" 
            @click="editBtn(scope.row)"
            >编辑</el-button
          >
          <el-button 
            v-if="hasPerm('sys:liveUser:assignHome')"
            type="primary" 
            size="small" 
            @click="assignHose(scope.row)"
            >分配房屋</el-button
          >
          <el-button 
            v-if="hasPerm('sys:liveUser:assignCar')"
            type="success" 
            size="small" 
            @click="assignPark(scope.row)"
            >分配车位</el-button
          >
          <el-button 
            v-if="hasPerm('sys:liveUser:returnHome')"
            type="danger" 
            size="small" 
            @click="leaveHose(scope.row)"
            >退房</el-button
          >
          <el-button 
            v-if="hasPerm('sys:liveUser:returnCar')"
            type="warning" 
            size="small" 
            @click="leavePark(scope.row)"
            >退车位</el-button
          >
          <el-button 
            v-if="hasPerm('sys:liveUser:delete')"
            type="danger" 
            size="small" 
            @click="deleteBtn(scope.row)"
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

    <!-- 弹框 -->
    <sys-dialog
      :title="addDialog.title"
      :height="addDialog.height"
      :width="addDialog.width"
      :visible="addDialog.visible"
      @onClose="onClose"
      @onConfirm="onConfirm"
    >
      <template slot="content">
        <el-form
          :model="addModel"
          ref="addForm"
          :rules="rules"
          label-width="80px"
          :inline="true"
          size="small"
        >
          <el-form-item prop="loginName" label="姓名">
            <el-input v-model="addModel.loginName"></el-input>
          </el-form-item>
          <el-form-item prop="sex" label="性别">
            <el-radio-group v-model="addModel.sex">
              <el-radio :label="'0'">男</el-radio>
              <el-radio :label="'1'">女</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item prop="phone" label="电话">
            <el-input v-model="addModel.phone"></el-input>
          </el-form-item>
          <el-form-item prop="roleId" label="角色">
            <el-select v-model="addModel.roleId">
              <el-option
                v-for="item in roleList"
                :key="item.roleId"
                :label="item.roleName"
                :value="item.roleId"
              >
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item prop="username" label="账户">
            <el-input v-model="addModel.username"></el-input>
          </el-form-item>
          <el-form-item v-if="addModel.editType != '1'" prop="password" label="密码">
            <el-input v-model="addModel.password"></el-input>
          </el-form-item>
          <el-form-item prop="status" label="状态">
            <el-radio-group v-model="addModel.status">
              <el-radio :label="'0'">启用</el-radio>
              <el-radio :label="'1'">禁用</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-form>
      </template>
    </sys-dialog>
    <!-- 分配房屋 -->
    <sys-dialog
      :title="assignHouseDialog.title"
      :height="assignHouseDialog.height"
      :width="assignHouseDialog.width"
      :visible="assignHouseDialog.visible"
      @onClose="assignHoseClose"
      @onConfirm="assignHoseConfirm"
    >
      <template slot="content">
        <el-main style="padding: 0px">
          <!-- 搜索框 -->
          <el-form
            :model="houseParms"
            ref="assignHoseForm"
            label-width="70px"
            :inline="true"
            size="small"
          >
            <el-form-item label="栋数名称">
              <el-input
                placeholder="请输入栋数名称"
                v-model="houseParms.buildNme"
              ></el-input>
            </el-form-item>
            <el-form-item label="单元名称">
              <el-input
                placeholder="请输入单元名称"
                v-model="houseParms.unitName"
              ></el-input>
            </el-form-item>
            <el-form-item label="房屋编号">
              <el-input
                placeholder="请输入房屋编号"
                v-model="houseParms.houseNum"
              ></el-input>
            </el-form-item>
            <el-form-item>
              <el-button icon="el-icon-search" @click="assignHouseSearchBtn"
                >搜索</el-button
              >
              <el-button icon="el-icon-close" @click="assignHouseResetBtn"
                >重置</el-button
              >
            </el-form-item>
          </el-form>
          <!-- 表格 -->
          <el-table :height="assignTableHeight" :data="houseList" border stripe>
            <el-table-column width="50" align="center" label="选择">
              <template slot-scope="scope">
                <el-radio
                  v-model="assignHoseParm.houseId"
                  :label="scope.row.houseId"
                  @change="getCurrentRow(scope.row)"
                >
                  {{ "" }}
                </el-radio>
              </template>
            </el-table-column>
            <el-table-column
              align="center"
              prop="name"
              label="栋数名称"
            ></el-table-column>
            <el-table-column
              align="center"
              prop="unitName"
              label="单元名称"
            ></el-table-column>
            <el-table-column
              align="center"
              prop="houseNum"
              label="房屋编号"
            ></el-table-column>
            <el-table-column
              align="center"
              prop="houseArea"
              label="使用面积"
            ></el-table-column>
            <el-table-column align="center" prop="status" label="分配状态">
              <template slot-scope="scope">
                <el-tag v-if="scope.row.status == '0'" type="danger" size="small"
                  >未分配</el-tag
                >
                <el-tag v-if="scope.row.status == '1'" type="success" size="small"
                  >已分配</el-tag
                >
              </template>
            </el-table-column>
          </el-table>
          <!-- 分页 -->
          <el-pagination
            @size-change="assignHouseSize"
            @current-change="assignHouseChange"
            :current-page.sync="houseParms.currentPage"
            :page-sizes="[10, 20, 40, 80, 100]"
            :page-size="houseParms.pageSize"
            layout="total, sizes, prev, pager, next, jumper"
            :total="houseParms.total"
            background
          >
          </el-pagination>
        </el-main>
      </template>
    </sys-dialog>
    <!-- 分配车位 -->
    <sys-dialog
      :title="parkDialog.title"
      :height="parkDialog.height"
      :width="parkDialog.width"
      :visible="parkDialog.visible"
      @onClose="parkOnClose"
      @onConfirm="parkOnConfirm"
    >
      <template slot="content">
        <el-main style="padding: 0px">
          <!-- 搜索框 -->
          <el-form :model="parkParms" label-width="80px" :inline="true" size="small">
            <el-form-item label="车位名称">
              <el-input v-model="parkParms.parkName"></el-input>
            </el-form-item>
            <el-form-item>
              <el-button icon="el-icon-search" @click="parkSearchBtn">查询</el-button>
              <el-button style="color: #ff7670" icon="el-icon-close" @click="parkResetBtn"
                >重置</el-button
              >
            </el-form-item>
          </el-form>
          <!-- 表格 -->
          <el-table :height="parkTableHeight" :data="parkList" border stripe>
            <el-table-column align="center" label="操作" width="50">
              <template slot-scope="scope">
                <el-radio
                  v-model="assignParkParm.parkId"
                  :label="scope.row.parkId"
                  @change="getAssignParkId(scope.row)"
                >
                  {{ "" }}
                </el-radio>
              </template>
            </el-table-column>
            <el-table-column
              align="center"
              prop="parkName"
              label="车位名称"
            ></el-table-column>
            <el-table-column align="center" prop="parkType" label="车位类型">
              <template slot-scope="scope">
                <el-tag v-if="scope.row.parkType == '0'" type="danger" size="small"
                  >地上</el-tag
                >
                <el-tag v-if="scope.row.parkType == '1'" type="success" size="small"
                  >地下</el-tag
                >
              </template>
            </el-table-column>
            <el-table-column align="center" prop="parkName" label="使用状态">
              <template slot-scope="scope">
                <el-tag v-if="scope.row.parkStatus == '0'" type="danger" size="small"
                  >未使用</el-tag
                >
                <el-tag v-if="scope.row.parkStatus == '1'" type="success" size="small"
                  >已使用</el-tag
                >
              </template>
            </el-table-column>
          </el-table>
          <!-- 分页 -->
          <el-pagination
            @size-change="assignParkSizeChange"
            @current-change="assignParkCurrentChange"
            :current-page.sync="parkParms.currentPage"
            :page-sizes="[10, 20, 40, 80, 100]"
            :page-size="parkParms.pageSize"
            layout="total, sizes, prev, pager, next, jumper"
            :total="parkParms.total"
            background
          >
          </el-pagination>
        </el-main>
      </template>
    </sys-dialog>
  </el-main>
</template>

<script>
import SysDialog from "@/components/system/SysDialog.vue";
import {
  getRoleListApi,
  addApi,
  getListApi,
  getUserByIdApi,
  editApi,
  getHouseListApi,
  assignSaveApi,
  getParkListApi,
  assignParkSaveApi,
  returnHouseApi,
  returnParkApi,
  deleteUserApi,
} from "@/api/liveUser";
export default {
  components: { SysDialog },
  data() {
    return {
      //分配车位列表高度
      parkTableHeight: 0,
      //分配车位参数
      assignParkParm: {
        parkId: "",
        userId: "",
      },
      //车位弹框属性
      parkDialog: {
        title: "",
        height: 400,
        width: 900,
        visible: false,
      },
      //车位列表
      parkList: [],
      //车位查询参数
      parkParms: {
        currentPage: 1,
        pageSize: 10,
        parkName: "",
        parkStatus: "0", //0未使用 1 已使用
        parkType: "",
        total: 0,
      },
      //分配房屋列表高度
      assignTableHeight: 0,
      //分配房屋提交的数据
      assignHoseParm: {
        houseId: "",
        userId: "",
      },
      //分配房屋弹框属性
      assignHouseDialog: {
        title: "",
        height: 400,
        width: 1050,
        visible: false,
      },
      //房屋数据域
      houseList: [],
      //获取房屋列表的参数
      houseParms: {
        buildNme: "",
        status: "0",
        unitName: "",
        houseNum: "",
        currentPage: 1,
        pageSize: 10,
        total: 0,
      },
      //表格的高度
      tableHeight: 0,
      //业主列表
      tableList: [],
      //角色列表
      roleList: [],
      //表单绑定数据源
      addModel: {
        editType: "",
        userId: "",
        username: "",
        phone: "",
        sex: "",
        loginName: "",
        password: "",
        status: "",
        roleId: "",
      },
      //表单验证规则
      rules: {
        loginName: [
          {
            trigger: "change",
            required: true,
            message: "请填写姓名",
          },
        ],
        phone: [
          {
            trigger: "change",
            required: true,
            message: "请填写电话",
          },
        ],
        sex: [
          {
            trigger: "change",
            required: true,
            message: "请选择性别",
          },
        ],
        username: [
          {
            trigger: "change",
            required: true,
            message: "请填写登录名",
          },
        ],
        password: [
          {
            trigger: "change",
            required: true,
            message: "请填写登录密码",
          },
        ],
        status: [
          {
            trigger: "change",
            required: true,
            message: "请选择用户状态",
          },
        ],
        roleId: [
          {
            trigger: "change",
            required: true,
            message: "请选择用户角色",
          },
        ],
      },
      //弹框属性
      addDialog: {
        title: "",
        height: 220,
        width: 620,
        visible: false,
      },
      //参数
      parms: {
        loginName: "",
        phone: "",
        currentPage: 1,
        pageSize: 10,
        total: 0,
      },
    };
  },
  created() {
    this.getRoleList();
    this.getList();
  },
  mounted() {
    this.$nextTick(() => {
      this.tableHeight = window.innerHeight - 210;
      // this.assignTableHeight = window.innerHeight - 660;
      // this.parkTableHeight = window.innerHeight - 660;
    });
  },
  methods: {
    //重置按钮
    resetBtn() {
      this.parms.loginName = '';
      this.parms.phone = '';
      this.getList();
    },
    //查询按钮
    searchBtn() {
      this.getList();
    },

    //分配车位页数改变触发
    assignParkCurrentChange(val) {
      this.parkParms.currentPage = val;
      this.getAssignParkList();
    },
    //分配车位页容量改变触发
    assignParkSizeChange(val) {
      this.parkParms.pageSize = val;
      this.getAssignParkList();
    },

    //分配车位点击
    getAssignParkId(row) {
      this.assignParkParm.parkId = row.parkId;
      console.log(this.assignParkParm);
    },
    //分配车位弹框确认
    async parkOnConfirm() {
      //验证是否选择车位
      if (!this.assignParkParm.parkId) {
        this.$message.warning("请选择车位！");
        return;
      }
      let res = await assignParkSaveApi(this.assignParkParm);
      if (res && res.code == 200) {
        this.$message.success(res.msg);
        this.parkDialog.visible = false;
        //刷新列表
        this.getList();
      }
    },
    //分配车位弹框关闭
    parkOnClose() {
      this.parkDialog.visible = false;
    },

    //分配房屋列表页数改变
    assignHouseChange(val) {
      this.houseParms.currentPage = val;
      this.getAssignHoseList();
    },
    //分配房屋列表页容量改变事件
    assignHouseSize(val) {
      this.houseParms.pageSize = val;
      this.getAssignHoseList();
    },

    //分配房屋列表搜索事件
    assignHouseSearchBtn() {
      this.getAssignHoseList();
    },
    //分配房屋列表重置事件
    assignHouseResetBtn() {
      //清空搜索框数据
      this.houseParms.buildNme = "";
      this.houseParms.unitName = "";
      this.houseParms.houseNum = "";
      this.getAssignHoseList();
    },
    //房屋选择事件
    getCurrentRow(row) {
      console.log(row);
      this.assignHoseParm.houseId = row.houseId;
      console.log(this.assignHoseParm);
    },
    // 分配房屋关闭事件
    assignHoseClose() {
      this.assignHouseDialog.visible = false;
    },
    //分配房屋确认事件
    async assignHoseConfirm() {
      if (!this.assignHoseParm.houseId) {
        this.$message.warning("请选择要分配的房屋!");
        return;
      }
      //数据提交
      let res = await assignSaveApi(this.assignHoseParm);
      if (res && res.code == 200) {
        //刷新列表
        this.getList();
        this.assignHouseDialog.visible = false;
        this.$message.success(res.msg);
      }
    },
    //编辑业主
    async editBtn(row) {
      //清空表单
      this.$resetForm("addForm", this.addModel);

      //获取编辑的数据
      let res = await getUserByIdApi({ userId: row.userId });
      if (res && res.code == 200) {
        console.log(res);
        if (res.data) {
          this.$objCoppy(res.data, this.addModel);
        }
      }
      //设置编辑属性
      this.addModel.editType = "1";
      //设置弹框属性
      this.addDialog.title = "编辑业主";
      this.addDialog.visible = true;
      console.log(this.addModel);
    },
    //删除业主
    async deleteBtn(row) {
      console.log(row);
      let confrim = await this.$myconfirm("确定删除该数据吗?");
      if (confrim) {
        let res = await deleteUserApi({
          userId: row.userId,
          houseId: row.houseId,
        });
        if (res && res.code == 200) {
          //刷新列表
          this.getList();
          //信息提示
          this.$message.success(res.msg);
        }
      }
    },
    //退车位
    async leavePark(row) {
       console.log(row);
       if(!row.liveStatue){
        this.$message.warning('该租户暂时无可退的车位!');
        return;
      }
      let confirm = await this.$myconfirm("确定退车位吗？");
      if (confirm) {
        let res = await returnParkApi({
          userId: row.userId,
          parkId: row.parkId,
        });
        if (res && res.code == 200) {
          //刷新列表
          this.getList();
          //信息提示
          this.$message.success(res.msg);
        }
      }
    },
    //退房
    async leaveHose(row) {
      console.log(row);
      if(!row.useStatus){
        this.$message.warning('该租户暂时无可退的房屋!');
        return;
      }
      let confirm = await this.$myconfirm("确定退房吗？");
      if (confirm) {
        let res = await returnHouseApi({
          userId: row.userId,
          houseId: row.houseId,
        });
        if (res && res.code == 200) {
          //刷新列表
          this.getList();
          //信息提示
          this.$message.success(res.msg);
        }
      }
    },
    //分配车位列表重置事件
    parkResetBtn() {
      this.parkParms.parkName = "";
      //获取车位列表
      this.getAssignParkList();
    },
    //分配车位查询按钮
    parkSearchBtn() {
      //获取车位列表
      this.getAssignParkList();
    },
    //分配车位
    async assignPark(row) {
      console.log(row);
      if (row.parkName) {
        this.$message.warning("已分配车位，不能重复分配!");
        return;
      }
      //清空数据
      this.assignParkParm.parkId = "";
      this.parkParms.parkName = "";
      this.assignParkParm.userId = row.userId;
      //获取车位列表
      this.getAssignParkList();
      //弹框属性设置
      this.parkDialog.title = "为【" + row.loginName + "】分配车位";
      this.parkDialog.visible = true;
      this.$nextTick(() => {
        this.parkTableHeight = window.innerHeight - 450;
      });
    },
    async getAssignParkList() {
      let res = await getParkListApi(this.parkParms);
      if (res && res.code == 200) {
        console.log(res);
        //把数据赋值给车位列表
        this.parkList = res.data.records;
        this.parkParms.total = res.data.total;
      }
    },
    //分配房屋
    async assignHose(row) {
      console.log(row);
      if (row.houseNum) {
        this.$message.warning("已经分配房屋，不能重复分配!");
        return;
      }
      //清空搜索框数据
      this.houseParms.buildNme = "";
      this.houseParms.unitName = "";
      this.houseParms.houseNum = "";
      this.assignHoseParm.houseId = "";
      this.assignHoseParm.userId = "";
      //获取未分配的房屋列表
      this.getAssignHoseList();
      //弹框属性设置
      this.assignHouseDialog.title = "为【" + row.loginName + "】分配房屋";
      this.assignHouseDialog.visible = true;
      this.$nextTick(() => {
        this.assignTableHeight = window.innerHeight - 450;
      });
      //设置分配人
      this.assignHoseParm.userId = row.userId;
    },
    async getAssignHoseList() {
      let res = await getHouseListApi(this.houseParms);
      if (res && res.code == 200) {
        //把返回的数据放到接收房屋的数据域里面
        console.log(res);
        this.houseList = res.data.records;
        this.houseParms.total = res.data.total;
      }
    },
    //页数改变时触发
    currentChange(val) {
      this.parms.currentPage = val;
      this.getList();
    },
    //页容量改变触发
    sizeChange(val) {
      this.parms.pageSize = val;
      this.getList();
    },
    //获取角色列表
    async getRoleList() {
      let res = await getRoleListApi();
      if (res && res.code == 200) {
        console.log("角色列表");
        console.log(res);
        this.roleList = res.data;
      }
    },
    //弹框关闭
    onClose() {
      this.addDialog.visible = false;
    },
    //弹框确认
    onConfirm() {
      this.$refs.addForm.validate(async (valid) => {
        if (valid) {
          let res = null;
          if (this.addModel.editType == "0") {
            res = await addApi(this.addModel);
          } else {
            res = await editApi(this.addModel);
          }
          if (res && res.code == 200) {
            //刷新列表
            this.getList();
            this.addDialog.visible = false;
            this.$message.success(res.msg);
          }
        }
      });
    },
    //获取业主列表
    async getList() {
      let res = await getListApi(this.parms);
      if (res && res.code == 200) {
        this.tableList = res.data.records;
        this.parms.total = res.data.total;
      }
      console.log("业主列表");
      console.log(res);
    },
    //新增按钮
    addBtn() {
      //清空表单
      this.$resetForm("addForm", this.addModel);
      //设置编辑属性
      this.addModel.editType = "0";
      //设置弹框属性
      this.addDialog.title = "新增业主";
      this.addDialog.visible = true;
    },
  },
};
</script>

<style lang="scss" scoped></style>
