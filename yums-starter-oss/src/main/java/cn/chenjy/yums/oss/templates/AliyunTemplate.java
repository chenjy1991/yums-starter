package cn.chenjy.yums.oss.templates;


import cn.chenjy.yums.oss.config.OssProperties;
import cn.chenjy.yums.oss.config.OssRule;
import cn.chenjy.yums.oss.constant.CharConst;
import cn.chenjy.yums.oss.constant.StringConst;
import cn.chenjy.yums.oss.model.OssFile;
import cn.chenjy.yums.oss.model.YumsFile;
import cn.chenjy.yums.oss.util.FileUtils;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.*;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

/**
 * @author ChenJY
 * @create 2021/5/10 2:17 上午
 * @DESCRIPTION
 */
public class AliyunTemplate implements OssTemplate {
    private final OSSClient ossClient;
    private final OssProperties ossProperties;
    private final OssRule ossRule;

    public AliyunTemplate(OSSClient ossClient, OssProperties ossProperties, OssRule ossRule) {
        this.ossClient = ossClient;
        this.ossProperties = ossProperties;
        this.ossRule = ossRule;
    }

    @Override
    public void makeBucket() {
        if (!bucketExists()) {
            CreateBucketRequest createBucketRequest = new CreateBucketRequest(ossProperties.getBucketName());
            createBucketRequest.setStorageClass(StorageClass.Standard);
            if (ossProperties.getIsPublicRead()) {
                createBucketRequest.setCannedACL(CannedAccessControlList.PublicRead);
            }
            ossClient.createBucket(createBucketRequest);
        }
    }

    @Override
    public void removeBucket() {
        if (bucketExists()) {
            ossClient.deleteBucket(ossProperties.getBucketName());
        }
    }

    @Override
    public boolean bucketExists() {
        return ossClient.doesBucketExist(ossProperties.getBucketName());
    }

    @Override
    public OssFile getFileInfo(String fileName) {
        ObjectMetadata info = ossClient.getObjectMetadata(ossProperties.getBucketName(), fileName);
        OssFile ossFile = new OssFile();
        ossFile.setName(fileName);
        ossFile.setLink(getFileLink(ossFile.getName()));
        ossFile.setHash(info.getContentMD5());
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
            ossClient.putObject(ossProperties.getBucketName(), key, stream);
        } else {
            PutObjectResult response = ossClient.putObject(ossProperties.getBucketName(), key, stream);
            int retry = 0;
            int retryCount = 5;
            while (StringUtils.isEmpty(response.getETag()) && retry < retryCount) {
                response = ossClient.putObject(ossProperties.getBucketName(), key, stream);
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
        ossClient.deleteObject(ossProperties.getBucketName(), fileName);
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
