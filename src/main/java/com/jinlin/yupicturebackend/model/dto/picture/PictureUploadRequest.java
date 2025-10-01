package com.jinlin.yupicturebackend.model.dto.user;

import lombok.Data;

@Data
public class PictureUploadRequest {
    /**
     * 图片id(用于图片的修改)
     */
    private Long id;
    private static final long serialVersionUID = 1L;
}
