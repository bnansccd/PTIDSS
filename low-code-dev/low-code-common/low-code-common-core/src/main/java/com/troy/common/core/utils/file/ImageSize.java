package com.troy.common.core.utils.file;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName ImageSize
 * @Author ZhuQing
 * @Date: 2024/5/9  19:21
 * @Description: 图片尺寸
 */
@Data
public class ImageSize implements Serializable {

    private Integer width;

    private Integer height;
}
