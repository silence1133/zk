package cn.zxy.subscribe;

import com.alibaba.fastjson.JSON;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;

/**
 * Created by HP on 2017/5/29.
 */
public class WorkServer {
    private String configPath;
    private String serversPath;
    private ServerData serverData;
    private ServerConfig serverConfig;
    private ZkClient zkClient;
    private IZkDataListener zkDataListener;

    public WorkServer(String configPath, String serversPath, ServerData serverData, ServerConfig serverConfig, ZkClient zkClient) {
        this.configPath = configPath;
        this.serversPath = serversPath;
        this.serverData = serverData;
        this.serverConfig = serverConfig;
        this.zkClient = zkClient;
        zkDataListener = new IZkDataListener() {
            @Override
            public void handleDataChange(String dataPath, Object data) throws Exception {
                String newConfig = new String((byte[]) data);
                ServerConfig newServerConfig = JSON.parseObject(newConfig, ServerConfig.class);
                updateConfig(newServerConfig);
                System.out.println("new server config:" + newServerConfig.toString());
            }

            @Override
            public void handleDataDeleted(String dataPath) throws Exception {

            }
        };
    }


    public void start() {
        System.out.println("work server ["+serverData.getName()+"] start...");
        registerServer();//在servers节点下注册自己
        zkClient.subscribeDataChanges(configPath, zkDataListener);//监听config节点事件
    }

    /**
     * 在servers节点下注册自己
     */
    private void registerServer() {
        if (!zkClient.exists(this.serversPath)) {
            zkClient.createPersistent(this.serversPath);
        }
        zkClient.createEphemeral(this.serversPath.concat("/").concat(serverData.getAddress()), JSON.toJSONString(serverData).getBytes());
    }

    public void stop() {
        System.out.println("word server stop");
        zkClient.unsubscribeDataChanges(configPath, zkDataListener);
        zkClient.delete(this.serversPath.concat("/").concat(serverData.getAddress()));
    }

    private void updateConfig(ServerConfig newServerConfig) {
        this.serverConfig = newServerConfig;
    }
}
