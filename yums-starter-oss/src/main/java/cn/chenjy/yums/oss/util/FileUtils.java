package cn.chenjy.yums.oss.util;

import cn.chenjy.yums.oss.constant.CharConst;
import cn.chenjy.yums.oss.constant.StringConst;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.File;

/**
 * @author ChenJY
 * @create 2021/5/10 10:56 上午
 * @DESCRIPTION
 */
public class FileUtils {
    private static final Logger LOG = LoggerFactory.getLogger(FileUtils.class);
    private static final String TAG = "FileUtils";

    /**
     * 获取文件后缀名
     *
     * @param fullName
     * @return
     */
    public static String getFileExtension(String fullName) {
        if (StringUtils.isEmpty(fullName)) {
            return CharConst.EMPTY;
        }
        String fileName = new File(fullName).getName();
        int dotIndex = fileName.lastIndexOf(CharConst.DOT);
        return (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1);
    }

    /**
     * 获取cdn域名
     *
     * @param cdnDomain
     * @return
     */
    public static String getCdnHost(String cdnDomain) {
        StringBuilder host = new StringBuilder();
        if (!cdnDomain.startsWith(StringConst.SSL_PREFIX) && !cdnDomain.startsWith(StringConst.UN_SSL_PREFIX)) {
            host.append(StringConst.UN_SSL_PREFIX);
        }
        host.append(cdnDomain);
        if (cdnDomain.endsWith(CharConst.SLASH)) {
            host.deleteCharAt(host.lastIndexOf(CharConst.SLASH));
        }
        return host.toString();
    }
}
