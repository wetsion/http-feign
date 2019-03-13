package com.wetsion.middleware.httpfeign.method;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * 处理 PostMapping 请求的方法
 *
 * @author weixin
 * @version 1.0
 * @CLassName PostMappingMethodHandler
 * @date 2019/3/11 6:43 PM
 */
public class PostMappingMethodHandler implements HttpFeignClientMethodHandler {

    private final static Logger logger = LoggerFactory.getLogger(PostMappingMethodHandler.class);

    @Override
    public <T> T execute(Method method, Object[] args, ApplicationContext applicationContext, Class<T> returnType) throws Exception {
        String url = getUrl(method.getAnnotation(PostMapping.class));
        HttpHeaders httpHeaders = new HttpHeaders();
        // TODO 以后增加对header的处理
        Object params = getRequestParams(method, args);
        HttpEntity entity = new HttpEntity<>(params, httpHeaders);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<T> responseEntity =
                restTemplate.postForEntity(url, entity, returnType);
        T result = null;
        try {
            result = responseEntity.getBody();
        } catch (Exception e) {
            logger.info("请求结果转换失败！");
        }
        return result;
    }

    private Object getRequestParams(Method method, Object[] args)
            throws Exception {
        Parameter[] parameters = method.getParameters();
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        Annotation[][] parameterAnnotationsTemp = method.getParameterAnnotations();
        boolean isRequetBody = false;
        boolean isRequestParam = false;
        for (int j = 0; j < parameterAnnotationsTemp.length; j++) {
            for (int k = 0; k < parameterAnnotationsTemp[j].length; k ++) {
                if (RequestBody.class.equals(parameterAnnotationsTemp[j][k].annotationType())) {
                    isRequetBody = true;
                }
                if (RequestParam.class.equals(parameterAnnotationsTemp[j][k].annotationType())) {
                    isRequestParam = true;
                }
            }
        }
        if (isRequetBody && isRequestParam) {
            throw new Exception("@RequestBody and @RequestParam can not be exist together！");
        }
        if (parameters.length != args.length) {
            throw new Exception("参数数量异常");
        }
        if (isRequestParam) {
            MultiValueMap<String, Object> params= new LinkedMultiValueMap<>();
            for (int i = 0; i < parameters.length; i++) {
                Annotation[] annotations = parameterAnnotations[i];
                boolean isAdd = false;
                for (Annotation annotation : annotations) {
                    if (RequestParam.class.equals(annotation.annotationType())) {
                        RequestParam requestParam = (RequestParam) annotation;
                        params.add(requestParam.value(), args[i]);
                        isAdd = true;
                    }
                }
                if (!isAdd) {
                    params.add(parameters[i].getName(), args[i]);
                }
            }
            return params;
        }
        if (isRequetBody) {
            String requetBody = (String) args[0];
            JSONObject params = JSON.parseObject(requetBody);
            return params;
        }
        throw new Exception("@RequestBody or @RequestParam should be exist one!");
    }

    private String getUrl(PostMapping requestMapping) throws Exception {
        String url = null;
        if ((!StringUtils.isEmpty(requestMapping.value())) && requestMapping.value().length > 0) {
            url = requestMapping.value()[0];
        }
        if ((!StringUtils.isEmpty(requestMapping.path())) && requestMapping.path().length > 0) {
            url = requestMapping.path()[0];
        }
        if (url == null) {
            throw new Exception("url 不能为空");
        }
        return url;
    }
}
