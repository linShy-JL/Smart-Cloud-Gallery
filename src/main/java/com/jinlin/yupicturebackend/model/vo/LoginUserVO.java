package com.jinlin.yupicturebackend.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 已登录用户视图（脱敏）
 * @TableName user
 */
@Data
public class LoginUserVO {
    /**
     * id
     */
    //type = IdType.ASSIGN_ID   由MybatisPlus自动生成生成一个长整型的id
    //@TableId(type = IdType.AUTO)  是根据数据中的id字段进行自增生成0/1/2/3/
    private Long id;

    /**
     * 账号
     */
    private String userAccount;
    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 用户简介
     */
    private String userProfile;

    /**
     * 用户角色：user/admin
     */
    private String userRole;

    /**
     * 编辑时间
     */
    private Date editTime;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    private static final long serialVersionUID = 1L;
}