package com.jinlin.yupicturebackend.common;

import com.jinlin.yupicturebackend.exception.ErrorCode;

/**
 * 响应工具类
 */
public class ResultUtils {
    /**
     * 成功
     * @param data  数据
     * @return   数据类型
     * @param <T>   响应
     */
    public static <T> BaseResponse<T> success(T data)
    {
        return new BaseResponse<>(0,data,"ok");
    }

    /**
     * 失败
     * @param errorCode   错误码
     * @return  响应
     */
    public static  BaseResponse<?> error(ErrorCode errorCode)
    {
        return new BaseResponse<>(errorCode);
    }

    /**
     * 失败
     * @param Code   错误码
     * @param message 错误信息
     * @return        响应
     */
    public static BaseResponse<?> error(int Code,String message){
        return new BaseResponse<>(Code,null,message);
    }

    /**
     * 错误
     * @param errorCode   错误码
     * @return            响应
     */
    public static BaseResponse<?> error(ErrorCode errorCode,String message){
        return new BaseResponse<>(errorCode.getCode(),null,message);
    }
}
