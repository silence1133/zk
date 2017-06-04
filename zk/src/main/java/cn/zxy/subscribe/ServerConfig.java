package cn.zxy.subscribe;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by HP on 2017/5/29.
 * 配置信息
 */
@Data
@AllArgsConstructor
public class ServerConfig {
    private String dbUrl;
    private String dbUser;
    private String dbPwd;
    @Override
    public String toString() {
        return "ServerConfig{" +
                "dbUrl='" + dbUrl + '\'' +
                ", dbUser='" + dbUser + '\'' +
                ", dbPwd='" + dbPwd + '\'' +
                '}';
    }
}
