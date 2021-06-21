package cn.chenjy.yums.http;

import cn.chenjy.yums.http.config.HttpProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ChenJY
 * @create 2021/6/21 9:28 上午
 * @DESCRIPTION
 */
public class HttpClient {
    private static final Logger LOG = LoggerFactory.getLogger(HttpClient.class);
    private static final String TAG = "Client";

    private final Context context;

    public HttpClient(Context context) {
        this.context = context;
    }

    public HttpProperties getConfig() {
        return this.context.httpProperties;
    }
}
