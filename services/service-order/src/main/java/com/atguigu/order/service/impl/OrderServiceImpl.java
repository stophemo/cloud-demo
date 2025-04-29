package com.atguigu.order.service.impl;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import com.atguigu.order.model.Order;
import com.atguigu.order.service.OrderService;
import com.atguigu.product.model.Product;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    DiscoveryClient discoveryClient;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    LoadBalancerClient loadBalancerClient;

    @Override
    public Order createOrder(Long productId, Long userId) {
        Product product = getProductFromRemoteWithLoadBalanceAnnotation(productId);
        Order order = new Order();
        order.setId(1L);
        // TODO
        order.setTotalAmount(product.getPrice().multiply(new BigDecimal(product.getNum())));
        order.setUserId(userId);
        order.setNickName("zhangsan");
        order.setAddress("atguigu");
        // TODO
        order.setProductList(Arrays.asList(product));

        return order;
    }


    private Product getProductFromRemote(Long productId) {
        List<ServiceInstance> instances = discoveryClient.getInstances("service-product");

        ServiceInstance serviceInstance = instances.get(0);
        // http://localhost:9000/product/4
        String url = "http://" + serviceInstance.getHost() + ":" + serviceInstance.getPort() + "/product/" + productId;
        log.info("远程请求：{}", url);
        // 发送请求
        return restTemplate.getForObject(url, Product.class);
    }

    private Product getProductFromRemoteWithLoadBalance(Long productId) {
//        List<ServiceInstance> instances = discoveryClient.getInstances("service-product");
        ServiceInstance choose = loadBalancerClient.choose("service-product");

        // http://localhost:9000/product/4
        String url = "http://" + choose.getHost() + ":" + choose.getPort() + "/product/" + productId;
        log.info("远程请求：{}", url);
        // 发送请求
        return restTemplate.getForObject(url, Product.class);
    }


    // 注解式负载均衡
    private Product getProductFromRemoteWithLoadBalanceAnnotation(Long productId) {
        // http://localhost:9000/product/4
        String url = "http://service-product/product/" + productId;
        log.info("远程请求：{}", url);
        // 发送请求
        return restTemplate.getForObject(url, Product.class);
    }
}
