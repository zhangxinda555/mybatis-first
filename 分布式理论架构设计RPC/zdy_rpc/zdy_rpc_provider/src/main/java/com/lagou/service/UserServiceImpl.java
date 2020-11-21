package com.lagou.service;

import com.lagou.annotation.Service;


@Service
public class UserServiceImpl implements UserService {

    public String sayHello(String word) {
        System.out.println("调用成功--参数 "+word);
        return "调用成功--参数 "+word;
    }

    public String getData(Object o,String s){
        System.out.println("调用成功:success"+"++++++参数："+o.toString()+","+s);
        return "success";
    }

}
