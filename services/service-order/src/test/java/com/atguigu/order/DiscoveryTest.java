package com.atguigu.order;

import com.alibaba.cloud.nacos.discovery.NacosServiceDiscovery;
import com.alibaba.nacos.api.exception.NacosException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;

import java.util.List;

@SpringBootTest
public class DiscoveryTest {

    @Autowired
    DiscoveryClient discoveryClient;
//

    @Autowired
    NacosServiceDiscovery nacosServiceDiscovery;

    @Test
    void nacosServiceDiscoveryTest() throws NacosException {
        for (String service : nacosServiceDiscovery.getServices()) {
            System.out.println("service = " + service);
            List<ServiceInstance> instances = nacosServiceDiscovery.getInstances(service);
            for (ServiceInstance instance : instances) {
                System.out.println("ip: " + instance.getHost() + ", port: " + instance.getPort());
            }
        }
    }

    @Test
    void discoveryClinetTest() {
        for (String instance : discoveryClient.getServices()) {
            System.out.println("service=" + instance);
            // 获取ip + port
            List<ServiceInstance> instances = discoveryClient.getInstances(instance);
            for (ServiceInstance serviceInstance : instances) {
                System.out.println("ip=" + serviceInstance.getHost() + ",port=" + serviceInstance.getPort());
            }
        }
    }
}
