package com.cj.rpc.comsumer.controller;


import com.cj.rpc.api.IUserService;
import com.cj.rpc.comsumer.anno.RpcReference;
import com.cj.rpc.pojo.User;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    @RpcReference
    IUserService userService;

    @RequestMapping("/getUserById")
    public User getUserById(int id){
        return userService.getById(id);
    }

}
