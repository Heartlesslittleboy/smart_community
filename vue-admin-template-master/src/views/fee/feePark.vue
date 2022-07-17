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
        <el-input v-model="parms.userName"></el-input>
      </el-form-item>
      <el-form-item label="车位名称">
        <el-input v-model="parms.parkName"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button icon="el-icon-search" @click="searchBtn">查询</el-button>
        <el-button style="color: #ff7670" @click="resetBtn" icon="el-icon-close"
          >重置</el-button
        >
        <el-button icon="el-icon-plus"
          v-if="hasPerm('sys:feePark:add')"
          @click="addBtn" 
          type="primary"
          >新增</el-button>
      </el-form-item>
    </el-form>
    <!-- 表格 -->
    <el-table :height="tableHeight" :data="tableList" border stripe>
      <el-table-column label="姓名" prop="loginName"></el-table-column>
      <el-table-column label="电话" prop="phone"></el-table-column>
      <el-table-column label="车位" prop="parkName"></el-table-column>
      <el-table-column label="车位类型" prop="parkType">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.parkType == '0'" type="danger" size="small"
            >地上</el-tag
          >
          <el-tag v-if="scope.row.parkType == '1'" type="success" size="small"
            >地下</el-tag
          >
        </template>
      </el-table-column>
      <el-table-column label="所属月份" prop="payParkMonth"></el-table-column>
      <el-table-column label="缴费金额" prop="payParkMoney"></el-table-column>
      <el-table-column label="缴费状态" prop="payParkStatus">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.payParkStatus == '0'" type="danger" size="small"
            >未缴费</el-tag
          >
          <el-tag v-if="scope.row.payParkStatus == '1'" type="success" size="small"
            >已缴费</el-tag
          >
        </template>
      </el-table-column>
      <el-table-column width="270" align="center" label="操作">
        <template slot-scope="scope">
          <el-button
            v-if="hasPerm('sys:feePark:edit')"
            icon="el-icon-edit"
            type="primary"
            size="small"
            @click="editBtn(scope.row)"
            >编辑</el-button
          >
          <el-button
            v-if="hasPerm('sys:feePark:delete')"
            icon="el-icon-delete"
            type="danger"
            size="small"
            @click="deleteBtn(scope.row)"
            >删除</el-button
          >
          <el-button
            v-if="scope.row.payParkStatus == '0' && hasPerm('sys:feePark:pay')"
            icon="el-icon-delete"
            type="warning"
            size="small"
            @click="payBtn(scope.row)"
            >缴费</el-button
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
    <!-- 新增弹框 -->
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
          <el-form-item prop="parkId" label="车位">
            <el-select v-model="addModel.parkId" clearable filterable>
              <el-option
                v-for="item in parkList"
                :key="item.parkId"
                :label="item.parkName"
                :value="item.parkId"
              >
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item prop="payParkMonth" label="所属月份">
            <el-date-picker
              format="yyyy-MM"
              value-format="yyyy-MM"
              v-model="addModel.payParkMonth"
              type="month"
              placeholder="选择月份"
            >
            </el-date-picker>
          </el-form-item>
          <el-form-item prop="payParkMoney" label="缴费金额" size="small">
            <el-input v-model="addModel.payParkMoney"></el-input>
          </el-form-item>
          <el-form-item prop="payParkStatus" label="缴费状态" size="small">
            <el-radio-group v-model="addModel.payParkStatus">
              <el-radio :label="'0'">未缴费</el-radio>
              <el-radio :label="'1'">已缴费</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-form>
      </template>
    </sys-dialog>
  </el-main>
</template>

<script>
import { getListApi, getParkListApi, addApi, editApi,deleteApi,payApi } from "@/api/feePark";
import SysDialog from "@/components/system/SysDialog.vue";
export default {
  components: { SysDialog },
  data() {
    return {
      //表单数据域
      addModel: {
        parkFeeId: "",
        editType: "",
        parkId: "",
        payParkMonth: "",
        payParkMoney: "",
        payParkStatus: "",
      },
      //表单验证规则
      rules: {
        parkId: [
          {
            trigger: "change",
            required: true,
            message: "请选择车位",
          },
        ],
        payParkMonth: [
          {
            trigger: "change",
            required: true,
            message: "请填写缴费月份",
          },
        ],
        payParkMoney: [
          {
            trigger: "change",
            required: true,
            message: "请填写缴费金额",
          },
        ],
        payParkStatus: [
          {
            trigger: "change",
            required: true,
            message: "请选择缴费状态",
          },
        ],
      },
      //弹框属性
      addDialog: {
        title: "",
        height: 180,
        width: 650,
        visible: false,
      },
      //车辆列表
      parkList: [],
      //表格高度
      tableHeight: 0,
      //表格的数据
      tableList: [],
      //列表查询参数
      parms: {
        currentPage: 1,
        pageSize: 10,
        userName: "",
        parkName: "",
        total: 0,
      },
    };
  },
  created() {
    this.getList();
    this.getParkList();
  },
  mounted() {
    this.$nextTick(() => {
      this.tableHeight = window.innerHeight - 210;
    });
  },
  methods: {
    //弹框确定事件
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
            //刷新表格
            this.getList();
            //信息提示
            this.$message.success(res.msg);
            this.addDialog.visible = false;
          }
        }
      });
    },
    //弹框关闭
    onClose() {
      this.addDialog.visible = false;
    },
    //获取车辆列表
    async getParkList() {
      let res = await getParkListApi();
      if (res && res.code == 200) {
        console.log("获取车辆");
        console.log(res);
        this.parkList = res.data;
      }
    },
    //页数改变时触发
    currentChange(val) {
      this.parms.currentPage = val;
      this.getList();
    },
    //页容量改变时触发
    sizeChange(val) {
      this.parms.pageSize = val;
      this.getList();
    },
    //缴费按钮
    async payBtn(row) {
       console.log(row)
      //提示信息
      let confirm = await this.$myconfirm('确定缴费吗？');
      if(confirm){
        let res = await payApi({parkFeeId:row.parkFeeId})
        if(res && res.code == 200){
          //刷新列表
          this.getList();
          //信息提示
          this.$message.success(res.msg)
        }
      }
    },
    //删除按钮
    async deleteBtn(row) {
      console.log(row)
      //提示信息
      let confirm = await this.$myconfirm('确定删除该数据吗？');
      if(confirm){
        let res = await deleteApi({parkFeeId:row.parkFeeId})
        if(res && res.code == 200){
          //刷新列表
          this.getList();
          this.$message.success(res.msg)
        }
      }
    },
    //编辑按钮
    editBtn(row) {
      console.log(row);
      //清空表单
      this.$resetForm("addForm", this.addModel);
      //设置弹框属性
      this.addDialog.title = "编辑停车费";
      this.addDialog.visible = true;
      //把当前有编辑的数据赋值到表单数据域
      this.$objCoppy(row, this.addModel);
      //设置编辑属性
      this.addModel.editType = "1";
    },
    //新增按钮
    addBtn() {
      //清空表单
      this.$resetForm("addForm", this.addModel);
      //设置编辑属性
      this.addModel.editType = "0";
      //设置弹框属性
      this.addDialog.title = "新增停车费";
      this.addDialog.visible = true;
    },
    //重置按钮
    resetBtn() {
      this.parms.userName = "";
      this.parms.parkName = "";
      this.getList();
    },
    //搜索按钮
    searchBtn() {
      this.getList();
    },
    //查询列表
    async getList() {
      let res = await getListApi(this.parms);
      console.log(res);
      if (res && res.code == 200) {
        //赋值给表格数据
        this.tableList = res.data.records;
        //分页总条数
        this.parms.total = res.data.total;
      }
    },
  },
};
</script>

<style lang="scss" scoped></style>