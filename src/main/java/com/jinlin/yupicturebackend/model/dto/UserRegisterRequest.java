package com.jinlin.yupicturebackend.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求
 */
//implements Serializable 可能需要再网络中传输，所以需要实现Serializable接口，更好的序列化
@Data
public class UserRegisterRequest implements Serializable {
    //作用：对象序列化到磁盘或网络时，保证数据的一致性，需要用id来进行标识
    private static final long serialVersionUID = 4048560400522372501L;

    /**
     * 用户账号
     */
    private final String userAccount;
    /**
     * 用户密码
     */
    private final String userPassword;
    /**
     * 确认密码
     */
    private final String checkPassword;

}
