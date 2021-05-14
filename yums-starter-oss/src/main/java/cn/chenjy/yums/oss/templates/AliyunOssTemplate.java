package cn.chenjy.yums.oss.templates;


import cn.chenjy.yums.oss.config.OssProp;
import cn.chenjy.yums.oss.config.OssRule;
import cn.chenjy.yums.oss.constant.CharConst;
import cn.chenjy.yums.oss.model.OssFile;
import cn.chenjy.yums.oss.model.YumsFile;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectResult;
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
public class AliyunOssTemplate implements OssTemplate {

    private final OSSClient ossClient;
    private final OssProp ossProp;
    private final OssRule ossRule;

    public AliyunOssTemplate(OSSClient ossClient, OssProp ossProp, OssRule ossRule) {
        this.ossClient = ossClient;
        this.ossProp = ossProp;
        this.ossRule = ossRule;
    }

    @Override
    public void makeBucket() {
        if (!bucketExists()) {
            ossClient.createBucket(ossProp.getBucketName());
        }
    }

    @Override
    public void removeBucket() {
        if (bucketExists()) {
            ossClient.deleteBucket(ossProp.getBucketName());
        }
    }

    @Override
    public boolean bucketExists() {
        return ossClient.doesBucketExist(ossProp.getBucketName());
    }

    @Override
    public OssFile getFileInfo(String fileName) {
        ObjectMetadata stat = ossClient.getObjectMetadata(ossProp.getBucketName(), fileName);
        OssFile ossFile = new OssFile();
        ossFile.setName(fileName);
        ossFile.setLink(getFileLink(ossFile.getName()));
        ossFile.setHash(stat.getContentMD5());
        ossFile.setLength(stat.getContentLength());
        ossFile.setUploadTime(LocalDateTime.ofInstant(stat.getLastModified().toInstant(), ZoneId.systemDefault()));
        ossFile.setContentType(stat.getContentType());
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
            ossClient.putObject(ossProp.getBucketName(), key, stream);
        } else {
            PutObjectResult response = ossClient.putObject(ossProp.getBucketName(), key, stream);
            int retry = 0;
            int retryCount = 5;
            while (StringUtils.isEmpty(response.getETag()) && retry < retryCount) {
                response = ossClient.putObject(ossProp.getBucketName(), key, stream);
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
        ossClient.deleteObject(ossProp.getBucketName(), fileName);
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
        String prefix = ossProp.getEndpoint().contains("https://") ? "https://" : "http://";
        if (ossProp.getCdnEnable()) {
            prefix = ossProp.getCdnDomain();
        }
        return prefix + ossProp.getBucketName() + CharConst.DOT + ossProp.getEndpoint().replaceFirst(prefix, CharConst.EMPTY);
    }

    private String getFileName(String originalFilename) {
        return ossRule.fileName(originalFilename);
    }
}
