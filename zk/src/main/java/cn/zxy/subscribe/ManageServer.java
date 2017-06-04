package cn.zxy.subscribe;

import com.alibaba.fastjson.JSON;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;

import java.util.List;

/**
 * Created by HP on 2017/5/30.
 */
public class ManageServer {
    private String serversPath;
    private String commandPath;
    private String configPath;
    private ZkClient zkClient;
    private ServerConfig serverConfig;//初始化配置信息
    private IZkChildListener zkChildListener;//监听workServer子节点变化
    private IZkDataListener zkDataListener;//监听command节点
    private List<String> serversList;

    public ManageServer(String serversPath, String commandPath, String configPath, ZkClient zkClient, ServerConfig serverConfig) {
        this.serversPath = serversPath;
        this.commandPath = commandPath;
        this.configPath = configPath;
        this.zkClient = zkClient;
        this.serverConfig = serverConfig;
        zkChildListener = new IZkChildListener() {
            @Override
            public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
                serversList = currentChilds;
                System.out.println(currentChilds);
            }
        };
        zkDataListener = new IZkDataListener() {
            @Override
            public void handleDataChange(String dataPath, Object data) throws Exception {
                String command = data.toString();
                System.out.println("读取到指令cmd:"+command);
                excute(command);
            }

            @Override
            public void handleDataDeleted(String dataPath) throws Exception {
//                System.out.println("delete");
            }
        };
    }

    public void start(){

        //监听servers节点下工作服务器的变化
        zkClient.subscribeChildChanges(serversPath,zkChildListener);
        //监听command节点下的数据，实时读取指令
        zkClient.subscribeDataChanges(commandPath,zkDataListener);
        System.out.println("manage server start...");
    }

    public void stop(){
        zkClient.unsubscribeDataChanges(commandPath,zkDataListener);
        zkClient.unsubscribeChildChanges(serversPath,zkChildListener);
    }

    private void excute(String command) {
        if ("list".equalsIgnoreCase(command)){
            System.out.println(serversList);
            System.out.println("执行指令list");
        }else if("create".equalsIgnoreCase(command)){
            System.out.println("执行指令create");
            if(zkClient.exists(configPath)){
                zkClient.writeData(configPath,JSON.toJSONString(serverConfig).getBytes());
            }else {
                zkClient.createPersistent(configPath, JSON.toJSONString(serverConfig).getBytes());
            }
        }else if("modify".equalsIgnoreCase(command)){
            System.out.println("modify");
            serverConfig.setDbUser(serverConfig.getDbUrl()+"_modify");
            if(!zkClient.exists(configPath)){
                zkClient.createPersistent(configPath);
            }
            zkClient.writeData(configPath,JSON.toJSONString(serverConfig).getBytes());
            System.out.println();
        }
    }
}
