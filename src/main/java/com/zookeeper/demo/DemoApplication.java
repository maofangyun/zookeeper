package com.zookeeper.demo;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) throws Exception {
        ApplicationContext ac = SpringApplication.run(DemoApplication.class, args);
        ZkClient zkClient = ac.getBean(ZkClient.class);
        ZooKeeper zk = zkClient.getInstance();
        zk.create("/mynode","test".getBytes(),ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);
        byte[] data1 = zk.getData("/mynode", zkClient.new OneWatch(), new Stat());
        System.out.println(new String(data1));
        byte[] data2 = zk.getData("/mynode", zkClient.new OneWatch(), new Stat());
        System.out.println(new String(data2));
        Thread.sleep(Long.MAX_VALUE);
    }

}
