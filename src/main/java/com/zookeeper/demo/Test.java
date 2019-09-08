package com.zookeeper.demo;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Test {
    public static void main(String[] args) throws  Exception{
        ExecutorService executorService= Executors.newFixedThreadPool(40);
        String content="abcdefg";
        String zkHost="192.168.174.128";
        Integer sessionOut=30000;
        //创建10个线程测试
        for(int i=0;i<10;++i){
            ZKDisLock disLock=new ZKDisLock(sessionOut,zkHost);
            disLock.init();
            executorService.submit(new TestJob(disLock,content));
        }

    }

    //测试任务
    private static class TestJob implements Runnable{
        private ZKDisLock disLock;
        private String content;
        public TestJob(ZKDisLock disLock, String content) {
            this.disLock = disLock;
            this.content = content;
        }
        @Override
        public void run() {
            try {
                disLock.lock(content);
                Thread.sleep(2000);
                disLock.unLock();
            }catch (Exception e){
                System.out.println(e.getMessage());
            }

        }
    }
}
