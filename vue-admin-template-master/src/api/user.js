import request from '@/utils/request'
import http from '@/utils/http'
import { getUserId } from '@/utils/auth';
//获取员工列表
export async function getUserListApi(parm) {
  return await http.get("/api/user/list", parm);
}
//新增员工
export async function addUserApi(parm){
  return await http.post("/api/user",parm);
}
//编辑员工
export async function editUserApi(parm){
  return await http.put("/api/user",parm)
}
//删除员工
export async function deleteUserApi(parm){
  return await http.delete("/api/user",parm)
}

//员工登录
export async function login(parm){
  //console.log("1");
  return await http.post("/api/user/login",parm)
}
export async function getInfo() {
  let parm = {
    userId:getUserId()
  }
  return http.get("/api/user/getInfo", parm);
}

//根据用户id查询角色id
export async function getRoleByUserId(parm){
  return await http.get("/api/user/getRoleByUserId",parm);
}
//分配角色保存
export async function assignSave(parm){
  return await http.post("/api/user/saveRole",parm)
}
//获取菜单数据
export async function getMenuList(){
  return await http.get("/api/user/getMenuList",null)
}
// export function getInfo(token) {
//   return request({
//     url: '/vue-admin-template/user/info',
//     method: 'get',
//     params: { token }
//   })
// }

// export function logout() {
//   return request({
//     url: '/vue-admin-template/user/logout',
//     method: 'post'
//   })
// }
//退出登录
export async function loginOutApi(){
  return await http.post("/api/user/loginOut",null)
}
//修改密码
export async function resetPasswordApi(parm){
  return await http.post("/api/user/resetPassword",parm)
}