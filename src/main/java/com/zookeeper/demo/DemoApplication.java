package com.zookeeper.demo;

import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) throws Exception {
        ApplicationContext ac = SpringApplication.run(DemoApplication.class, args);
        ZkClient zkClient = ac.getBean(ZkClient.class);
        CuratorFramework cf = zkClient.getInstance();
        cf.create().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath("/mynode","test".getBytes());
    }

}
