package com.wetsion.middleware.httpfeign.method;

import org.springframework.context.ApplicationContext;

import java.lang.reflect.Method;

/**
 * @author weixin
 * @version 1.0
 * @CLassName HttpFeignClientMethodHandler
 * @date 2019/3/11 6:41 PM
 */
public interface HttpFeignClientMethodHandler {

    <T> T execute(Method method, Object[] args, ApplicationContext applicationContext, Class<T> returnType) throws Exception;

}
