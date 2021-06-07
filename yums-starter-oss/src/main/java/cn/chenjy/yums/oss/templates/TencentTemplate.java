package cn.chenjy.yums.oss.templates;

import cn.chenjy.yums.oss.config.OssProperties;
import cn.chenjy.yums.oss.config.OssRule;
import cn.chenjy.yums.oss.constant.CharConst;
import cn.chenjy.yums.oss.model.OssFile;
import cn.chenjy.yums.oss.model.YumsFile;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.CannedAccessControlList;
import com.qcloud.cos.model.CreateBucketRequest;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectResult;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

/**
 * @author ChenJY
 * @create 2021/6/7 5:42 下午
 * @DESCRIPTION
 */
public class TencentTemplate implements OssTemplate {
    private final COSClient cosClient;
    private final OssProperties ossProperties;
    private final OssRule ossRule;
    private final String bucketName;

    public TencentTemplate(COSClient cosClient, OssProperties ossProperties, OssRule ossRule) {
        this.cosClient = cosClient;
        this.ossProperties = ossProperties;
        this.ossRule = ossRule;
        this.bucketName = ossProperties.getBucketName().concat(CharConst.DASH).concat(ossProperties.getTencentAppId());
    }

    @Override
    public void makeBucket() {
        if (!bucketExists()) {
            CreateBucketRequest createBucketRequest = new CreateBucketRequest(bucketName);
            // 设置 bucket 的权限为 Private(私有读写), 其他可选有公有读私有写, 公有读写
            if (ossProperties.getIsPublicRead()) {
                createBucketRequest.setCannedAcl(CannedAccessControlList.PublicRead);
            }
            cosClient.createBucket(createBucketRequest);
        }
    }

    @Override
    public void removeBucket() {
        if (bucketExists()) {
            cosClient.deleteBucket(bucketName);
        }
    }

    @Override
    public boolean bucketExists() {
        return cosClient.doesBucketExist(bucketName);
    }

    @Override
    public OssFile getFileInfo(String fileName) {
        ObjectMetadata info = cosClient.getObjectMetadata(bucketName, fileName);
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
            cosClient.putObject(bucketName, key, stream, null);
        } else {
            PutObjectResult response = cosClient.putObject(bucketName, key, stream, null);
            int retry = 0;
            int retryCount = 5;
            while (StringUtils.isEmpty(response.getETag()) && retry < retryCount) {
                response = cosClient.putObject(bucketName, key, stream, null);
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
        cosClient.deleteObject(bucketName, fileName);
    }

    @Override
    public void removeFiles(List<String> fileNames) {
        fileNames.forEach(fileName -> removeFile(fileName));
    }

    public String getOssHost() {
        return "http://" + cosClient.getClientConfig().getEndpointBuilder().buildGeneralApiEndpoint(bucketName);
    }

    private String getFileName(String originalFilename) {
        return ossRule.fileName(originalFilename);
    }
}
