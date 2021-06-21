package cn.chenjy.yums.http;

import cn.chenjy.yums.http.config.HttpProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ChenJY
 * @create 2021/6/21 9:26 上午
 * @DESCRIPTION
 */
public class Context {
    private static final Logger LOG = LoggerFactory.getLogger(Context.class);
    private static final String TAG = "Context";

    public final HttpProperties httpProperties;

    public Context(HttpProperties httpProperties) {
        this.httpProperties = httpProperties;
    }
}
