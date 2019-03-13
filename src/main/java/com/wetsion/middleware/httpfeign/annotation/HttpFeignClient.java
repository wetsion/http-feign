package com.wetsion.middleware.httpfeign.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 声明一个接口是远程调用rest
 *
 * @author weixin
 * @version 1.0
 * @CLassName HttpFeignClient
 * @date 2019/3/11 2:46 PM
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface HttpFeignClient {

    @AliasFor("name")
    String value() default "";

    @AliasFor("value")
    String name() default "";

    boolean primary() default true;

    /**
     * 是否需要头部加入Authorization
     * 当开启，将会在methodHandler中注入RmsOauthClientContext获取当前token,
     * 并放入Authorization
     **/
    boolean authorization() default false;
}
