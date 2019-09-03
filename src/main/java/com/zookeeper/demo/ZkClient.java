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

import javax.annotation.PostConstruct;

/**
 * zookeeper的客户端
 * */
public class ZkClient {

    private CuratorFramework curatorFramework;
    private final static String CONNECT_URL = "192.168.174.128:2181";
    private final static int SESSION_TIMEOUT = 50*1000;

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

    public ZooKeeper getInstance() throws Exception {
        return curatorFramework.getZookeeperClient().getZooKeeper();
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
