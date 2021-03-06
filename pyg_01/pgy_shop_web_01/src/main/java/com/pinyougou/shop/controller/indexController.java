package com.pinyougou.shop.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/index")
public class indexController {

    @RequestMapping("/findLoginUser")
    public Map<String,String> findLoginUser(){
        //从springsecurity中获取用户信息
        String name = SecurityContextHolder.getContext().getAuthentication().getName();

        //将用户封装到key为username的map中.返回
        Map map=new HashMap<>();
        map.put("username", name);
        return map;
    }

}
