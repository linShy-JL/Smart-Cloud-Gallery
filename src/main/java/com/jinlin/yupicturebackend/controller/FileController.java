package com.jinlin.yupicturebackend.controller;

import cn.hutool.core.io.IoUtil;
import com.jinlin.yupicturebackend.annotation.AuthCheck;
import com.jinlin.yupicturebackend.common.BaseResponse;
import com.jinlin.yupicturebackend.common.ResultUtils;
import com.jinlin.yupicturebackend.constant.UserConstant;
import com.jinlin.yupicturebackend.exception.BusinessException;
import com.jinlin.yupicturebackend.exception.ErrorCode;
import com.jinlin.yupicturebackend.manager.CosManager;
import com.qcloud.cos.model.COSObject;
import com.qcloud.cos.model.COSObjectInputStream;
import com.qcloud.cos.utils.IOUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
@Slf4j
@RestController
@RequestMapping("/file")
public class FileController {
    @Resource
    private CosManager cosManager;

    /**
     * 测试文件上传
     * @param multipartFile
     * @return
     */
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @RequestMapping("/test/upload")
    public BaseResponse< String> testUploadFile(@RequestPart("file")MultipartFile multipartFile){
        //文件目录
        String fileName=multipartFile.getOriginalFilename();
        //要上传到的位置
        String filePath=String.format("/test/%s", fileName);
        File file=null;
        try {
            //创建一个临时文件，用来保存前端上传的文件
            file=File.createTempFile(fileName, null);
            multipartFile.transferTo(file);
            cosManager.putObject(filePath, file);
            //返回可访问的路径
            return ResultUtils.success(filePath);
        } catch (Exception e) {
            log.error("file upload error,filepath="+filePath,e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "上传失败");
        }finally {
            //每次上传文件都会在本地创建一个临时文件，所以当文件上传到远程后要删除临时文件
            if(file!=null){
                boolean deleten=file.delete();
                if(!deleten){
                    log.error("file delete error,filepath={}", filePath);
                }
            }
        }
    }
    /**
     * 测试文件下载
     * @param filePath
     * @param response
     * @throws IOException
     */
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @GetMapping("/test/download/")
    public void testDownloadFile(String filePath, HttpServletResponse response) throws IOException {
        COSObjectInputStream cosObjectInput =null;
        try {
            COSObject cosObject = cosManager.getObject(filePath);
            cosObjectInput=cosObject.getObjectContent();
            byte[] byteArray = IOUtils.toByteArray(cosObjectInput);
            //设置响应头
            response.setContentType("application/octet-stream;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + filePath);
            //写入响应头
            response.getOutputStream().write(byteArray);
            //进行刷新
            response.getOutputStream().flush();
        } catch (Exception e) {
            log.error("file download error,filepath="+filePath,e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "下载失败");
        }finally {
            //释放流
            if(cosObjectInput!=null){
                cosObjectInput.close();
            }
        }

    }
}
