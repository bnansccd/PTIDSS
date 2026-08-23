package com.troy.common.security.annotation;

import com.troy.common.core.utils.StringUtils;
import com.troy.common.security.utils.DictUtils;
import com.troy.system.api.domain.VO.SysDictVO;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @Auther: zhuqing
 * @Date: 2023/9/15 16:16:43
 * @Description: ValidDictConstraintValidator
 * @Version: 1.0.0
 */
public class ValidDictConstraintValidator implements ConstraintValidator<ValidDict, String> {

    private List<SysDictVO> sysDictVOS = new ArrayList<>();

    @Override
    public void initialize(ValidDict constraintAnnotation) {
        String parentType = constraintAnnotation.parentType().getCode();
        sysDictVOS = DictUtils.getDictCache(parentType);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (StringUtils.isBlank(value)) {
            return true;
        }
        Optional<SysDictVO> optional = sysDictVOS.stream().filter(d -> StringUtils.equals(value, d.getDictType())).findFirst();
        if (optional.isPresent()) {
            return true;
        }
        return false;
    }
}
