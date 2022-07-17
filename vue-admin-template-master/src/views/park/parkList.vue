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
      <el-form-item label="车位名称">
        <el-input v-model="parms.parkName"></el-input>
      </el-form-item>
      <el-form-item label="车位类型">
        <el-select v-model="parms.parkType">
          <el-option
            v-for="item in typeOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          >
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="使用状态">
        <el-select v-model="parms.parkStatus">
          <el-option
            v-for="item in statusOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          >
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button icon="el-icon-search" @click="searchBtn">查询</el-button>
        <el-button style="color: #ff7670" icon="el-icon-close" @click="resetBtn"
          >重置</el-button
        >
        <el-button v-if="hasPerm('sys:parkList:add')" icon="el-icon-plus" type="primary" @click="addBtn">新增</el-button>
      </el-form-item>
    </el-form>
    <!-- 表格列表 -->
    <el-table :height="tableHeight" :data="tableList" border stripe>
      <el-table-column prop="parkName" label="车位名称"></el-table-column>
      <el-table-column prop="parkType" label="车位类型">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.parkType == '0'" type="danger" size="small"
            >地上</el-tag
          >
          <el-tag v-if="scope.row.parkType == '1'" type="success" size="small"
            >地下</el-tag
          >
        </template>
      </el-table-column>
      <el-table-column prop="parkName" label="使用状态">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.parkStatus == '0'" type="danger" size="small"
            >未使用</el-tag
          >
          <el-tag v-if="scope.row.parkStatus == '1'" type="success" size="small"
            >已使用</el-tag
          >
        </template>
      </el-table-column>
      <el-table-column prop="parkNum" label="序号"></el-table-column>
      <el-table-column width="180" align="center" label="操作">
        <template slot-scope="scope">
          <el-button v-if="hasPerm('sys:parkList:edit')" type="primary" size="small" @click="editBtn(scope.row)"
            >编辑</el-button
          >
          <el-button v-if="hasPerm('sys:parkList:delete')" type="danger" size="small" @click="deleteBtn(scope.row)"
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
    <!-- 定义弹框 -->
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
          <el-form-item prop="parkName" label="车位名称">
            <el-input v-model="addModel.parkName"></el-input>
          </el-form-item>
          <el-form-item prop="parkType" label="车位类型">
            <el-select v-model="addModel.parkType">
              <el-option
                v-for="item in options"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              >
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item prop="parkStatus" label="使用状态">
            <el-select v-model="addModel.parkStatus">
              <el-option
                v-for="item in useOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              >
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="车位序号">
            <el-input v-model="addModel.parkNum"></el-input>
          </el-form-item>
        </el-form>
      </template>
    </sys-dialog>
  </el-main>
</template>

<script>
import { getListApi, addApi, editApi, deleteApi } from "@/api/parkList";
import SysDialog from "@/components/system/SysDialog.vue";
export default {
  //注册组件
  components: { SysDialog },
  data() {
    return {
      //车位使用状态
      useOptions: [
        {
          value: "0",
          label: "未使用",
        },
        {
          value: "1",
          label: "已使用",
        },
      ],
      //车位类型
      options: [
        {
          value: "0",
          label: "地上",
        },
        {
          value: "1",
          label: "地下",
        },
      ],
      //表单验证
      rules: {
        parkName: [
          {
            trigger: "change",
            required: true,
            message: "请填写车位名称",
          },
        ],
        parkType: [
          {
            trigger: "change",
            required: true,
            message: "请选择车位类型",
          },
        ],
        parkStatus: [
          {
            trigger: "change",
            required: true,
            message: "请选择使用状态",
          },
        ],
      },
      //表单数据
      addModel: {
        editType: "", //0 新增 1：编辑
        parkId: "",
        parkType: "",
        parkName: "",
        parkStatus: "",
        parkNum: "",
      },
      //弹框属性
      addDialog: {
        title: "",
        height: 150,
        width: 620,
        visible: false,
      },
      //表格高度
      tableHeight: 0,
      statusOptions: [
        {
          value: "0",
          label: "未使用",
        },
        {
          value: "1",
          label: "已使用",
        },
      ],
      typeOptions: [
        {
          value: "0",
          label: "地上",
        },
        {
          value: "1",
          label: "地下",
        },
      ],
      //表格数据
      tableList: [],
      //查询参数
      parms: {
        currentPage: 1,
        pageSize: 10,
        parkName: "",
        parkStatus: "",
        parkType: "",
        total: 0,
      },
    };
  },
  created() {
    this.getList();
  },
  mounted() {
    this.$nextTick(() => {
      this.tableHeight = window.innerHeight - 210;
    });
  },
  methods: {
    //弹框确认
    onConfirm() {
      this.$refs.addForm.validate(async (valid) => {
        if (valid) {
          let res = null;
          // 数据提交
          if (this.addModel.editType == "0") {
            res = await addApi(this.addModel);
          } else {
            res = await editApi(this.addModel);
          }
          if (res && res.code == 200) {
            this.addDialog.visible = false;
            //刷新表格
            this.getList();
          }
        }
      });
    },
    //弹框关闭
    onClose() {
      this.addDialog.visible = false;
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
    async deleteBtn(row) {
      //信息提示
      const confirm = await this.$myconfirm("确定删除该数据吗?");
      if (confirm) {
        let res = await deleteApi({ parkId: row.parkId });
        if (res && res.code == 200) {
          //刷新表格
          this.$message.success(res.msg);
          this.getList();
        }
      }
    },
    //编辑按钮
    editBtn(row) {
      //清空表单
      this.$resetForm("addForm", this.addModel);
      //设置编辑属性
      this.addModel.editType = "1";
      //设置弹框属性
      this.addDialog.title = "编辑车位";
      this.addDialog.visible = true;
      //赋值回显
      this.$objCoppy(row, this.addModel);
    },
    //新增按钮
    addBtn() {
      //清空表单
      this.$resetForm("addForm", this.addModel);
      //设置设置编辑标识
      this.addModel.editType = "0";
      //设置弹框属性
      this.addDialog.title = "新增车位";
      this.addDialog.visible = true;
    },
    //重置按钮
    resetBtn() {
      this.parms.parkName = '';
      this.parms.parkStatus = '';
      this.parms.parkType = '';
      this.getList();
    },
    // 搜索按钮
    searchBtn() {
      this.getList();
    },
    //获取列表
    async getList() {
      let res = await getListApi(this.parms);
      if (res && res.code == 200) {
        console.log(res);
        //把返回值赋值给表格
        this.tableList = res.data.records;
        this.parms.total = res.data.total;
      }
    },
  },
};
</script>

<style lang="scss" scoped></style>
