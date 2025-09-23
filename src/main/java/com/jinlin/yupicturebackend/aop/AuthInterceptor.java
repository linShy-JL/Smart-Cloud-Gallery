package com.jinlin.yupicturebackend.aop;

import com.jinlin.yupicturebackend.annotation.AuthCheck;
import com.jinlin.yupicturebackend.common.ResultUtils;
import com.jinlin.yupicturebackend.exception.BusinessException;
import com.jinlin.yupicturebackend.exception.ErrorCode;
import com.jinlin.yupicturebackend.model.entity.User;
import com.jinlin.yupicturebackend.model.enums.UserRoleEnum;
import com.jinlin.yupicturebackend.service.UserService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Aspect
@Component  //交给容器进行管理
public class AuthInterceptor {
    @Resource
    private UserService userService;
    @Around("@annotation(authCheck)")
    public Object doInterceptor(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {
        String mustRole = authCheck.mustRole();
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        //获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        UserRoleEnum mustRoleEnum = UserRoleEnum.getEnumByValue(mustRole);
        //如果不需要权限就放行
        if(mustRoleEnum==null){
            return joinPoint.proceed();
        }
        //以下的代码必须有权限才能通过
        UserRoleEnum userRoleEnum = UserRoleEnum.getEnumByValue(loginUser.getUserRole());
        if(userRoleEnum==null){
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        //要求必须要有管理员权限，但用户没有管理员权限，则拒绝
        //(单前需要的是管理员权限，但用户又不是管理员权限)
        if(UserRoleEnum.ADMIN.equals(mustRoleEnum)&&!UserRoleEnum.ADMIN.equals(userRoleEnum)){
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        //通过权限校验，放行
        return joinPoint.proceed();
    }
}
