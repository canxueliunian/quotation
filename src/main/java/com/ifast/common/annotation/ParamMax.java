package com.ifast.common.annotation;


import java.lang.annotation.*;

/**
 * Created by viruser on 2017/6/20.
 * <= 比较场景
 * value为数据表对应列名 value值不能为空
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ParamMax {
    String value();
}
