package cn.zxy.subscribe;

import org.I0Itec.zkclient.ZkClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Created by HP on 2017/6/4.
 */
public class Monitor {
    public static final String serversPath = "/servers";
    public static final String commandPath = "/command";
    public static final String configPath = "/serverConfig";
    public static void main(String[] args) throws Exception {
        ZkClient zkClient = new ZkClient("localhost:2181", 50000, 5000);
        ServerConfig config = new ServerConfig("dburl","dbUser","dbpwd");
        ManageServer manageServer = new ManageServer(serversPath,commandPath,configPath,zkClient, config);
        manageServer.start();
        for (int i = 1; i < 4; i++) {
            ServerData ServerData = new ServerData("192.168.1."+i,i,"server_"+i);
            WorkServer workServer = new WorkServer(configPath,serversPath,ServerData,config,zkClient);
            workServer.start();
        }
        Thread.sleep(500);
        System.out.println("敲回车键退出！\n");
        new BufferedReader(new InputStreamReader(System.in)).readLine();
    }
}
