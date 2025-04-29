package com.atguigu.order.service;

import com.atguigu.order.model.Order;

public interface OrderService {

    Order createOrder(Long productId, Long userId);
}
