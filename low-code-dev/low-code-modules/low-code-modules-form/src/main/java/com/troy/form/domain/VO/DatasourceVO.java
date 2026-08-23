package com.troy.form.domain.VO;


import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/**
 * <p>
 * 公司产品表
 * </p>
 *
 * @author chenxl
 * @since 2023-03-14
 */
@Data
public class DatasourceVO {

    @NotBlank(message = "数据源标识不能为空")
    private String identification;

    @NotBlank(message = "数据源名称不能为空")
    private String name;

    @NotBlank(message = "数据源地址不能为空")
    private String url;

    @NotBlank(message = "数据源用户名不能为空")
    private String username;

    @NotBlank(message = "数据源密码不能为空")
    private String password;

    @NotBlank(message = "数据源driver不能为空")
    private String driver;

    @NotBlank(message = "数据源类型不能为空")
    private String type;

    private Long id;
}
