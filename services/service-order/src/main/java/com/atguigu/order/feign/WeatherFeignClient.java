package com.atguigu.order.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "test-client", url = "http://47.108.189.44:9898/api")
public interface WeatherFeignClient {

    @PostMapping("/memo/getUserMemo")
    String getUserMemo(@RequestHeader("Satoken") String satoken,@RequestParam("username") String username);
}
