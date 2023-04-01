package com.cj.rpc.api;

import com.cj.rpc.pojo.User;

/**
 * 用户服务
 */
public interface IUserService {

    /**
     * 根据用户id查询
     */
    User getById(int id);


}
