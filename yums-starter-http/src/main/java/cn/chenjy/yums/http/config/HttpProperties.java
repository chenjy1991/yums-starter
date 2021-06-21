package cn.chenjy.yums.http.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author ChenJY
 * @create 2021/6/18 10:50 上午
 * @DESCRIPTION
 */
@ConfigurationProperties(prefix = "yums.http")
public class HttpProperties {

    /**
     * http工具的底层实现:okhttp、httpClient
     */
    private String type = "okhttp";


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
