package cn.zxy.master;

import org.I0Itec.zkclient.ZkClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Silence.
 *         模拟10个客户端的主main
 */
public class ClientMonitor {
    public static void main(String[] args) throws Exception {
        List<WorkServer> servers = new ArrayList<>(10);
        List<ZkClient> zkClients = new ArrayList<>(10);
        try {
            for (int i = 0; i < 10; i++) {
                ZkClient zkClient = new ZkClient("10.4.23.70:2181", 50000, 5000);
                ServerData data = new ServerData(i + 1, "clinet_" + (i + 1));
                WorkServer workServer = new WorkServer(zkClient, data);
                servers.add(workServer);
                zkClients.add(zkClient);
                workServer.start();
            }
            Thread.sleep(Integer.MAX_VALUE);
        } finally {
            servers.forEach(workServer -> workServer.stop());
            zkClients.forEach(zkClient -> zkClient.close());
        }
    }
}
