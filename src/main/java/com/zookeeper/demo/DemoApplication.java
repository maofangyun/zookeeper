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
        //ZkClient zkClient = ac.getBean(ZkClient.class);
        //ZooKeeper zk = zkClient.getInstance().getZookeeperClient().getZooKeeper();
        //zk.create("/mynode","test".getBytes(),ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);
        //byte[] data1 = zk.getData("/mynode", zkClient.new OneWatch(), new Stat());
        //System.out.println(new String(data1));
        //byte[] data2 = zk.getData("/mynode", zkClient.new OneWatch(), new Stat());
        //System.out.println(new String(data2));
        //Thread.sleep(Long.MAX_VALUE);
        ZkLock zkLock = ac.getBean(ZkLock.class);
        for(int i=0; i<10; i++){
            Thread thread = new Thread(new ThreadTest(""+i,zkLock),"Thread-"+i);
            thread.start();
        }
        Thread.sleep(Long.MAX_VALUE);
    }

    static class ThreadTest implements Runnable{

        private String value;

        private ZkLock zkLock;

        public ThreadTest(String value,ZkLock zkLock){
            this.value = value;
            this.zkLock = zkLock;
        }

        @Override
        public void run() {
            zkLock.setValue(value);
            System.out.println(Thread.currentThread().getName()+"尝试获取锁！");
            zkLock.lock();
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName()+"释放锁！");
            zkLock.unlock();
        }
    }

}
