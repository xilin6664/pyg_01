package com.pinyougou.shop.sevice;

import com.pinyougou.pojo.TbSeller;
import com.pinyougou.sellergoods.service.SellerService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

//自定义用户安全登陆验证类
public class UserDetailServiceImpl implements UserDetailsService {
    private SellerService sellerService;

    public void setSellerService(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //构建角色列表
        List<GrantedAuthority> grantAuths= new ArrayList<>();
        grantAuths.add(new SimpleGrantedAuthority("ROLE_SELLER"));
        TbSeller seller = sellerService.findOne(username);
        //判断只有商家存在并且商家状态为审核通过  1 才允许登录
        if (seller != null) {
            if ("1".equals(seller.getStatus())) {
                return new User(username, seller.getPassword(), grantAuths);
            }
            return null;
        }
        return null;
    }
}
