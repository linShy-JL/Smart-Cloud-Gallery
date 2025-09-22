package com.jinlin.yupicturebackend.model.enums;

import cn.hutool.core.util.ObjUtil;
import lombok.Getter;

/**
 * 用户角色枚举
 */
@Getter   //lombok注解，自动生成getter方法
public enum UserRoleEnum {
    USER("普通用户", "user"),
    ADMIN("管理员", "admin");
    private final String text;

    private final String value;

    UserRoleEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 根据value获取枚举
     * @param value
     * @return
     */
    public static UserRoleEnum getEnumByValue(String value) {
        if(ObjUtil.isEmpty(value)){
            return null;
        }
        for (UserRoleEnum userRoleEnum : UserRoleEnum.values()){
            if(userRoleEnum.value.equals(value)){
                return userRoleEnum;
            }
        }
        return null;
    }
}
