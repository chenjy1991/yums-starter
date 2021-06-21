package cn.chenjy.yums.http.template;

import cn.chenjy.yums.http.CharConst;
import com.google.gson.Gson;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

/**
 * @author ChenJY
 * @create 2021/6/18 11:13 上午
 * @DESCRIPTION
 */
public class OkHttpTemplate implements HttpTemplate {
    private static final Logger LOG = LoggerFactory.getLogger(OkHttpTemplate.class);
    private static final String TAG = "OkHttpTemplate";

    @Autowired
    OkHttpClient okHttpClient;

    @Override
    public String get(String url) {
        return get(url,null);
    }

    @Override
    public String get(String url, Map<String, String> params) {
        Request request = getGetRquest(url, params);
        String responseStr = execRequest(request);
        return responseStr;
    }

    @Override
    public String post(String url) {
        return postByForm(url,null);
    }

    @Override
    public String postByForm(String url, Map<String, String> params) {
        Request request = getPostFormRequest(url, params);
        String responseStr = execRequest(request);
        return responseStr;
    }

    @Override
    public String postByJson(String url, Map<String, Object> params) {
        Request request = getPostJsonRequest(url, params);
        String responseStr = execRequest(request);
        return responseStr;
    }

    /**
     * 发送请求
     *
     * @param request
     * @return
     */
    private String execRequest(Request request) {
        Response response = null;
        try {
            response = okHttpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                return response.body().string();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (response != null) {
                response.close();
            }
        }
        return null;
    }

    /**
     * 获取参数拼接在URL中的地址
     *
     * @param url
     * @param params
     * @return
     */
    private String getQueryUrlString(String url, Map<String, String> params) {
        StringBuilder urlSb = new StringBuilder(url);
        if (params != null && params.keySet().size() > 0) {
            urlSb.append(CharConst.QUESTION_MARK);
            Iterator<Map.Entry<String, String>> is = params.entrySet().iterator();
            while (is.hasNext()) {
                Map.Entry<String, String> item = is.next();
                urlSb.append(item.getKey()).append("=").append(item.getValue()).append("&");
            }
            urlSb.deleteCharAt(urlSb.lastIndexOf("&"));
        }
        return urlSb.toString();
    }

    /**
     * 获取GET方法的Request
     *
     * @param url
     * @return
     */
    private Request getGetRquest(String url, Map<String, String> params) {
        return new Request.Builder().url(getQueryUrlString(url, params)).build();
    }

    /**
     * 获取POST-FORM方法的Request
     *
     * @param url
     * @param params
     * @return
     */
    private Request getPostFormRequest(String url, Map<String, String> params) {
        FormBody.Builder builder = new FormBody.Builder();
        //添加参数
        if (params != null && params.keySet().size() > 0) {
            for (String key : params.keySet()) {
                builder.add(key, params.get(key));
            }
        }
        Request request = new Request.Builder().url(url).post(builder.build()).build();
        return request;
    }

    /**
     * 获取POST-JSON方法的Request
     *
     * @param url
     * @param params
     * @return
     */
    private Request getPostJsonRequest(String url, Map<String, Object> params) {
        Gson gson = new Gson();
        String jsonParams = gson.toJson(params);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonParams);
        Request request = new Request.Builder().url(url).post(requestBody).build();
        return request;
    }
}
