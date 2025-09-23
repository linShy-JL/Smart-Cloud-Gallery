package com.jinlin.yupicturebackend.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jinlin.yupicturebackend.constant.UserConstant;
import com.jinlin.yupicturebackend.exception.BusinessException;
import com.jinlin.yupicturebackend.exception.ErrorCode;
import com.jinlin.yupicturebackend.model.entity.User;
import com.jinlin.yupicturebackend.model.enums.UserRoleEnum;
import com.jinlin.yupicturebackend.model.vo.LoginUserVO;
import com.jinlin.yupicturebackend.service.UserService;
import com.jinlin.yupicturebackend.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;

import static com.jinlin.yupicturebackend.constant.UserConstant.USER_LOGIN_STATE;

/**
* @author Lenovo
* @description 针对表【user(用户)】的数据库操作Service实现
* @createDate 2025-09-22 20:07:17
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService{
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
     * 用户登录
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @return
     */
    @Override
    public LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        //1.校验参数
        if(StrUtil.hasBlank(userAccount,userPassword)){
            throw  new BusinessException(ErrorCode.PARAMS_ERROR,"参数为空");
        }
        if(userAccount.length()<4){
            throw  new BusinessException(ErrorCode.PARAMS_ERROR,"用户账户错误");
        }
        if(userPassword.length()<8){
            throw  new BusinessException(ErrorCode.PARAMS_ERROR,"用户密码错误");
        }
        //2.对用户传递的密码进行加密
        String encryptPassword = getEncryptPassword(userPassword);
        //3.查询用户是否在数据库中
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount",userAccount);
        queryWrapper.eq("userPassword",encryptPassword);
        User user = this.getOne(queryWrapper);
        if(user==null){
            log.info("user login failed ,Wrong account or password");
            throw  new BusinessException(ErrorCode.PARAMS_ERROR,"用户不存在或密码错误");
        }
        //4.保存用户的状态
        request.getSession().setAttribute(UserConstant.USER_LOGIN_STATE,user);
        return this.getLoginUserVO(user);
    }

    /**
     * 获取加密后的密码
     * @param userPassword  用户密码
     * @return  返回加密后的用户密码
     */
    @Override
    public String getEncryptPassword(String userPassword){
        //加盐，混淆密码
        final String SLAT="jinlin";
        return DigestUtils.md5DigestAsHex((userPassword+SLAT).getBytes());
    }
    /**
     * 获取脱敏类的用户信息
     * @param user  用户
     * @return   返回脱敏后的用户信息
     */
    @Override
    public LoginUserVO getLoginUserVO(User user) {
        if(user==null){
            return null;
        }
        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtil.copyProperties(user,loginUserVO);
        return loginUserVO;
    }
}




