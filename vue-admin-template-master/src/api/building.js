import http from '@/utils/http'
//查询列表
export async function getListApi(parm){
    return await http.get("/api/houseBuild/list",parm)
}
//新增
export async function addApi(parm){
    return await http.post("/api/houseBuild",parm)
}
//编辑
export async function editApi(parm){
    return await http.put("/api/houseBuild",parm)
}
//删除
export async function deleteApi(parm){
    return await http.delete("/api/houseBuild",parm)
}