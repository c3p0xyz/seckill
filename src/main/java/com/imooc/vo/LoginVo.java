package com.imooc.vo;

import com.imooc.validator.IsMobile;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

@Data
public class LoginVo {

    // 通过注解的方式校验（JSR303）

    @NotNull
    @IsMobile
    private String mobile;

    @NotNull
    @Length(min = 32)  // 长度最小为 32
    private String password;
}
