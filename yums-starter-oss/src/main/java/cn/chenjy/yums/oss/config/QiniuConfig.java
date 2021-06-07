package cn.chenjy.yums.oss.config;

import cn.chenjy.yums.oss.templates.QiniuTemplate;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author ChenJY
 * @create 2021/6/7 10:11 上午
 * @DESCRIPTION
 */
@Configuration(proxyBeanMethods = false)
@AutoConfigureAfter(YumsOssConfig.class)
@EnableConfigurationProperties(OssProperties.class)
@ConditionalOnExpression("${yums.oss.enable:true}&&'${yums.oss.name}'.equals('qiniu')")
public class QiniuConfig {
    private static final Logger LOG = LoggerFactory.getLogger(QiniuConfig.class);
    private static final String TAG = "QiniuConfig";

    private final OssProperties ossProperties;
    private final OssRule ossRule;

    public QiniuConfig(OssProperties ossProperties, OssRule ossRule) {
        this.ossProperties = ossProperties;
        this.ossRule = ossRule;
    }

    @Bean
    @ConditionalOnMissingBean(com.qiniu.storage.Configuration.class)
    public com.qiniu.storage.Configuration qnConfiguration() {
        return new com.qiniu.storage.Configuration(Region.autoRegion());
    }

    @Bean
    @ConditionalOnMissingBean(Auth.class)
    public Auth auth() {
        return Auth.create(ossProperties.getAccessKey(), ossProperties.getSecretKey());
    }

    @Bean
    @ConditionalOnBean(com.qiniu.storage.Configuration.class)
    public UploadManager uploadManager(com.qiniu.storage.Configuration cfg) {
        return new UploadManager(cfg);
    }

    @Bean
    @ConditionalOnBean(com.qiniu.storage.Configuration.class)
    public BucketManager bucketManager(com.qiniu.storage.Configuration cfg) {
        return new BucketManager(Auth.create(ossProperties.getAccessKey(), ossProperties.getSecretKey()), cfg);
    }

    @Bean
    @ConditionalOnBean({Auth.class, UploadManager.class, BucketManager.class})
    @ConditionalOnMissingBean(QiniuTemplate.class)
    public QiniuTemplate qiniuTemplate(Auth auth, UploadManager uploadManager, BucketManager bucketManager) {
        return new QiniuTemplate(auth, uploadManager, bucketManager, ossProperties, ossRule);
    }
}
