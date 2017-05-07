package cn.zxy.master;

import org.I0Itec.zkclient.IZkDataListener;

/**
 * @author silence
 *         监听节点数据的变化
 */
public class ZkDataListener implements IZkDataListener {

    private WorkServer workServer;

    public ZkDataListener(WorkServer workServer) {
        this.workServer = workServer;
    }

    @Override
    public void handleDataChange(String dataPath, Object data) throws Exception {
    }

    @Override
    public void handleDataDeleted(String dataPath) throws Exception {
        //如果当前的server不是masterserver
/*        if(!workServer.getServerData().equals(workServer.getMasterData())){
            //延迟5秒再争抢
            Thread.sleep(5000);
        }*/
        this.workServer.takeMaster();
    }
}
