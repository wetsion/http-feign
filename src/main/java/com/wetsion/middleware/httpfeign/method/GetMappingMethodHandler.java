package com.wetsion.middleware.httpfeign.method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 处理被 GetMapping 修饰的方法
 *
 * @author weixin
 * @version 1.0
 * @CLassName GetMappingMethodHandler
 * @date 2019/3/11 6:42 PM
 */
public class GetMappingMethodHandler implements HttpFeignClientMethodHandler {

    private final static Logger logger = LoggerFactory.getLogger(GetMappingMethodHandler.class);

    @Override
    public <T> T execute(Method method, Object[] args, ApplicationContext applicationContext, Class<T> returnType) throws Exception {
        GetMapping getMapping = method.getAnnotation(GetMapping.class);
        String url = getUrl(getMapping);
        Map<String, Object> varBuilder = new LinkedHashMap<>();
        MultiValueMap<String, Object> param = new LinkedMultiValueMap<>();
        getRequestParams(method, varBuilder, args);
        param.setAll(varBuilder);
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        // TODO 增加对头部的处理
        StringBuffer sb = new StringBuffer();
        for (String key : varBuilder.keySet()) {
            sb.append(key + "=" + varBuilder.get(key) + "&");
        }
        String temp = sb.toString();
        String valParam = temp.substring(0, temp.length() - 1);
        HttpEntity entity = new HttpEntity<>(httpHeaders);
        ResponseEntity<T> responseEntity =
                restTemplate.exchange(url + "?" +valParam, HttpMethod.GET, entity, returnType);
        T result = null;
        try {
            result = responseEntity.getBody();
        } catch (Exception e) {
            logger.info("请求结果转换失败！");
        }
        return result;
    }

    /** 获取注解里的url **/
    private String getUrl(GetMapping getMapping) throws Exception{
        String url = null;
        if ((!StringUtils.isEmpty(getMapping.value())) && getMapping.value().length > 0) {
            url = getMapping.value()[0];
        }
        if ((!StringUtils.isEmpty(getMapping.path())) && getMapping.path().length > 0) {
            url = getMapping.path()[0];
        }
        if (url == null) {
            throw new Exception("url 不能为空");
        }
        return url;
    }

    private void getRequestParams(Method method,
                                  Map<String, Object> varBuilder, Object[] args)
            throws Exception {
        Parameter[] parameters = method.getParameters();
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        if (parameters.length != args.length) {
            throw new Exception("参数数量异常");
        }
        for (int i = 0; i < parameters.length; i++) {
            Annotation[] annotations = parameterAnnotations[i];
            boolean isAdd = false;
            for (Annotation annotation : annotations) {
                if (RequestParam.class.equals(annotation.annotationType())) {
                    RequestParam requestParam = (RequestParam) annotation;
                    varBuilder.put(requestParam.value(), args[i]);
                    isAdd = true;
                }
            }
            if (!isAdd) {
                varBuilder.put(parameters[i].getName(), args[i]);
            }
        }
    }
}
