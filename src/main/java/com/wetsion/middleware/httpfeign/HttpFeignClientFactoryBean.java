package com.wetsion.middleware.httpfeign;

import com.wetsion.middleware.httpfeign.method.GetMappingMethodHandler;
import com.wetsion.middleware.httpfeign.method.HttpFeignClientMethodHandler;
import com.wetsion.middleware.httpfeign.method.PostMappingMethodHandler;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author weixin
 * @version 1.0
 * @CLassName HttpFeignClientFactoryBean
 * @date 2019/3/11 4:06 PM
 */
public class HttpFeignClientFactoryBean
        implements FactoryBean<Object>, InitializingBean, ApplicationContextAware {

    private String name;

    private Class<?> type;

    private boolean authorization;

    private ApplicationContext applicationContext;

    public boolean isAuthorization() {
        return authorization;
    }

    public void setAuthorization(boolean authorization) {
        this.authorization = authorization;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    @Override
    public Object getObject() throws Exception {
        System.out.println(this.name);
        System.out.println(this.type.getName());
        FeignClientProxy proxy = new FeignClientProxy(authorization ? applicationContext : null);
        Object r = Proxy.newProxyInstance(this.type.getClassLoader(), new Class[]{this.type}, proxy);
        return r;
    }

    static class FeignClientProxy implements InvocationHandler {

        private ApplicationContext applicationContext;

        public FeignClientProxy(ApplicationContext applicationContext) {
            this.applicationContext = applicationContext;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            System.out.println(proxy.getClass());
            System.out.println(method.getName());
            HttpFeignClientMethodHandler methodHandler = null;
            if (Object.class.equals(method.getDeclaringClass())) {
                return method.invoke(this, args);
            } else {
                System.out.println(method);
                System.out.println(method.getAnnotations());
                Annotation[] annotations = method.getAnnotations();
                Object result = null;
                for (int i = 0; i < annotations.length; i++) {
                    Annotation annotation = annotations[i];
                    if (annotation.annotationType().equals(GetMapping.class)) {
                        System.out.println("getmapping");
                        methodHandler = new GetMappingMethodHandler();
                        result = methodHandler.execute(method, args, applicationContext, method.getReturnType());
                    }
                    if (annotation.annotationType().equals(PostMapping.class)) {
                        System.out.println("postmapping");
                        methodHandler = new PostMappingMethodHandler();
                        result = methodHandler.execute(method, args, applicationContext, method.getReturnType());
                    }
                }
                return result;
            }
        }
    }

    @Override
    public Class<?> getObjectType() {
        return this.type;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.hasText(this.name, "Name must be set");
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
