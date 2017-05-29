package cn.zxy.lock;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by HP on 2017/5/7.
 */
public class Lock{

    private static final String LOCK_NODE_PATH = "/lock";
    private static final String LOCK_BASE_PATH = "/node-";

    private ZkClient zkClient ;

    public Lock(ZkClient zkClient){
        this.zkClient = zkClient;
        init();
    }

    private void init() {
        if(!this.zkClient.exists(LOCK_NODE_PATH)){
            zkClient.createPersistent(LOCK_NODE_PATH);
        }
    }

    public Boolean getLock() throws InterruptedException {
        String currentPath = zkClient.createEphemeralSequential(LOCK_NODE_PATH+LOCK_BASE_PATH,null);
        return this.waitForLock(currentPath);
    }

    private Boolean  waitForLock(String currentPath) throws InterruptedException {
        List<String> childrenNode = getSortedChildren();
        //判断当前创建的节点是否是顺序节点的第一个节点,不是则没有拿到锁
        String currentNode = currentPath.replace(LOCK_NODE_PATH.concat("/"),"");
        if(!childrenNode.get(0).equals(currentNode)){
            String currentNodeBefore = getCurrentNodeBefore(currentNode,childrenNode);
            CountDownLatch countDownLatch = new CountDownLatch(1);
            //监听当前创建子节点前面的一个节点
            zkClient.subscribeDataChanges(LOCK_NODE_PATH.concat("/").concat(currentNodeBefore), new IZkDataListener() {
                @Override
                public void handleDataChange(String dataPath, Object data) throws Exception {

                }

                @Override
                public void handleDataDeleted(String dataPath) throws Exception {
                    countDownLatch.countDown();
                }
            });
            countDownLatch.await();
            waitForLock(currentPath);
        }
        return true;
    }

    private String getCurrentNodeBefore(String currentNode, List<String> childrenNode) {

        for(int i = 0 ;i<childrenNode.size(); i++  ){
            if(childrenNode.get(i).equals(currentNode)){
                return childrenNode.get(i-1);
            }
        }
        return null;
    }


    private List<String> getSortedChildren() {
       List<String> list = zkClient.getChildren(LOCK_NODE_PATH);
       list.sort(new Comparator<String>() {
           @Override
           public int compare(String o1, String o2) {
               return o1.compareTo(o2);
           }
       });
       return list;
    }

    public Boolean getLock(long time, TimeUnit timeUnit) throws Exception {
        return null;
    }

    public void release() {
        List<String> nodeList =  getSortedChildren();
        zkClient.delete(LOCK_NODE_PATH.concat("/").concat(nodeList.get(0)));
    }
}
