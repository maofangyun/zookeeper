package com.zookeeper.demo;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * zookeeper的客户端
 * */
@Component
public class ZkClient {

    private CuratorFramework curatorFramework;
    @Value("${ZooKeeper.Connect.Url}")
    private String CONNECT_URL;
    @Value("${ZooKeeper.Session.TimeOut}")
    private int SESSION_TIMEOUT;

    @PostConstruct
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

    protected class OneWatch implements Watcher{

        @Override
        public void process(WatchedEvent watchedEvent) {
            try {
                ZooKeeper zk = curatorFramework.getZookeeperClient().getZooKeeper();
                byte[] data = zk.getData("/mynode", false, new Stat());
                System.out.println(new String(data));
            } catch (KeeperException | InterruptedException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
