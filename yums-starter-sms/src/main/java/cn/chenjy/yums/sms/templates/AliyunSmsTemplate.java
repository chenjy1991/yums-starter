package cn.chenjy.yums.sms.templates;

import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @author ChenJY
 * @create 2021/5/12 3:15 下午
 * @DESCRIPTION
 */
public class AliyunSmsTemplate implements SmsTemplate {
    private static final Logger LOG = LoggerFactory.getLogger(AliyunSmsTemplate.class);
    private static final String TAG = "AliyunSmsTemplate";

    private final Client smsClient;

    public AliyunSmsTemplate(Client smsClient) {
        this.smsClient = smsClient;
    }

    @Override
    public Boolean sendSms(String mobile, String signName, String templateCode, String templateParam) {
        SendSmsRequest sendSmsRequest = new SendSmsRequest()
                .setPhoneNumbers(mobile)
                .setSignName(signName)
                .setTemplateCode(templateCode)
                .setTemplateParam(templateParam);
        try {
            SendSmsResponse response = smsClient.sendSms(sendSmsRequest);
            if ("OK".equals(response.body.code)) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Boolean sendSms(String mobile, String signName, String templateCode, Map<String, String> templateParam) {
        Gson gson = new Gson();
        return sendSms(mobile, signName, templateCode, gson.toJson(templateParam));
    }
}
