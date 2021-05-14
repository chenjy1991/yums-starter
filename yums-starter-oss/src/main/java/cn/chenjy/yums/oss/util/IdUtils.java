package cn.chenjy.yums.oss.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * @author ChenJY
 * @create 2021/5/10 10:54 上午
 * @DESCRIPTION
 */
public class IdUtils {
    private static final Logger LOG = LoggerFactory.getLogger(IdUtils.class);
    private static final String TAG = "IdUtils";

    public static String uuid() {
        return UUID.randomUUID().toString();
    }

    public static String simpleUuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }

}
