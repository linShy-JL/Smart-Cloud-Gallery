package com.jinlin.yupicturebackend.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jinlin.yupicturebackend.exception.BusinessException;
import com.jinlin.yupicturebackend.exception.ErrorCode;
import com.jinlin.yupicturebackend.model.entity.User;
import com.jinlin.yupicturebackend.model.enums.UserRoleEnum;
import com.jinlin.yupicturebackend.service.UserService;
import com.jinlin.yupicturebackend.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

/**
* @author Lenovo
* @description 针对表【user(用户)】的数据库操作Service实现
* @createDate 2025-09-22 20:07:17
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{
    /**
     * 用户注册
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @return
     */
    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        //1.校验参数
        if(StrUtil.hasBlank(userAccount,userPassword,checkPassword)){
            throw  new BusinessException(ErrorCode.PARAMS_ERROR,"参数为空");
        }
        if(userAccount.length()<4){
            throw  new BusinessException(ErrorCode.PARAMS_ERROR,"用户账户过短");
        }
        if(userPassword.length()<8 || checkPassword.length()<8){
            throw  new BusinessException(ErrorCode.PARAMS_ERROR,"用户密码过短");
        }
        if(!userPassword.equals(checkPassword)){
            throw  new BusinessException(ErrorCode.PARAMS_ERROR,"两次输入的密码不一致");
        }
        //2.检查用户账户在数据库中是否有重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount",userAccount);
        long count = this.count(queryWrapper);
        if(count>0){
            throw  new BusinessException(ErrorCode.PARAMS_ERROR,"用户账户重复");
        }
        //3.密码一定要加密
        String encryptPassword = getEncryptPassword(userPassword);
        //4.存入数据库当中
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setUserName("无名");
        user.setUserRole(UserRoleEnum.USER.getValue());
        boolean isSave = this.save(user);
        if(!isSave){
            throw  new BusinessException(ErrorCode.SYSTEM_ERROR,"注册失败，数据库错误");
        }
        //id mybatisplus已经帮我们做了，主键回填
        return user.getId();
    }

    /**
     * 获取加密后的密码
     * @param userPassword
     * @return
     */
    @Override
    public String getEncryptPassword(String userPassword){
        //加盐，混淆密码
        final String SLAT="jinlin";
        return DigestUtils.md5DigestAsHex((userPassword+SLAT).getBytes());
    }
}




