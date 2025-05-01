package com.atguigu.order.interceptor;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import feign.InvocationContext;
import feign.Response;
import feign.ResponseInterceptor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 通过请求拦截器、响应拦截器可以对请求头、请求体、响应头、响应体做统一处理。敏感数据脱敏、统一错误响应格式、统一日志记录。
 */
@Component
public class CustomResponseInterceptor implements ResponseInterceptor {
    @Override
    public Object intercept(InvocationContext context, Chain chain) throws Exception {



        // 响应处理逻辑
        try (Response response = context.response()) {

            // 执行原始的调用，获取原始响应
            Object result = chain.next(context);
            // 1.获取原始响应 Response 对象不可变性
            String url = response.request().url();
            System.out.printf("%s 响应拦截器 url: %s%n", LocalDateTime.now(), url);

            // 2.修改响应头
            Map<String, Collection<String>> newHeaders = this.modifyHeaders(response.headers());
            System.out.printf("%s 响应拦截器 headers: %s%n", LocalDateTime.now(), newHeaders);

            // 3.修改响应体
            String newBody = this.modifyBody(response);
            System.out.printf("%s 响应拦截器 body: %s%n", LocalDateTime.now(), newBody);

            // 4.创建新的response对象
            Response newResponse = Response.builder()
                    .status(response.status())
                    .headers(newHeaders)
                    .body(newBody, StandardCharsets.UTF_8)
                    .request(response.request())
                    .reason(response.reason())
                    .build();

            return context.decoder().decode(newResponse, context.returnType());
        }

    }

    private Map<String, Collection<String>> modifyHeaders(Map<String, Collection<String>> originalHeaders) {
        HashMap<String, Collection<String>> newHeaders = new HashMap<>(originalHeaders);
        newHeaders.put("X-Request-ID", Collections.singletonList(UUID.randomUUID().toString().replace("-", "")));
        return newHeaders;
    }

    /**
     * 1.响应体只能读取一次
     *  Feign 的 Response.body().asInputStream() 是单向流，多次读取会导致异常。如果需要多次访问响应体，需先将内容缓存到内存
     * 2.性能影响
     *  修改响应体会增加内存开销，尤其是大文件场景下。建议仅在必要时修改。
     * 3.错误处理
     *  如果修改后的响应体格式非法（如 JSON 格式错误），会导致后续反序列化失败。可通过 try-catch 捕获异常
     * 4.编码一致性
     *  确保读取和写入响应体时使用相同的字符编码（如 StandardCharsets.UTF_8）
     * @param originalResponse
     * @return
     */
    private String modifyBody(Response originalResponse) {
        InputStream inputStream = null;
        int status = originalResponse.status();
        Map<String, Object> exceptionRet = new HashMap<>();
        exceptionRet.put("code", status);
        exceptionRet.put("msg", "请求失败");
        exceptionRet.put("success", false);
        exceptionRet.put("data", null);
        String exceptionRetJsonStr = JSONUtil.toJsonStr(exceptionRet);
        String result = "";
        try {
            inputStream = originalResponse.body().asInputStream();
            String bodyContent = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            JSONObject entries = JSONUtil.parseObj(bodyContent);
            entries.set("modifyResponse", "yes");
            entries.set("id", Integer.parseInt(String.valueOf(entries.getOrDefault("id", "0"))) + 1);
            result = JSONUtil.toJsonStr(entries);
        } catch (IOException e) {
            System.out.printf("%s openfeign响应拦截器异常%n", LocalDateTime.now());
            return exceptionRetJsonStr;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    System.out.printf("%s openfeign响应拦截器流关闭异常%n", LocalDateTime.now());
                    return exceptionRetJsonStr;
                }
            }
        }
        return result;
    }
}
