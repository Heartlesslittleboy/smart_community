<template>
  <el-main>
    <!-- 搜索框 -->
    <el-form
      :model="parm"
      ref="searchForm"
      label-width="80px"
      :inline="true"
      size="small"
    >
      <el-form-item label="栋数名称">
        <el-input v-model="parm.name"></el-input>
      </el-form-item>
      <el-form-item label="栋数类型">
        <el-select v-model="parm.type" placeholder="" clearable filterable>
          <el-option
            v-for="item in options"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          >
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button icon="el-icon-search" @click="searchBtn">查询</el-button>
        <el-button @click="cancelBtn" style="color: #ff7670" icon="el-icon-close"
          >重置</el-button
        >
        <el-button 
          v-if="hasPerm('sys:houseBuilding:add')"
          icon="el-icon-plus" 
          type="primary" 
          @click="addBtn"
          >新增</el-button>
      </el-form-item>
    </el-form>
    <!-- 表格列表 -->
    <el-table :height="tableHeight" :data="tableList" border stripe>
      <el-table-column prop="name" label="栋数名称"></el-table-column>
      <el-table-column prop="orderNum" label="序号"></el-table-column>
      <el-table-column prop="type" label="栋数类型">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.type == '0'" type="success" size="small">普通房</el-tag>
          <el-tag v-if="scope.row.type == '1'" type="danger" size="small">电梯房</el-tag>
        </template>
      </el-table-column>
      <el-table-column width="180" label="操作">
        <template slot-scope="scope">
          <el-button
            v-if="hasPerm('sys:houseBuilding:edit')"
            icon="el-icon-edit"
            type="primary"
            @click="editBtn(scope.row)"
            size="small"
            >编辑</el-button
          >
          <el-button
            v-if="hasPerm('sys:houseBuilding:delete')"
            icon="el-icon-delete"
            type="danger"
            @click="deleteBtn(scope.row)"
            size="small"
            >删除</el-button
          >
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页 -->
    <el-pagination
      @size-change="sizeChange"
      @current-change="currentChange"
      :current-page.sync="parm.currentPage"
      :page-sizes="[10, 20, 40, 80, 100]"
      :page-size="parm.pageSize"
      layout="total, sizes, prev, pager, next, jumper"
      :total="parm.total"
      background
    >
    </el-pagination>
    <!-- 新增栋数弹框 -->
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
          :model="addModle"
          ref="addRef"
          :rules="rules"
          label-width="80px"
          :inline="true"
          size="small"
        >
          <el-form-item prop="name" label="栋数名称">
            <el-input v-model="addModle.name"></el-input>
          </el-form-item>
          <el-form-item prop="type" label="栋数类型">
            <el-select v-model="addModle.type">
              <el-option
                v-for="item in options"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              >
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="栋数序号">
            <el-input type="number" v-model="addModle.orderNum"></el-input>
          </el-form-item>
        </el-form>
      </template>
    </sys-dialog>
  </el-main>
</template>

<script>
import { getListApi, addApi, editApi, deleteApi } from "@/api/building";
import SysDialog from "@/components/system/SysDialog.vue";
export default {
  components: { SysDialog },
  data() {
    return {
      //表单验证规则
      rules: {
        name: [
          {
            trigger: "change",
            required: true,
            message: "请填写栋数名称",
          },
        ],
        type: [
          {
            trigger: "change",
            required: true,
            message: "请选择栋数类型",
          },
        ],
      },
      //新增或编辑表单数据
      addModle: {
        buildId: "",
        editType: "", //0新增 1：编辑
        type: "", //0：普通房 1：电梯房
        name: "",
        orderNum: "",
      },
      //定义弹框属性
      addDialog: {
        title: "",
        height: 200,
        width: 630,
        visible: false,
      },
      //表格高度
      tableHeight: 0,
      options: [
        {
          value: "0",
          label: "普通房",
        },
        {
          value: "1",
          label: "电梯房",
        },
      ],
      //存放列表的数据
      tableList: [],
      //获取列表的参数
      parm: {
        name: "",
        type: "",
        pageSize: 10,
        currentPage: 1,
        total: 0,
      },
    };
  },
  created() {
    this.getList();
  },
  mounted() {
    this.$nextTick(() => {
      this.tableHeight = window.innerHeight - 220;
    });
  },
  methods: {
    //新增或编辑确认事件
    onConfirm() {
      this.$refs.addRef.validate(async (valid) => {
        if (valid) {
          let res = null;
          if (this.addModle.editType == "0") {
            res = await addApi(this.addModle);
          } else {
            res = await editApi(this.addModle);
          }
          if (res && res.code == 200) {
            //提示信息
            this.$message.success(res.msg);
            //关闭弹框、
            this.addDialog.visible = false;
            //刷新表格
            this.getList();
          }
        }
      });
      // this.addDialog.visible = false;
    },
    //新增或编辑关闭
    onClose() {
      this.addDialog.visible = false;
    },
    //新增按钮
    addBtn() {
      //清空表单
      this.$resetForm("addRef", this.addModle);
      //设置弹框属性
      this.addModle.editType = "0"; //标识新增
      this.addDialog.title = "新增栋数";
      this.addDialog.visible = true;
    },
    //重置按钮
    cancelBtn() {
      this.parm.name = "";
      this.parm.type = "";
      this.getList();
    },
    //页容量改变时触发
    sizeChange(val) {
      this.parms.pageSize = val;
      this.getList();
    },
    //页数改变时触发
    currentChange(val) {
      this.parm.currentPage = val;
      this.getList();
    },
    //删除按钮
    async deleteBtn(row) {
      console.log(row);
      //先提示，不能直接删除
      const confirm = await this.$myconfirm("确定删除该数据吗？");
      if (confirm) {
        let res = await deleteApi({ buildId: row.buildId });
        if (res && res.code == 200) {
          //提示信息
          this.$message.success(res.msg);
          //刷新表格
          this.getList();
        }
      }
    },
    //编辑按钮
    editBtn(row) {
      //清空表单
      this.$resetForm("addRef", this.addModle);
      //设置编辑状态
      this.addModle.editType = "1";
      //设置弹框属性
      this.addDialog.title = "编辑栋数";
      this.addDialog.visible = true;
      //设置需要编辑的数据
      this.$objCoppy(row, this.addModle);
    },
    //搜索按钮
    searchBtn() {
      console.log(this.parm);
      this.getList();
    },
    async getList() {
      let res = await getListApi(this.parm);
      console.log(res);
      if (res && res.code == 200) {
        this.tableList = res.data.records;
        this.parm.total = res.data.total;
      }
    },
  },
};
</script>

<style lang="scss" scoped></style>