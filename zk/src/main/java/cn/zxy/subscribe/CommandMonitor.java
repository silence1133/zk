package cn.zxy.subscribe;

import org.I0Itec.zkclient.ZkClient;

/**
 * Created by HP on 2017/6/4.
 */
public class CommandMonitor {
    public static void main(String[] args) {
        ZkClient zkClient = new ZkClient("localhost:2181", 50000, 5000);
        if(!zkClient.exists(Monitor.commandPath)){
            zkClient.createPersistent(Monitor.commandPath,"list");
        }
        zkClient.writeData(Monitor.commandPath,"modify");
    }
}
