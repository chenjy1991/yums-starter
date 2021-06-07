package cn.chenjy.yums.oss.config;

import cn.chenjy.yums.oss.templates.HuaweiTemplate;
import com.obs.services.BasicObsCredentialsProvider;
import com.obs.services.ObsClient;
import com.obs.services.ObsConfiguration;
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
 * @create 2021/6/7 3:02 下午
 * @DESCRIPTION
 */
@Configuration(proxyBeanMethods = false)
@AutoConfigureAfter(YumsOssConfig.class)
@EnableConfigurationProperties(OssProperties.class)
@ConditionalOnClass({ObsClient.class})
@ConditionalOnExpression("${yums.oss.enable:true}&&'${yums.oss.name}'.equals('huawei')")
public class HuaweiConfig {
    private static final Logger LOG = LoggerFactory.getLogger(HuaweiConfig.class);
    private static final String TAG = "HuaweiConfig";
    private OssProperties ossProperties;
    private OssRule ossRule;

    public HuaweiConfig(OssProperties ossProperties, OssRule ossRule) {
        this.ossProperties = ossProperties;
        this.ossRule = ossRule;
    }

    @Bean
    @ConditionalOnMissingBean(ObsClient.class)
    public ObsClient obsClient() {
        ObsConfiguration config = new ObsConfiguration();
        config.setEndPoint(ossProperties.getEndpoint());
        config.setSocketTimeout(30000);
        config.setMaxErrorRetry(5);
        BasicObsCredentialsProvider credentialsProvider = new BasicObsCredentialsProvider(ossProperties.getAccessKey(), ossProperties.getSecretKey());
        return new ObsClient(credentialsProvider, config);
    }

    @Bean
    @ConditionalOnBean(ObsClient.class)
    @ConditionalOnMissingBean(HuaweiTemplate.class)
    public HuaweiTemplate huaweiTemplate(ObsClient obsClient) {
        return new HuaweiTemplate(obsClient, ossProperties, ossRule);
    }
}
