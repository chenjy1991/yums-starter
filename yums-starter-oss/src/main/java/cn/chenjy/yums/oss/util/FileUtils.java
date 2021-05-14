package cn.chenjy.yums.oss.util;

import cn.chenjy.yums.oss.constant.CharConst;
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

    public static String getFileExtension(String fullName) {
        if (StringUtils.isEmpty(fullName)) {
            return CharConst.EMPTY;
        }
        String fileName = new File(fullName).getName();
        int dotIndex = fileName.lastIndexOf(CharConst.DOT);
        return (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1);
    }
}
