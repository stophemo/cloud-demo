package com.atguigu.order;

import com.atguigu.order.feign.WeatherFeignClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TestClientTest {

    @Autowired
    WeatherFeignClient weatherFeignClient;

    @Test
    void test(){
        String userMemo = weatherFeignClient.getUserMemo("f4219bac89e545a6a04b83de7310905f", "admin");

        System.out.println(userMemo);
    }
}
