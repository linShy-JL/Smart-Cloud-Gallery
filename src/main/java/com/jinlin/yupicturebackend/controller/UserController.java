package com.jinlin.yupicturebackend.controller;

import com.jinlin.yupicturebackend.annotation.AuthCheck;
import com.jinlin.yupicturebackend.common.BaseResponse;
import com.jinlin.yupicturebackend.common.ResultUtils;
import com.jinlin.yupicturebackend.constant.UserConstant;
import com.jinlin.yupicturebackend.exception.ErrorCode;
import com.jinlin.yupicturebackend.exception.ThrowUtils;
import com.jinlin.yupicturebackend.model.dto.UserLoginRequest;
import com.jinlin.yupicturebackend.model.dto.UserRegisterRequest;
import com.jinlin.yupicturebackend.model.entity.User;
import com.jinlin.yupicturebackend.model.enums.UserRoleEnum;
import com.jinlin.yupicturebackend.model.vo.LoginUserVO;
import com.jinlin.yupicturebackend.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;
    /**
     * 用户注册
     */
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest){
        ThrowUtils.throwIf(userRegisterRequest==null, ErrorCode.PARAMS_ERROR);
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        long result = userService.userRegister(userAccount, userPassword, checkPassword);
        return ResultUtils.success(result);
    }
    /**
     * 用户登录
     */
    @PostMapping("/login")
    public BaseResponse<LoginUserVO> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(userLoginRequest == null, ErrorCode.PARAMS_ERROR);
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        LoginUserVO loginUserVO = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(loginUserVO);
    }
    /**
     * 获取当前登录用户
     */
    @GetMapping("/get/login")
    public BaseResponse<LoginUserVO> getLoginUser(HttpServletRequest request) {
        User loginUserVO = userService.getLoginUser(request);
        //对得到的数据进行一个封装，返回一个脱敏后的用户信息
        return ResultUtils.success(userService.getLoginUserVO(loginUserVO));
    }
    /**
     * 用户注销（退出登录）
     * @param request
     */
    @PostMapping("/logout")
    public BaseResponse<Boolean> userLogOut(HttpServletRequest request) {
        //1.判断request是否为空
        ThrowUtils.throwIf(request == null, ErrorCode.PARAMS_ERROR);
        boolean result = userService.userLogout(request);
        return ResultUtils.success(result);
    }
}
