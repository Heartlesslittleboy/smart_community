package com.zqq.web.user.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zqq.config.jwt.JwtUtils;
import com.zqq.utils.ResultUtils;
import com.zqq.utils.ResultVo;
import com.zqq.web.live_user.entity.LiveUser;
import com.zqq.web.live_user.service.LiveUserService;
import com.zqq.web.menu.entity.MakeMenuTree;
import com.zqq.web.menu.entity.Menu;
import com.zqq.web.menu.entity.RouterVO;
import com.zqq.web.menu.service.MenuService;
import com.zqq.web.user.entity.*;
import com.zqq.web.user.service.UserService;
import com.zqq.web.user_role.entity.UserRole;
import io.jsonwebtoken.Claims;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 员工管理模块
 */
@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private MenuService menuService;
    @Autowired
    private LiveUserService liveUserService;

    /**
     * 获取菜单列表
     * @param request
     * @return
     */
    @GetMapping("/getMenuList")
    public ResultVo getMenuList(HttpServletRequest request){
        //获取token
        String token = request.getHeader("token");
        //获取用户名
        String username = jwtUtils.getUsernameFromToken(token);
        //获取用户类型
        Claims claims = jwtUtils.getClaimsFromToken(token);
        Object userType = claims.get("userType");
        if(userType.equals("0")){//0：业主
            //获取用户信息
            LiveUser liveUser = liveUserService.loadUser(username);
            //查询业主的权限信息
            List<Menu> menuList = menuService.getMenuByUserIdForLiveUser(liveUser.getUserId());
            List<Menu> collect = menuList.stream().filter(item -> item != null && !item.getType().
                    equals("2")).collect(Collectors.toList());
            //组装路由数据
            List<RouterVO> routerVOS = MakeMenuTree.makeRouter(collect, 0L);
            return ResultUtils.success("查询成功",routerVOS);
        }else{
            //获取用户信息
            User liveUser = userService.loadUser(username);
            //查询业主的权限信息
            List<Menu> menuList = menuService.getMenuByUserId(liveUser.getUserId());
            List<Menu> collect = menuList.stream().filter(item -> item != null && !item.getType().
                    equals("2")).collect(Collectors.toList());
            //组装路由数据
            List<RouterVO> routerVOS = MakeMenuTree.makeRouter(collect, 0L);
            return ResultUtils.success("查询成功",routerVOS);
        }
    }

    /**
     * 获取用户信息
     * @param user
     * @param request
     * @return
     */
    @GetMapping("/getInfo")
    public ResultVo getInfo(User user, HttpServletRequest request){
        //从头部获取token
        String token = request.getHeader("token");
        Claims claims = jwtUtils.getClaimsFromToken(token);
        Object userType = claims.get("userType");
        if(userType.equals("0")){ //0：业主
            LiveUser liveUser = liveUserService.getById(user.getUserId());
            UserInfo userInfo = new UserInfo();
            userInfo.setId(liveUser.getUserId());
            userInfo.setName(liveUser.getUsername());
            userInfo.setAvatar("https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif");
            //查询业主的权限信息
            List<Menu> menuList = menuService.getMenuByUserIdForLiveUser(liveUser.getUserId());
            //获取权限字段
            List<String> collect = menuList.stream().filter(item -> item != null).map(item -> item.getMenuCode()).filter(item -> item != null).collect(Collectors.toList());
            //转成数组
            String[] strings = collect.toArray(new String[collect.size()]);
            userInfo.setRoles(strings);
            return ResultUtils.success("获取用户信息成功",userInfo);
        }else{ //物主
            //根据用户id查询,区分查的是哪一个
            User user1 = userService.getById(user.getUserId());
            UserInfo userInfo = new UserInfo();
            userInfo.setId(user1.getUserId());
            userInfo.setName(user1.getUsername());
            userInfo.setAvatar("https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif");
            //根据用户id查询权限字段
            //查询用户权限信息
            List<Menu> menuList = menuService.getMenuByUserId(user.getUserId());
            //获取权限字段
            List<String> collect = menuList.stream().filter(item -> item != null).map(item -> item.getMenuCode()).filter(item -> item != null).collect(Collectors.toList());
            //转成数组
            String[] strings = collect.toArray(new String[collect.size()]);
            userInfo.setRoles(strings);
            return ResultUtils.success("获取用户信息成功",userInfo);
        }
    }

    /**
     * 用户登录
     * @param parm
     * @return
     */
    @PostMapping("/login")
    public ResultVo login(@RequestBody LoginParm parm) {
        System.out.println("login");
        if (StringUtils.isEmpty(parm.getUsername()) || StringUtils.isEmpty(parm.getPassword()) || StringUtils.isEmpty(parm.getUserType())) {
            return ResultUtils.error("用户名、密码或用户类型不能为空!");
        }
        String encode = passwordEncoder.encode(parm.getPassword());
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(parm.getUsername() + ":" + parm.getUserType(), parm.getPassword());
        Authentication authenticate = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authenticate);
        System.out.println("******");
        //用户信息
        if (parm.getUserType().equals("0")) { //业主
            LiveUser liveUser = (LiveUser) authenticate.getPrincipal();

            //生成token返回给前端
            String liveToken = jwtUtils.generateToken(liveUser.getUsername(), parm.getUserType());
            //获取token过期的时间
            Long time = jwtUtils.getExpireTime(liveToken, jwtUtils.getSecret());
            LoginResult result = new LoginResult();
            result.setUserId(liveUser.getUserId());
            result.setToken(liveToken);
            result.setExpireTime(time);
            return ResultUtils.success("登录成功", result);
        } else if (parm.getUserType().equals("1")) { //物业
            User user = (User) authenticate.getPrincipal();
            //生成token返回给前端
            String token = jwtUtils.generateToken(user.getUsername(), parm.getUserType());
            //获取token过期的时间
            Long time = jwtUtils.getExpireTime(token, jwtUtils.getSecret());
            LoginResult result = new LoginResult();
            result.setUserId(user.getUserId());
            result.setToken(token);
            result.setExpireTime(time);
            return ResultUtils.success("登录成功", result);
        } else {
            return ResultUtils.error("您选择的用户类型不存在!");
        }
    }

    /**
     * 用户退出
     * @param request
     * @param response
     * @return
     */
    @PostMapping("/loginOut")
    public ResultVo loginOut(HttpServletRequest request, HttpServletResponse response){
        //获取用户相关信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null){
            //清空用户信息
            new SecurityContextLogoutHandler().logout(request,response,authentication);

        }
        return ResultUtils.success("退出登录成功!");
    }

    /**
     * 密码重置
     * @param user
     * @param request
     * @return
     */
    @PostMapping("/resetPassword")
    public ResultVo resetPassword(@RequestBody ChangePassword user, HttpServletRequest request){
        //获取token
        String token = request.getHeader("token");
        Claims claims = jwtUtils.getClaimsFromToken(token);
        //判断用户类型
        Object userType = claims.get("userType");
        if(userType.equals("0")){//0：业主
            LiveUser liveUser = liveUserService.getById(user.getUserId());
            //原来的密码
            String dataOldPassword = liveUser.getPassword();
            boolean encode = passwordEncoder.matches(user.getOldPassword(),dataOldPassword);
            if(!encode){
                return ResultUtils.error("旧密码错误!");
            }
            LiveUser liveUser1 = new LiveUser();
            liveUser1.setUserId(user.getUserId());
            liveUser1.setPassword(passwordEncoder.encode(user.getNewPassword()));
            boolean b = liveUserService.updateById(liveUser1);
            if(b){
                return ResultUtils.success("密码修改成功!");
            }
            return ResultUtils.error("密码修改失败!");
        }else{
            User liveUser = userService.getById(user.getUserId());
            //原来的密码
            String dataOldPassword = liveUser.getPassword();
            boolean encode = passwordEncoder.matches(user.getOldPassword(),dataOldPassword);
            if(!encode){
                return ResultUtils.error("旧密码错误!");
            }
            User liveUser1 = new User();
            liveUser1.setUserId(user.getUserId());
            liveUser1.setPassword(passwordEncoder.encode(user.getNewPassword()));
            boolean b = userService.updateById(liveUser1);
            if(b){
                return ResultUtils.success("密码修改成功!");
            }
            return ResultUtils.error("密码修改失败!");
        }
    }

    /**
     * 新增用户
     * @param user
     * @return
     */
    @PreAuthorize("hasAuthority('sys:user:add')")
    @PostMapping
    public ResultVo addUser(@RequestBody User user) {
        //唯一性判断
        if (StringUtils.isNotEmpty(user.getUsername())) {
            QueryWrapper<User> query = new QueryWrapper<>();
            query.lambda().eq(User::getUsername, user.getUsername());
            User one = userService.getOne(query);
            if (one != null && one.getUserId() != user.getUserId()) {
                return ResultUtils.error("登录名已经被占用", 500);
            }
        }

        if (StringUtils.isNotEmpty(user.getPassword())) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        boolean save = userService.save(user);
        if (save) {
            return ResultUtils.success("新增员工成功");
        }
        return ResultUtils.error("新增员工失败");
    }

    /**
     * 编辑员工信息
     * @param user
     * @return
     */
    @PreAuthorize("hasAuthority('sys:user:edit')")
    @PutMapping
    public ResultVo ediUser(@RequestBody User user) {
        System.out.println("执行ediUser");
        if (StringUtils.isNotEmpty(user.getUsername())) {
            QueryWrapper<User> query = new QueryWrapper<>();
            query.lambda().eq(User::getUsername, user.getUsername());
            User one = userService.getOne(query);
            if (one != null && one.getUserId() != user.getUserId()) {
                return ResultUtils.error("登录名已经被占用", 500);
            }
        }
        if (StringUtils.isNotEmpty(user.getPassword())) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        boolean b = userService.updateById(user);
        if (b) {
            return ResultUtils.success("编辑员工成功");
        }
        return ResultUtils.error("编辑员工失败");
    }

    /**
     * 删除员工信息
     * @param userId
     * @return
     */
    @PreAuthorize("hasAuthority('sys:user:delete')")
    @DeleteMapping("/{userId}")
    public ResultVo deleteUser(@PathVariable("userId") Long userId) {
        boolean b = userService.removeById(userId);
        if (b) {
            return ResultUtils.success("删除员工成功");
        }
        return ResultUtils.error("删除员工失败");
    }

    /**
     * 查询员工列表
     * @param parm
     * @return
     */
    @GetMapping("/list")
    public ResultVo list(UserParm parm) {
        //System.out.println("执行list");
        IPage<User> list = userService.list(parm);
        list.getRecords().stream().forEach(item -> item.setPassword(""));
        return ResultUtils.success("查询成功", list);
    }

    /**
     * 根据id查询员工信息
     * @param userRole
     * @return
     */
    @GetMapping("/getRoleByUserId")
    public ResultVo getRoleByUserId(UserRole userRole) {
        UserRole userRole1 = userService.getRoleByUserId(userRole);
        return ResultUtils.success("查询成功", userRole1);
    }

    /**
     * 为员工分配角色
     * @param userRole
     * @return
     */
    @PreAuthorize("hasAuthority('sys:user:assignRole')")
    @PostMapping("/saveRole")
    public ResultVo saveRole(@RequestBody UserRole userRole) {
        userService.saveRole(userRole);
        return ResultUtils.success("分配角色成功!");
    }

}

