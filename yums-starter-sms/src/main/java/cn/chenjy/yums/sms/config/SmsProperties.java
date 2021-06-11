package cn.chenjy.yums.sms.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author ChenJY
 * @create 2021/5/12 2:49 下午
 * @DESCRIPTION
 */
@ConfigurationProperties(prefix = "yums.sms")
public class SmsProperties {
    /**
     * 是否开启
     */
    private Boolean enable;
    /**
     * 短信服务名称(aliyun、mas)
     */
    private String name;
    /**
     * ccess key就像用户ID，可以唯一标识你的账户
     */
    private String accessKey;
    /**
     * Secret key是你账户的密码
     */
    private String secretKey;

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }
}
