package cn.chenjy.yums.http.template;

import java.util.Map;

/**
 * @author ChenJY
 * @create 2021/6/18 11:12 上午
 * @DESCRIPTION
 */
public interface HttpTemplate {

    String get(String url);

    String get(String url, Map<String,String> params);

    String post(String url);

    String postByForm(String url, Map<String,String> params);

    String postByJson(String url, Map<String, Object> params);

}
