package com.zqq.config.security.customerdetailservice;

import com.zqq.config.security.exception.CustomerAuthenionException;
import com.zqq.web.live_user.entity.LiveUser;
import com.zqq.web.live_user.service.LiveUserService;
import com.zqq.web.menu.entity.Menu;
import com.zqq.web.menu.service.MenuService;
import com.zqq.web.user.entity.User;
import com.zqq.web.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 自定义UserDetailsService
 * 目的：告诉spring security如何查询用户信息
 */
@Component("custometUserDetailService") //把该类交给spring进行管理
public class CustomerUserDetailService implements UserDetailsService {
    @Autowired
    private UserService userService;
    @Autowired
    private LiveUserService liveUserService;
    @Autowired
    private MenuService menuService;
    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        System.out.println("loadUserByUsername");
        //获取用户类型  //0：业主  1： 物主
        //  s = "admin:1"
        int index = s.indexOf(":");
        //账户
        String username = s.substring(0,index);
        //用户类型
        String userType = s.substring(index+1,s.length());
//        UserDetails user = null;
        if(userType.equals("0")){ //业主
            LiveUser liveUser = liveUserService.loadUser(username);
            if(liveUser == null){
                throw new UsernameNotFoundException("用户账户不存在！");
            }
            if(liveUser.getStatus().equals("1")){
                throw new CustomerAuthenionException("账户被禁用，请联系管理员!");
            }
            //查询业主的权限信息
            List<Menu> menuList = menuService.getMenuByUserIdForLiveUser(liveUser.getUserId());
            //获取权限字段
            List<String> collect = menuList.stream().filter(item -> item != null).map(item -> item.getMenuCode()).filter(item -> item != null).collect(Collectors.toList());
            //转成数组
            String[] strings = collect.toArray(new String[collect.size()]);
            List<GrantedAuthority> authorityList = AuthorityUtils.createAuthorityList(strings);
            //设置用户的权限
            liveUser.setAuthorities(authorityList);
            return liveUser;
        }else if(userType.equals("1")){ //物业
            User user = userService.loadUser(username);

            if(user == null){
                throw new UsernameNotFoundException("用户账户不存在！");
            }
            if(user.getIsUsed().equals("1")){
                throw new CustomerAuthenionException("账户被禁用，请联系管理员!");
            }
            if (user.getStatus().equals("1")) {
                throw new CustomerAuthenionException("用户已离职，请联系管理员!");
            }
            //查询用户权限信息
            List<Menu> menuList = menuService.getMenuByUserId(user.getUserId());
            //获取权限字段
            List<String> collect = menuList.stream().filter(item -> item != null).map(item -> item.getMenuCode()).filter(item -> item != null).collect(Collectors.toList());

            //转成数组
            String[] strings = collect.toArray(new String[collect.size()]);
            List<GrantedAuthority> authorityList = AuthorityUtils.createAuthorityList(strings);

            //设置用户的权限
            user.setAuthorities(authorityList);
            return user;
        }else{
            throw new UsernameNotFoundException("用户类型不存在");
        }
    }
}

