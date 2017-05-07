package cn.zxy.master;

import com.sun.corba.se.spi.activation.Server;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkNodeExistsException;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by HP on 2017/5/7.
 */
public class WorkServer {

    private Boolean running = false;//服务器运行状态
    private final static String MASTER_NODE = "/master";
    private ZkClient zkClient;
    private ServerData serverData;//当前服务器的数据
    private ServerData masterData;//当前master服务器数据
    private IZkDataListener zkDataListener;
    //任务调度器
    private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

    public WorkServer(ZkClient zkClient, ServerData masterData) {
        this.zkClient = zkClient;
        this.serverData = masterData;
        this.zkDataListener = new ZkDataListener(this);
    }

    public void start() {
        if (running) {
            throw new RuntimeException("server is started!");
        }
        running = true;
        zkClient.subscribeDataChanges(MASTER_NODE, zkDataListener);
        takeMaster();
    }

    public void stop() {
        if (!running) {
            throw new RuntimeException("server is stopped!");
        }

        zkClient.unsubscribeDataChanges(MASTER_NODE, zkDataListener);
        releaseMaster();
    }

    public void releaseMaster() {
        if (checkMaster()) {
            zkClient.delete(MASTER_NODE);
        }
    }

    private boolean checkMaster() {
        ServerData masterData = zkClient.readData(MASTER_NODE, true);
        if (serverData.equals(masterData)) {
            return true;
        }
        return false;
    }

    public void takeMaster() {
        try {
            zkClient.createEphemeral(MASTER_NODE, serverData);
            masterData = serverData;
            System.out.println("master is " + serverData.getName());
            scheduledExecutorService.schedule(new Runnable() {
                @Override
                public void run() {
                    if (checkMaster()) {
                        releaseMaster();
                    }
                }
            }, 2, TimeUnit.SECONDS);
        } catch (ZkNodeExistsException e) {
            ServerData masterData = zkClient.readData(MASTER_NODE, true);
            if (masterData == null) {
                takeMaster();
            } else {
                this.masterData = masterData;
            }
        }
    }

    public ServerData getServerData() {
        return serverData;
    }

    public ServerData getMasterData() {
        return masterData;
    }
}
