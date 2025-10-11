package com.jinlin.yupicturebackend.model.dto.picture;

import lombok.Data;

import java.io.Serializable;
/*
批量导入图片请求
 */
@Data
public class PictureUploadByBatchRequest implements Serializable {
    /**
     * 搜索词
     */
    private String searchText;
    /**
     * 要抓取的图片个数（默认抓取10个）
     */
    private Integer count=10;
    /**
     * 图片名称前前缀
     */
    private String namePrefix;
    private static final long serialVersionUID = 1L;
}
