import http from '@/utils/http'
//获取我的列表
export async function getMyListApi(parm){
    return await http.get("/api/userRepair/myList",parm)
}
//获取维修列表
export async function getListApi(parm){
    return await http.get("/api/userRepair/list",parm)
}
//新增维修
export async function addApi(parm){
    return await http.post("/api/userRepair",parm)
}
//编辑
export async function editApi(parm){
    return await http.put("/api/userRepair",parm)
}
//删除
export async function deleteApi(parm){
    return await http.delete("/api/userRepair",parm)
}