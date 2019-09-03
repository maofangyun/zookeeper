package com.zookeeper.demo;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

import javax.annotation.PostConstruct;

/**
 * zookeeper的客户端
 * */
public class ZkClient {

    private CuratorFramework curatorFramework;
    private final static String CONNECT_URL = "192.168.11.130:2181";
    private final static int SESSION_TIMEOUT = 50*1000;

    public void init(){
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(10*1000,5);
        curatorFramework = CuratorFrameworkFactory.builder().connectString(CONNECT_URL).retryPolicy(retryPolicy).
                sessionTimeoutMs(SESSION_TIMEOUT).build();
        curatorFramework.start();
    }

    public void stop(){
        curatorFramework.close();
    }

    public CuratorFramework getInstance(){
        return curatorFramework;
    }
}
