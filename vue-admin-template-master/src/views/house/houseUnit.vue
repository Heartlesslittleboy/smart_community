<template>
  <el-main>
    <el-form
      :model="parms"
      ref="searchForm"
      label-width="80px"
      :inline="true"
      size="small"
    >
      <el-form-item label="栋数名称:">
        <el-input v-model="parms.buildNme"></el-input>
      </el-form-item>
      <el-form-item label="单元名称:">
        <el-input v-model="parms.unitName"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button icon="el-icon-search" @click="searchBtn">查询</el-button>
        <el-button @click="resetBtn" style="color: #ff7670" icon="el-icon-close"
          >重置</el-button
        >
        <el-button v-if="hasPerm('sys:houseUnit:add')" @click="addBtn" icon="el-icon-plus" type="primary">新增</el-button>
      </el-form-item>
    </el-form>
    <!-- 表格 -->
    <el-table :height="tableHeight" :data="tableList" border stripe>
      <el-table-column prop="name" label="栋数名称"></el-table-column>
      <el-table-column prop="unitName" label="单元名称"></el-table-column>
      <el-table-column width="180" align="center" label="操作">
        <template slot-scope="scope">
          <el-button
            v-if="hasPerm('sys:houseUnit:edit')"
            type="primary"
            icon="el-icon-edit"
            size="small"
            @click="editBtn(scope.row)"
            >编辑</el-button
          >
          <el-button
            v-if="hasPerm('sys:houseUnit:delete')"
            type="danger"
            icon="el-icon-delete"
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
    <!-- 新增或编辑弹框 -->
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
          <el-form-item prop="buildId" label="所属栋数">
            <el-select v-model="addModel.buildId">
              <el-option
                v-for="item in buildTableList"
                :key="item.buildId"
                :label="item.name"
                :value="item.buildId"
              >
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item prop="unitName" label="单元名称">
            <el-input v-model="addModel.unitName"></el-input>
          </el-form-item>
        </el-form>
      </template>
    </sys-dialog>
  </el-main>
</template>

<script>
import { getListApi, addApi, unitListApi,editApi,deleteApi } from "@/api/houseUnit";
import SysDialog from "@/components/system/SysDialog.vue";
export default {
  components: { SysDialog },
  data() {
    return {
      //栋数列表
      buildTableList: [],
      //表单验证规则
      rules: {
        buildId: [
          {
            trigger: "change",
            required: true,
            message: "请选择所属栋数",
          },
        ],
        unitName: [
          {
            trigger: "change",
            required: true,
            message: "请填写单元名称",
          },
        ],
      },
      //新增或编辑数据
      addModel: {
        editType: "", // 0:新增 1：编辑
        unitId: "",
        buildId: "",
        unitName: "",
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
      //列表数据
      tableList: [],
      parms: {
        buildNme: "",
        unitName: "",
        currentPage: 1,
        pageSize: 10,
        total: 0,
      },
    };
  },
  created() {
    this.getList();
    this.getBuildList();
  },
  mounted() {
    this.$nextTick(() => {
      this.tableHeight = window.innerHeight - 210;
    });
  },
  methods: {
    //获取栋数列表
    async getBuildList() {
      let res = await unitListApi();
      if (res && res.code == 200) {
        console.log(res);
        this.buildTableList = res.data;
      }
    },
    //弹框确认
    onConfirm() {
      this.$refs.addForm.validate(async (valid) => {
        if (valid) {
          let res = null;
          if (this.addModel.editType == "0") {
            res = await addApi(this.addModel);
          }else{
            res = await editApi(this.addModel)
          }
          if (res && res.code == 200) {
            //刷新列表
            this.getList();
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
    //删除按钮
    async deleteBtn(row) {
      //信息提示
      const confirm = await this.$myconfirm('确定删除该数据吗?');
      if(confirm){
        let res = await deleteApi({unitId:row.unitId});
        if(res && res.code == 200){
          //刷新表格
          this.getList();
          this.$message.success(res.msg)
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
      this.addDialog.title = '编辑单元';
      this.addDialog.visible = true;
      //把当前需要编辑的数据放到数据域
      this.$objCoppy(row,this.addModel);
    },
    //新增按钮
    addBtn() {
      //清空表单
      this.$resetForm("addForm", this.addModel);
      //设置编辑属性
      this.addModel.editType = "0";
      //设置弹框属性
      this.addDialog.title = "新增单元";
      this.addDialog.visible = true;
    },
    //重置按钮
    resetBtn() {
      this.parms.buildNme = "";
      this.parms.unitName = "";
      this.getList();
    },
    //搜索按钮
    searchBtn() {
      this.getList();
    },
    //获取列表
    async getList() {
      let res = await getListApi(this.parms);
      if (res && res.code == 200) {
        this.tableList = res.data.records;
        this.parms.total = res.data.total;
      }
      console.log(res);
    },
  },
};
</script>

<style lang="scss" scoped></style>
