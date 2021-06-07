package cn.chenjy.yums.oss.config;

import cn.chenjy.yums.oss.templates.AliyunTemplate;
import com.aliyun.oss.ClientConfiguration;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.common.auth.CredentialsProvider;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author ChenJY
 * @create 2021/5/10 2:50 上午
 * @DESCRIPTION
 */
@Configuration(proxyBeanMethods = false)
@AutoConfigureAfter(YumsOssConfig.class)
@EnableConfigurationProperties(OssProperties.class)
@ConditionalOnClass({OSSClient.class})
@ConditionalOnExpression("${yums.oss.enable:true}&&'${yums.oss.name}'.equals('aliyun')")
public class AliyunConfig {
    private static final Logger LOG = LoggerFactory.getLogger(AliyunConfig.class);
    private static final String TAG = "AliyunOssConfig";
    private OssProperties ossProperties;
    private OssRule ossRule;

    public AliyunConfig(OssProperties ossProperties, OssRule ossRule) {
        this.ossProperties = ossProperties;
        this.ossRule = ossRule;
    }

    @Bean
    @ConditionalOnMissingBean(OSSClient.class)
    public OSSClient ossClient() {
        // 创建ClientConfiguration。ClientConfiguration是OSSClient的配置类，可配置代理、连接超时、最大连接数等参数。
        ClientConfiguration conf = new ClientConfiguration();
        // 设置OSSClient允许打开的最大HTTP连接数，默认为1024个。
        conf.setMaxConnections(1024);
        // 设置Socket层传输数据的超时时间，默认为50000毫秒。
        conf.setSocketTimeout(50000);
        // 设置建立连接的超时时间，默认为50000毫秒。
        conf.setConnectionTimeout(50000);
        // 设置从连接池中获取连接的超时时间（单位：毫秒），默认不超时。
        conf.setConnectionRequestTimeout(1000);
        // 设置连接空闲超时时间。超时则关闭连接，默认为60000毫秒。
        conf.setIdleConnectionTime(60000);
        // 设置失败请求重试次数，默认为3次。
        conf.setMaxErrorRetry(5);
        CredentialsProvider credentialsProvider = new DefaultCredentialProvider(ossProperties.getAccessKey(), ossProperties.getSecretKey());
        return new OSSClient(ossProperties.getEndpoint(), credentialsProvider, conf);
    }

    @Bean
    @ConditionalOnBean({OSSClient.class})
    @ConditionalOnMissingBean(AliyunTemplate.class)
    public AliyunTemplate aliyunOssTemplate(OSSClient ossClient) {
        return new AliyunTemplate(ossClient, ossProperties, ossRule);
    }
}
