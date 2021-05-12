package cn.chenjy.yums.oss.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author ChenJY
 * @create 2021/5/10 2:44 上午
 * @DESCRIPTION
 */
@ConfigurationProperties(prefix = "yums.oss")
public class OssProp {
    /**
     * 是否开启
     */
    private Boolean enable;
    /**
     * 对象存储名称(aliyun)
     */
    private String name;
    /**
     * 是否开启租户模式
     */
    private Boolean tenantMode = false;
    /**
     * 对象存储服务的URL
     */
    private String endpoint;
    /**
     * Access key就像用户ID，可以唯一标识你的账户
     */
    private String accessKey;
    /**
     * Secret key是你账户的密码
     */
    private String secretKey;
    /**
     * 默认的存储桶名称
     */
    private String bucketName = "yums";

    private Boolean cdnEnable;
    private String cdnDomain;

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

    public Boolean getTenantMode() {
        return tenantMode;
    }

    public void setTenantMode(Boolean tenantMode) {
        this.tenantMode = tenantMode;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
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

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public Boolean getCdnEnable() {
        return cdnEnable;
    }

    public void setCdnEnable(Boolean cdnEnable) {
        this.cdnEnable = cdnEnable;
    }

    public String getCdnDomain() {
        return cdnDomain;
    }

    public void setCdnDomain(String cdnDomain) {
        this.cdnDomain = cdnDomain;
    }
}
