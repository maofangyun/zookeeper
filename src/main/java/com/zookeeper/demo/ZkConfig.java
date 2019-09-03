package com.zookeeper.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * zookeeper的配置类
 * */
@Component
public class ZkConfig {

    @Bean
    public ZkClient zkClient(){
        return new ZkClient();
    }
}
