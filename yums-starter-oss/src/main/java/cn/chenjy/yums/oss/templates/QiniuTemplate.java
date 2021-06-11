package cn.chenjy.yums.oss.templates;

import cn.chenjy.yums.oss.config.OssProperties;
import cn.chenjy.yums.oss.config.OssRule;
import cn.chenjy.yums.oss.constant.CharConst;
import cn.chenjy.yums.oss.constant.StringConst;
import cn.chenjy.yums.oss.model.OssFile;
import cn.chenjy.yums.oss.model.YumsFile;
import cn.chenjy.yums.oss.util.FileUtils;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.FileInfo;
import com.qiniu.util.Auth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;

/**
 * @author ChenJY
 * @create 2021/6/7 11:11 上午
 * @DESCRIPTION
 */
public class QiniuTemplate implements OssTemplate {
    private static final Logger LOG = LoggerFactory.getLogger(QiniuTemplate.class);
    private static final String TAG = "QiniuTemplate";

    private final Auth auth;
    private final UploadManager uploadManager;
    private final BucketManager bucketManager;
    private final OssProperties ossProperties;
    private final OssRule ossRule;

    public QiniuTemplate(Auth auth, UploadManager uploadManager, BucketManager bucketManager, OssProperties ossProperties, OssRule ossRule) {
        this.auth = auth;
        this.uploadManager = uploadManager;
        this.bucketManager = bucketManager;
        this.ossProperties = ossProperties;
        this.ossRule = ossRule;
    }

    @Override
    public void makeBucket() {
        if (!bucketExists()) {
            try {
                bucketManager.createBucket(ossProperties.getBucketName(), Zone.autoZone().getRegion());
            } catch (QiniuException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void removeBucket() {
        //七牛云不支持通过SDK删除bucket
    }

    @Override
    public boolean bucketExists() {
        String[] buckets = new String[0];
        try {
            buckets = bucketManager.buckets();
        } catch (QiniuException e) {
            e.printStackTrace();
        }
        if (buckets == null || buckets.length == 0) {
            return false;
        }
        return Arrays.stream(buckets).anyMatch(bucket -> ObjectUtils.nullSafeEquals(bucket, ossProperties.getBucketName()));
    }

    @Override
    public OssFile getFileInfo(String fileName) {
        try {
            FileInfo stat = bucketManager.stat(ossProperties.getBucketName(), fileName);
            OssFile ossFile = new OssFile();
            ossFile.setName(StringUtils.isEmpty(stat.key) ? fileName : stat.key);
            ossFile.setLink(getFileLink(ossFile.getName()));
            ossFile.setHash(stat.hash);
            ossFile.setLength(stat.fsize);
            ossFile.setUploadTime(Instant.ofEpochMilli(stat.putTime / 10000).atZone(ZoneId.systemDefault()).toLocalDateTime());
            ossFile.setContentType(stat.mimeType);
            return ossFile;
        } catch (QiniuException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getFileLink(String fileName) {
        return getOssHost().concat(CharConst.SLASH).concat(fileName);
    }

    @Override
    public YumsFile uploadFile(MultipartFile file) {
        return uploadFile(file.getOriginalFilename(), file);
    }

    @Override
    public YumsFile uploadFile(String fileName, MultipartFile file) {
        try {
            return uploadFile(fileName, file.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public YumsFile uploadFile(String fileName, InputStream stream) {
        return put(stream, fileName, false);
    }

    public YumsFile put(InputStream stream, String key, boolean cover) {
        makeBucket();
        String originalName = key;
        key = getFileName(key);
        // 覆盖上传
        if (cover) {
            try {
                uploadManager.put(stream, key, getUploadToken(key), null, null);
            } catch (QiniuException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        } else {
            Response response = null;
            try {
                response = uploadManager.put(stream, key, getUploadToken(), null, null);
            } catch (QiniuException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
            int retry = 0;
            int retryCount = 5;
            while (response.needRetry() && retry < retryCount) {
                try {
                    response = uploadManager.put(stream, key, getUploadToken(), null, null);
                } catch (QiniuException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
                retry++;
            }
        }
        YumsFile file = new YumsFile();
        file.setOriginalName(originalName);
        file.setName(key);
        file.setDomain(getOssHost());
        file.setLink(getFileLink(key));
        return file;
    }

    @Override
    public void removeFile(String fileName) {
        try {
            bucketManager.delete(ossProperties.getBucketName(), fileName);
        } catch (QiniuException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removeFiles(List<String> fileNames) {
        fileNames.forEach(fileName -> removeFile(fileName));
    }

    /**
     * 获取上传凭证，普通上传
     *
     * @return
     */
    public String getUploadToken() {
        return auth.uploadToken(ossProperties.getBucketName());
    }

    /**
     * 获取上传凭证，覆盖上传
     *
     * @param key
     * @return
     */
    private String getUploadToken(String key) {
        return auth.uploadToken(ossProperties.getBucketName(), key);
    }

    /**
     * 获取域名
     *
     * @return String
     */
    public String getOssHost() {
        if (ossProperties.getCdnEnable() && !StringUtils.isEmpty(ossProperties.getCdnDomain())) {
            return FileUtils.getCdnHost(ossProperties.getCdnDomain());
        } else {
            StringBuilder host = new StringBuilder();
            host.append(ossProperties.getEndpoint().contains(StringConst.SSL_PREFIX) ? StringConst.SSL_PREFIX : StringConst.UN_SSL_PREFIX);
            host.append(ossProperties.getEndpoint().replaceFirst(host.toString(), CharConst.EMPTY));
            return host.toString();
        }
    }

    private String getFileName(String originalFilename) {
        return ossRule.fileName(originalFilename);
    }
}
