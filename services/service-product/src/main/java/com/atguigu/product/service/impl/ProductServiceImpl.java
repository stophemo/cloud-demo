package com.atguigu.product.service.impl;
import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

import com.atguigu.product.model.Product;
import com.atguigu.product.service.ProductService;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl implements ProductService {
    @Override
    public Product getProductById(Long id) {
        Product product = new Product();
        product.setId(id);
        product.setPrice(new BigDecimal("99"));
        product.setProductName("苹果-" + id);
        product.setNum(2);

//        try {
//            TimeUnit.SECONDS.sleep(100);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
        return product;
    }
}
