package cn.chenjy.yums.oss.templates;

import cn.chenjy.yums.oss.config.OssProperties;
import cn.chenjy.yums.oss.config.OssRule;
import cn.chenjy.yums.oss.constant.CharConst;
import cn.chenjy.yums.oss.constant.StringConst;
import cn.chenjy.yums.oss.model.OssFile;
import cn.chenjy.yums.oss.model.YumsFile;
import cn.chenjy.yums.oss.util.FileUtils;
import com.obs.services.ObsClient;
import com.obs.services.model.AccessControlList;
import com.obs.services.model.ObjectMetadata;
import com.obs.services.model.ObsBucket;
import com.obs.services.model.PutObjectResult;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

/**
 * @author ChenJY
 * @create 2021/6/7 3:22 下午
 * @DESCRIPTION
 */
public class HuaweiTemplate implements OssTemplate {
    private final ObsClient obsClient;
    private final OssProperties ossProperties;
    private final OssRule ossRule;

    public HuaweiTemplate(ObsClient obsClient, OssProperties ossProperties, OssRule ossRule) {
        this.obsClient = obsClient;
        this.ossProperties = ossProperties;
        this.ossRule = ossRule;
    }

    @Override
    public void makeBucket() {
        if (!bucketExists()) {
            ObsBucket obsBucket = new ObsBucket();
            if (ossProperties.getIsPublicRead()) {
                obsBucket.setAcl(AccessControlList.REST_CANNED_PUBLIC_READ);
            }
            if (!StringUtils.isEmpty(ossProperties.getLocation())) {
                obsBucket.setLocation(ossProperties.getLocation());
            }
            obsClient.createBucket(obsBucket);
        }
    }

    @Override
    public void removeBucket() {
        if (bucketExists()) {
            obsClient.deleteBucket(ossProperties.getBucketName());
        }
    }

    @Override
    public boolean bucketExists() {
        return obsClient.headBucket(ossProperties.getBucketName());
    }

    @Override
    public OssFile getFileInfo(String fileName) {
        ObjectMetadata info = obsClient.getObjectMetadata(ossProperties.getBucketName(), fileName);
        OssFile ossFile = new OssFile();
        ossFile.setName(fileName);
        ossFile.setLink(getFileLink(ossFile.getName()));
        ossFile.setHash(info.getContentMd5());
        ossFile.setLength(info.getContentLength());
        ossFile.setUploadTime(LocalDateTime.ofInstant(info.getLastModified().toInstant(), ZoneId.systemDefault()));
        ossFile.setContentType(info.getContentType());
        return ossFile;
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
        }
        return null;
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
            obsClient.putObject(ossProperties.getBucketName(), key, stream);
        } else {
            PutObjectResult response = obsClient.putObject(ossProperties.getBucketName(), key, stream);
            int retry = 0;
            int retryCount = 5;
            while (StringUtils.isEmpty(response.getEtag()) && retry < retryCount) {
                response = obsClient.putObject(ossProperties.getBucketName(), key, stream);
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
        obsClient.deleteObject(ossProperties.getBucketName(), fileName);
    }

    @Override
    public void removeFiles(List<String> fileNames) {
        fileNames.forEach(fileName -> removeFile(fileName));
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
            host.append(ossProperties.getBucketName());
            host.append(CharConst.DOT);
            host.append(ossProperties.getEndpoint().replaceFirst(host.toString(), CharConst.EMPTY));
            return host.toString();
        }
    }

    private String getFileName(String originalFilename) {
        return ossRule.fileName(originalFilename);
    }
}
