<template>
    <el-main>
        <!-- Main content -->
        <el-form :model="parms" ref="searchForm" label-width="80px" :inline="true" size="normal">
            <el-form-item label="姓名">
                <el-input v-model="parms.loginName"></el-input>
            </el-form-item>
            <el-form-item label="电话">
                <el-input v-model="parms.phone"></el-input>
            </el-form-item>
            <el-button @click="searchBtn" icon="el-icon-search">查询</el-button>
            <el-button 
              @click="resetBtn" 
              style="color:#FF7670" 
              icon="el-icon-delete"
              >重置</el-button>
            <el-button 
              v-if="hasPerm('sys:user:add')"
              type="primary" 
              @click="addUser" 
              icon="el-icon-plus"
              >新增</el-button>
        </el-form>
        <!-- 员工表格 -->
        <el-table :height="tableHeight" :data="tableList" empty-text="暂无数据" border stripe>
            <el-table-column prop="loginName" label="姓名"></el-table-column>
            <el-table-column prop="phone" label="电话"></el-table-column>
            <el-table-column prop="idCard" label="身份证"></el-table-column>
            <el-table-column align="center" prop="sex" label="性别">
                <template slot-scope="scope">
                    <el-tag v-if="scope.row.sex == '1'" size="normal">男</el-tag>
                    <el-tag v-if="scope.row.sex == '0'" type="success" size="normal">女</el-tag>
                </template>    
            </el-table-column> 
            <el-table-column align="center" prop="status" label="是否离职">
                <template slot-scope="scope">
                    <el-switch
                        v-model="scope.row.status"
                        :active-value="'1'"
                        :inactive-value="'0'"
                        active-text="是"
                        inactive-text="否"
                        @change="changeStatus(scope.row)"
                    >
                    </el-switch>
                </template>
            </el-table-column>
            <el-table-column align="center" prop="isUsed" label="是否启用">
                <template slot-scope="scope">
                    <el-switch
                        v-model="scope.row.isUsed"
                        :active-value="'0'"
                        :inactive-value="'1'"
                        active-text="是"
                        inactive-text="否"
                        @change="changeUsed(scope.row)"
                    >
                    </el-switch>
                </template>
            </el-table-column>
            <el-table-column align="center" width="290" label="操作">
                <template slot-scope="scope">
                    <el-button
                        v-if="hasPerm('sys:user:edit')"
                        type="primary"
                        icon="el-icon-edit"
                        size="small"
                        @click="editUser(scope.row)"
                    >编辑</el-button>
                    <el-button
                        v-if="hasPerm('sys:user:assignRole')"
                        type="primary"
                        icon="el-icon-edit"
                        size="small"
                        @click="assignRole(scope.row)"
                    >分配角色</el-button>
                    <el-button
                        v-if="hasPerm('sys:user:delete')"
                        type="danger"
                        icon="el-icon-delete"
                        size="small"
                        @click="deleteUser(scope.row)"
                        >删除</el-button>
                </template>
            </el-table-column>
        </el-table>
        
        <!-- 分页
        size-change 页容量改变时触发事件
        current-change页数改变时触发事件
        current-page.sync当前页数
        page-size每页几条
         -->
        <el-pagination 
            @size-change="sizeChange" 
            @current-change="currentChange" 
            :current-page.sync="parms.curentPage" 
            :page-sizes="[10, 20, 40, 80, 100]" 
            layout="total, sizes, prev, pager, next, jumper"
            :page-size="parms.pageSize" 
            :total="parms.total" 
            background>
        </el-pagination>
        <!-- 新增或编辑弹窗 -->
        <sys-dialog
        :title="dialog.title"
        :visible="dialog.visible"
        :height="dialog.height"
        :width="dialog.width"
        @onClose='onClose'
        @onConfirm='onConfirm'
        >
        <div slot="content">
            <el-form
            :model="addModel"
            ref="addForm"
            :rules="rules"
            label-width="80px"
            :inline="true"
            size="small"
            >
                <el-form-item style="width:280px;" prop="loginName" label="姓名:">
                    <el-input v-model="addModel.loginName"></el-input>
                </el-form-item>
                <el-form-item style="width:280px;" prop="sex" label="性别:">
                    <el-radio-group  v-model="addModel.sex">
                        <el-radio :label="'1'">男</el-radio>
                        <el-radio :label="'0'">女</el-radio>
                    </el-radio-group>
                </el-form-item>
                <el-form-item style="width:280px;" prop="phone" label="电话:">
                    <el-input v-model="addModel.phone"></el-input>
                </el-form-item>
                <el-form-item style="width:280px;" prop="idCard"  label="身份证:">
                    <el-input v-model="addModel.idCard"></el-input>
                </el-form-item>
                <el-form-item style="width:280px;" prop="username" label="登录名:">
                    <el-input v-model="addModel.username"></el-input>
                </el-form-item>
                <el-form-item style="width:280px;" prop="password" label="密码:">
                    <el-input type="password" v-model="addModel.password"></el-input>
                </el-form-item>
                <el-form-item style="width:280px;" prop="status" label="离职:">
                    <el-radio-group v-model="addModel.status">
                        <el-radio :label="'1'">是</el-radio>
                        <el-radio :label="'0'">否</el-radio>
                    </el-radio-group>
                </el-form-item>
                <el-form-item style="width:280px;" prop="isUsed" label="启用:">
                    <el-radio-group  v-model="addModel.isUsed">
                        <el-radio :label="'0'">是</el-radio>
                        <el-radio :label="'1'">否</el-radio>
                    </el-radio-group>
                </el-form-item>
            </el-form>
        </div>
        </sys-dialog>
        <sys-dialog
            :title="roleDialog.title"
            :height="roleDialog.height"
            :width="roleDialog.width"
            :visible="roleDialog.visible"
            @onClose="roleClose"
            @onConfirm="roleConfirm"
        >
            <template slot="content">
                <el-table :height="roleHeight" :data="roleList" border stripe>
                    <el-table-column width="50" align="center" label="选择">
                        <template slot-scope="scope">
                            <el-radio v-model="radio" :label="scope.row.roleId" @change="getCurrentRow(scope.row)">
                                {{""}}
                            </el-radio>
                        </template>
                    </el-table-column>
                    <el-table-column prop="roleName" label="角色名称"></el-table-column>
                    <el-table-column prop="remark" label="角色备注"></el-table-column>
                </el-table>
                <el-pagination
                    @size-change="roleSizeChange"
                    @current-change="roleCurrentChange"
                    :current-page.sync="roleParm.currentPage"
                    :page-sizes="[10, 20, 40, 80, 100]"
                    :page-size="roleParm.pageSize"
                    layout="total, sizes, prev, pager, next, jumper"
                    :total="roleParm.total"
                    background
                >
                </el-pagination>
            </template>
        </sys-dialog>
    </el-main>
</template>
<script>
import { addUserApi, getUserListApi, editUserApi, deleteUserApi, getRoleByUserId, assignSave } from "@/api/user";
import SysDialog from '@/components/system/SysDialog.vue'
import {getRoleListApi} from '@/api/role'
export default {
    //组件在使用之气需要注册
    components: {
        SysDialog
    },
    //所有需要在页面上展示的数据，都需要显示的在data里面进行定义
    data() {
        return {
            //分配角色保存参数
            assignParm:{
                roleId:'',
                userId:''
            },
            radio:'',
            //角色列表高度
            roleHeight:0,
            //角色列表
            roleList:[],
            //角色列表分页
            roleParm:{
                pageSize:10,
                currentPage:1,
                total:0
            },
            //分配角色弹框
            roleDialog: {
                title: "",
                height: 400,
                width: 800,
                visible: false,
            },
            //表单验证规则
            rules:{
                loginName: [{
                    required: true,
                    trigger: "change",
                    message: "请填写姓名",
                },],
                phone: [{
                    required: true,
                    trigger: "change",
                    message: "请填写电话号码",
                },],
                idCard: [{
                    required: true,
                    trigger: "change",
                    message: "请填写身份证号码",
                },],
                status: [{
                    required: true,
                    trigger: "change",
                    message: "请选择是否离职",
                },],
                isUsed: [{
                    required: true,
                    trigger: "change",
                    message: "请选择是否启用",
                },],
                sex: [{
                    required: true,
                    trigger: "change",
                    message: "请选择性别",
                },]
            },
            //表单属性
            addModel:{
                userId:'',
                type:'', //0： 新增 1： 编辑
                username:'',
                sex:'',
                phone:'',
                idCard:'',
                loginName:'',
                password:'',
                status:'',
                isUsed:''
            },
            //弹窗属性
            dialog: {
                title:"",
                visible:false,
                height:240,
                width:610,
            },
            //表格的高度
            tableHeight: 0,
            //搜索框数据绑定
            parms: { phone: "", loginName: "", pageSize: 10, curentPage: 1, total: 0,},
            //表格数据
            tableList: [],
        };
    },
    created() {
        this.getUserList();
    },
    mounted() { 
        this.$nextTick(
            () => { this.tableHeight = window.innerHeight - 220; }
        ); 
    },
    methods: {
        //角色选中事件
        getCurrentRow(row){
            this.assignParm.roleId = row.roleId
            console.log(row)
        },
        //页数改变
        roleCurrentChange(val){
            this.roleParm.currentPage = val;
            this.getRoleList();
        },
        //页容量改变触发
        roleSizeChange(val){
            this.roleParm.pageSize = val;
            this.getRoleList();
        },
        //分配角色按钮
        async assignRole(row) {
            this.radio = ''
            this.assignParm.userId = row.userId;
            //设置弹框属性
            this.roleDialog.title = '为【'+row.loginName+'】分配角色';
            
            //查询角色列表
            await this.getRoleList();
            console.log("await this.getRoleList()")
            console.log(this.roleList);
            this.roleDialog.visible = true;
            this.$nextTick(()=>{
                this.roleHeight = window.innerHeight-380
            })
            //角色回显
            let roleRes = await getRoleByUserId({userId:row.userId});
            if(roleRes && roleRes.code == 200){
                console.log(roleRes);
                if (roleRes.data) {
                    this.radio = roleRes.data.roleId;
                }
            }
        },
        async getRoleList(){
            let res = await getRoleListApi(this.roleParm);
            console.log("sysRoleList::getRoleList");
            if(res.code == 200){
                // console.log(res.data.records);
                // let role = res.data.records;
                // let arr = new Array();
                // for (var i = 0; i < role.length; i++) {
                //     if (role[i].roleName != "业主") {
                //         arr.push(role[i]);
                //     }
                // }
                // console.log(arr)
                this.roleList = res.data.records;
                this.roleParm.total = res.data.total;
            }
        },
        //分配角色确认
        async roleConfirm() {
            if(!this.radio){
                this.$message.warning('请选择角色!');
                return;
            }
            let res = await assignSave(this.assignParm);
            if(res && res.code == 200){
                this.$message.success(res.msg);
                this.roleDialog.visible = false;
            }
        },
        //分配角色取消
        roleClose() {
            this.roleDialog.visible = false;
        }, 

        //重置按钮
        resetBtn() {
            this.parms.loginName='';
            this.parms.phone='';
            this.getUserList();
        },
        //搜索按钮
        searchBtn() {
            this.getUserList();
        },
        //对话框确认
        onConfirm() {
            this.$refs.addForm.validate(async (valid) => {
                if (valid) {
                    let res = null;
                    if (this.addModel.type == '0') {
                        res = await addUserApi(this.addModel);
                    } else {
                        res = await editUserApi(this.addModel);
                    }
                    //请求成功，刷新用户列表
                    if (res && res.code == 200) {
                        this.getUserList();
                        this.dialog.visible = false;
                        this.$message.success(res.msg);
                    }
                    console.log("onConfirm");
                    
                }
            })
        },
        //对话框关闭
        onClose() {
            this.dialog.visible = false;
        },
        //删除
        async deleteUser(row) {
            console.log("delete")
            console.log(row);
            let confrim = await this.$myconfirm("确定删除该员工吗？");
            console.log(confrim);
            if (confrim) {

                let res = await deleteUserApi({userId:row.userId});
                if (res && res.code == 200) {
                    this.getUserList();
                    this.$message.success(res.msg);
                }
            }
        },
        //新增按钮事件
        addUser() {
            //清空表单
            this.$resetForm('addForm', this.addModel);
            this.addModel.type=0;
            this.dialog.title="新增员工";
            this.dialog.visible=true;
        },
        //编辑
        editUser(row) {
            console.log(row);
            //清空表单
            this.$resetForm('addForm', this.addModel);
            //设置是编辑还是新增
            this.addModel.type='1';
            //把当前编辑的数据回显到表单
            this.$objCoppy(row, this.addModel);
            this.dialog.visible=true;
        },
        async getUserList(){
            //console.log("1");
            let res = await getUserListApi(this.parms);
            //console.log("2");
            if (res.code == 200) {
                this.tableList = res.data.records;
                this.parms.total = res.data.total;
                
            }
            console.log(res);
        },
        //表格是否启用点击事件
        async changeUsed(row) {
            console.log(row);
            let parm = {
                userId:row.userId,
                isUsed:row.isUsed
            }
            let res = await editUserApi(parm);
            if (res && res.code == 200) {
                this.getUserList();
                this.$message.success(res.msg);
            }
        },
        //表格是否离职点击事件
        async changeStatus(row) {
            console.log(row);
            let parm = {
                userId:row.userId,
                status:row.status
            }
            let res = await editUserApi(parm);
            if (res && res.code == 200) {
                this.getUserList();
                this.$message.success(res.msg);
            }
        },
        //页容量改变的时候触发 
        sizeChange(val) {
            console.log(val);
            this.parms.pageSize=val;
            this.getUserList();
        },
        //页数改变的时候触发 
        currentChange(val) {
            console.log(val);
            this.parms.curentPage=val;
            this.getUserList();
        },
    },
};
</script>

<style lang="scss" scoped>

</style>
