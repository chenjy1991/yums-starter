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
@EnableConfigurationProperties(OssProp.class)
public class OssConfig {
    private static final Logger LOG = LoggerFactory.getLogger(OssConfig.class);
    private static final String TAG = "OssConfig";

    private final OssProp ossProp;

    public OssConfig(OssProp ossProp) {
        this.ossProp = ossProp;
    }

    @Bean
    @ConditionalOnMissingBean(OssRule.class)
    public OssRule ossRule() {
        return new YumsOssRule(ossProp.getTenantMode());
    }
}
