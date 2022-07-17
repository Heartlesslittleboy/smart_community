import http from '@/utils/http'
//获取列表
export async function getListApi(parm){
    return http.get("/api/houseUnit/list",parm)
}
//获取栋数列表
export async function unitListApi(){
    return await http.get("/api/houseBuild/unitList",null);
}
//新增
export async function addApi(parm){
    return await http.post("/api/houseUnit",parm)
}
//编辑
export async function editApi(parm){
    return await http.put("/api/houseUnit",parm)
}
//删除
export async function deleteApi(parm){
    return await http.delete("/api/houseUnit",parm)
}