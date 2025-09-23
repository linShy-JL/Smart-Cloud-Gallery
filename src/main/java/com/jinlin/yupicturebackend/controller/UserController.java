package com.jinlin.yupicturebackend.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jinlin.yupicturebackend.annotation.AuthCheck;
import com.jinlin.yupicturebackend.common.BaseResponse;
import com.jinlin.yupicturebackend.common.DeleteRequest;
import com.jinlin.yupicturebackend.common.ResultUtils;
import com.jinlin.yupicturebackend.constant.UserConstant;
import com.jinlin.yupicturebackend.exception.BusinessException;
import com.jinlin.yupicturebackend.exception.ErrorCode;
import com.jinlin.yupicturebackend.exception.ThrowUtils;
import com.jinlin.yupicturebackend.model.dto.user.*;
import com.jinlin.yupicturebackend.model.entity.User;
import com.jinlin.yupicturebackend.model.enums.UserRoleEnum;
import com.jinlin.yupicturebackend.model.vo.LoginUserVO;
import com.jinlin.yupicturebackend.model.vo.UserVO;
import com.jinlin.yupicturebackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;
    @Autowired
    private ResourcePatternResolver resourcePatternResolver;

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
    /**
     * 添加用户(仅管理员可用)
     * @param userAddRequest
     * @return
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addUser(@RequestBody UserAddRequest userAddRequest){
        ThrowUtils.throwIf(userAddRequest==null, ErrorCode.PARAMS_ERROR);
        User user = new User();
        BeanUtil.copyProperties(userAddRequest, user);
        //填充一些默认值 密码
        final String DEFAULT_PASSWORD="12345678";
        String encryptPassword = userService.getEncryptPassword(DEFAULT_PASSWORD);
        user.setUserPassword(encryptPassword);
        //将新增的用户添加到数据库中
        boolean result = userService.save(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(user.getId());
    }

    /**
     * 根据id删除用户(仅管理员可用)
     * @param deleteRequest
     * @return
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteUser(@RequestBody DeleteRequest deleteRequest) {
        //判断参数是否为空和传入的id是否合法
        if(deleteRequest==null || deleteRequest.getId()<=0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long useId = deleteRequest.getId();
        boolean result = userService.removeById(useId);
        return ResultUtils.success(result);
    }
    /**
     * 更新用户(仅管理员可用)
     * @param userUpdateRequest   更新对象请求
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateRequest userUpdateRequest) {
        //判断请求是否为空
        if(userUpdateRequest==null || userUpdateRequest.getId()== null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //new一个user对象
        User user = new User();
        //把userUpdateRequest对象复制给user对象
        BeanUtil.copyProperties(userUpdateRequest, user);
        boolean result = userService.updateById(user);
        return ResultUtils.success(result);
    }
    /**
     * 根据id查询用户(仅管理员可用)
     * @return
     */
    @GetMapping("/get")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<User> getUserById(long id) {
        //1.判断id是否合法
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        //2.根据id查询用户
        User user = userService.getById(id);
        //3.判断用户是否存在
        ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(user);
    }
    /**
     * 根据id查询用户列表(仅管理员可用)
     * @return
     */
    @PostMapping("/list/page/VO")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<UserVO>> listUserByPage(@RequestBody UserQueryRequest userQueryRequest) {
        //1.判断请求参数是否为空
        ThrowUtils.throwIf(userQueryRequest == null, ErrorCode.PARAMS_ERROR);
        long current = userQueryRequest.getCurrent();
        long pageSize = userQueryRequest.getPageSize();
        Page<User> userPage = userService.page(new Page<>(current, pageSize),
                userService.getQueryWrapper(userQueryRequest));
        Page<UserVO> userVOPage = new Page<>(current, pageSize, userPage.getTotal());
        List<UserVO> userVOList = userService.getUserVOList(userPage.getRecords());
        userVOPage.setRecords(userVOList);
        return ResultUtils.success(userVOPage);
    }
    /**
     * 根据id获取包装类
     * @return
     */
    @GetMapping("/get/VO")
    public BaseResponse<UserVO> getUserVOById(long id) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        BaseResponse<User> response = getUserById(id);
        User user = response.getData();
        return ResultUtils.success(userService.getUserVO(user));
    }
}
