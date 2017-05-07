package cn.zxy.master;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by HP on 2017/5/7.
 * server的信息
 */
@Data
@AllArgsConstructor
public class ServerData implements Serializable {
    private long cid;
    private String name;

    public boolean equals(Object obj) {
        if (!(obj instanceof ServerData)) {
            return false;
        }
        ServerData data = (ServerData) obj;
        if (data.getCid() == this.getCid() && data.getName().equals(this.getName())) {
            return true;
        }
        return false;
    }
}
