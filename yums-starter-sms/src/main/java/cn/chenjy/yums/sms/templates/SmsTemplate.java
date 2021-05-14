package cn.chenjy.yums.sms.templates;

import java.util.Map;

/**
 * @author ChenJY
 * @create 2021/5/12 3:10 下午
 * @DESCRIPTION
 */
public interface SmsTemplate {

    Boolean sendSms(String mobile, String signName, String templateCode, String templateParam);

    Boolean sendSms(String mobile, String signName, String templateCode, Map<String,String> templateParam);
}
