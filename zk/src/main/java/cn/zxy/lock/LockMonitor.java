package cn.zxy.lock;

import org.I0Itec.zkclient.ZkClient;

/**
 * Created by HP on 2017/5/29.
 * 模拟多个线程争抢锁资源
 */
public class LockMonitor {
    public static void main(String[] args) throws InterruptedException {
        for(int i = 0; i< 4 ;i ++){
            LockThread thread = new LockThread("线程"+i);
            thread.start();
//            Thread.sleep(1000);
        }
    }

    static class LockThread extends Thread{
        private String threadName;
        public LockThread(String name){
            this.threadName = name ;
        }
        @Override
        public void run() {
            ZkClient zkClient = new ZkClient("localhost:2181", 50000, 5000);
            Lock lock = new Lock(zkClient);
            try {
                if(lock.getLock()){
                    System.out.println(this.threadName+"获得了锁");
                    Thread.sleep(10000);
                    lock.release();
                    System.out.println(this.threadName+"释放了锁");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
