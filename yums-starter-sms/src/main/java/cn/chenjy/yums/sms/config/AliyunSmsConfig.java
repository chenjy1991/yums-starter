package cn.chenjy.yums.sms.config;

import cn.chenjy.yums.sms.templates.AliyunSmsTemplate;
import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.teaopenapi.models.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author ChenJY
 * @create 2021/5/12 2:49 下午
 * @DESCRIPTION
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(SmsProp.class)
@ConditionalOnClass({Client.class})
@ConditionalOnProperty(value = "yums.sms.enable", havingValue = "true")
public class AliyunSmsConfig {
    private static final Logger LOG = LoggerFactory.getLogger(AliyunSmsConfig.class);
    private static final String TAG = "SmsConfig";

    private final SmsProp smsProp;

    public AliyunSmsConfig(SmsProp smsProp){
        this.smsProp=smsProp;
    }

    @Bean
    @ConditionalOnMissingBean(Client.class)
    @ConditionalOnProperty(value = "yums.sms.name", havingValue = "aliyun")
    public Client smsClient()throws Exception{
        Config config = new Config()
                // 您的AccessKey ID
                .setAccessKeyId(smsProp.getAccessKey())
                // 您的AccessKey Secret
                .setAccessKeySecret(smsProp.getSecretKey());
        // 访问的域名
        config.endpoint = "dysmsapi.aliyuncs.com";
        return new Client(config);
    }

    @Bean
    @ConditionalOnBean({Client.class})
    @ConditionalOnMissingBean(AliyunSmsTemplate.class)
    @ConditionalOnProperty(value = "yums.sms.name", havingValue = "aliyun")
    public AliyunSmsTemplate aliyunSmsTemplate(Client smsClient){
        return new AliyunSmsTemplate(smsClient);
    }

}
