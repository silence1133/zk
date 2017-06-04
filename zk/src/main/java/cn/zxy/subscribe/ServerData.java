package cn.zxy.subscribe;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by HP on 2017/5/29.
 * workserver的信息
 */
@Data
@AllArgsConstructor
public class ServerData {
    private String address;
    private Integer id;
    private String name;

    @Override
    public String toString() {
        return "ServerData{" +
                "address='" + address + '\'' +
                ", id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
