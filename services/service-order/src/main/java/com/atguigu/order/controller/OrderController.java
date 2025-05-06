package com.atguigu.order.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.atguigu.order.model.Order;
import com.atguigu.order.properties.OrderProperties;
import com.atguigu.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

//@CrossOrigin
//@RequestMapping("/api/order")
@Slf4j
@RestController
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    OrderProperties orderProperties;

    @GetMapping("/config")
    public String config() {
        return "order.timeout=" + orderProperties.getTimeout() + ",order.auto-confirm=" + orderProperties.getAutoConfirm();
//                +",order.db-url="+orderProperties.getDbUrl();
    }

    @GetMapping("/create")
    public Order createOrder(@RequestParam("userId") Long userId,
                             @RequestParam("productId") Long productId) {
        return orderService.createOrder(userId, productId);
    }

    @GetMapping("/seckill")
    @SentinelResource(value = "seckill-order", fallback = "seckillFallback")
    public Order seckill(@RequestParam(value = "userId", required = false) Long userId,
                         @RequestParam(value = "productId", defaultValue = "0") Long productId) {
        Order order = orderService.createOrder(userId, productId);
        order.setId(Long.MAX_VALUE);
        return order;
    }

    public Order seckillFallback(Long userId,
                                 Long productId,
                                 Throwable exception) {
        System.out.println("seckillFallback............");
        Order order = new Order();
        order.setId(productId);
        order.setUserId(userId);
        order.setAddress("异常信息：" + exception.getClass());

        return order;
    }

    @GetMapping("/writeDb")
    public String writeDb() {
        log.info("writeDb");
        return "writeDb success";
    }

    @GetMapping("/readDb")
    public String readDb() {
        log.info("readDb");
        return "readDb success";
    }
}
