package cn.chenjy.yums.oss.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author ChenJY
 * @create 2021/5/10 4:02 上午
 * @DESCRIPTION
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(OssProperties.class)
public class YumsOssConfig {
    private static final Logger LOG = LoggerFactory.getLogger(YumsOssConfig.class);
    private static final String TAG = "OssConfig";

    private final OssProperties ossProperties;

    public YumsOssConfig(OssProperties ossProperties) {
        this.ossProperties = ossProperties;
    }

    @Bean
    @ConditionalOnMissingBean(OssRule.class)
    public OssRule ossRule() {
        return new YumsOssRule(ossProperties.getTenantMode());
    }
}
