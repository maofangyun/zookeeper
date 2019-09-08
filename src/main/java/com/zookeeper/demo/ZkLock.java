package com.zookeeper.demo;

import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.Watcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * ZooKeeper实现分布式锁
 * */
@Component
public class ZkLock implements Lock {

    @Autowired
    private ZkClient zkClient;

    private String value;

    private ThreadLocal<String> currentPath = new ThreadLocal<>();

    private final static String LOCK_NODE = "/lock";

    @Override
    public void lock() {
        tryLock();
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {

    }

    @Override
    public boolean tryLock() {
        CuratorFramework cf = zkClient.getInstance();
        String path = LOCK_NODE+"/children";
        List<Integer> orderList = new ArrayList<>();
        try {
            //返回顺序节点的path
            String forPath = cf.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath(path, value.getBytes());
            currentPath.set(forPath);
            List<String> strings = cf.getChildren().forPath(LOCK_NODE);
            for(String str : strings){
                String children = str.replace("children", "");
                orderList.add(Integer.valueOf(children));
            }
            Collections.sort(orderList);
            int firstOrder = orderList.get(0);
            int currentOrder = Integer.valueOf(currentPath.get().replace(LOCK_NODE+"/children", ""));
            if(firstOrder == currentOrder) {
                System.out.println(Thread.currentThread().getName() + "获取到锁！"+currentOrder);
                return true;
            }else {
                CountDownLatch latch = new CountDownLatch(1);
                Integer preOrder = currentOrder - 1;
                String prePath = path + preOrder;
                cf.checkExists().usingWatcher((Watcher) watchedEvent -> {
                    //if(watchedEvent.getType() == Watcher.Event.EventType.NodeDeleted){
                        System.out.println("触发"+prePath+"节点的监听器");
                        latch.countDown();
                    //}
                }).forPath(prePath);
                latch.await();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit){
        return false;
    }

    @Override
    public void unlock() {
        try {
            zkClient.getInstance().delete().forPath(currentPath.get());
            System.out.println("节点"+currentPath.get()+"已经删除！");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Condition newCondition() {
        return null;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
