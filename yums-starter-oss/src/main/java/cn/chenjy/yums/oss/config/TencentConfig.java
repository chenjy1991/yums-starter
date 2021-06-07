package cn.chenjy.yums.oss.config;

import cn.chenjy.yums.oss.templates.TencentTemplate;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.http.HttpProtocol;
import com.qcloud.cos.region.Region;
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
 * @create 2021/6/7 5:31 下午
 * @DESCRIPTION
 */
@Configuration(proxyBeanMethods = false)
@AutoConfigureAfter(OssConfig.class)
@EnableConfigurationProperties(OssProperties.class)
@ConditionalOnClass({COSClient.class})
@ConditionalOnExpression("${yums.oss.enable:true}&&'${yums.oss.name}'.equals('tencent')")
public class TencentConfig {
    private static final Logger LOG = LoggerFactory.getLogger(TencentConfig.class);
    private static final String TAG = "TencentConfig";

    private OssProperties ossProperties;
    private OssRule ossRule;

    public TencentConfig(OssProperties ossProperties, OssRule ossRule) {
        this.ossProperties = ossProperties;
        this.ossRule = ossRule;
    }

    @Bean
    @ConditionalOnMissingBean(COSClient.class)
    public COSClient cosClient() {
        // 1 初始化用户身份信息（secretId, secretKey）。
        String secretId = ossProperties.getAccessKey();
        String secretKey = ossProperties.getSecretKey();
        COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);
        // 2 设置 bucket 的地域, COS 地域的简称请参照 https://cloud.tencent.com/document/product/436/6224
        // clientConfig 中包含了设置 region, https(默认 http), 超时, 代理等 set 方法, 使用可参见源码或者常见问题 Java SDK 部分。
        Region region = new Region(ossProperties.getLocation());
        ClientConfig clientConfig = new ClientConfig(region);
        // 这里建议设置使用 https 协议
        clientConfig.setHttpProtocol(HttpProtocol.https);
        // 3 生成 cos 客户端。
        return new COSClient(cred, clientConfig);
    }

    @Bean
    @ConditionalOnBean({COSClient.class})
    @ConditionalOnMissingBean(TencentTemplate.class)
    public TencentTemplate tencentTemplate(COSClient cosClient) {
        return new TencentTemplate(cosClient, ossProperties, ossRule);
    }
}
