package cn.chenjy.yums.oss.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author ChenJY
 * @create 2021/5/10 2:44 上午
 * @DESCRIPTION
 */
@ConfigurationProperties(prefix = "yums.oss")
public class OssProperties {
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
     * aliyun:对象存储服务的URL
     * huawei:对象存储服务的URL
     * qiniu:外链域名
     */
    private String endpoint;
    /**
     * 华为云:当终端节点不归属于华北-北京一（cn-north-1）时需要配置该属性
     * 腾讯云:即腾讯云cos的region属性
     */
    private String location;

    /**
     * 腾讯云特有配置项，申请腾讯云账户后所得到的账号，由系统自动分配，具有固定性和唯一性，可在 账号信息 中查看。
     */
    private String tencentAppId;
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

    /**
     * 存储桶是否公共读，默认为是
     */
    private Boolean isPublicRead = true;

    private Boolean cdnEnable = false;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTencentAppId() {
        return tencentAppId;
    }

    public void setTencentAppId(String tencentAppId) {
        this.tencentAppId = tencentAppId;
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

    public Boolean getIsPublicRead() {
        return isPublicRead;
    }

    public void setIsPublicRead(Boolean publicRead) {
        isPublicRead = publicRead;
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
