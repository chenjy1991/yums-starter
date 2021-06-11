# OSS 对象存储工具包

## 介绍

该工具包用于各个云厂商的对象存储服务的使用，整合了各个厂商的SDK，解决不同SDK带来的代码无法复用的问题。  
通过该工具包，可以使一套代码在各个项目中复用，当更换对象存储服务时，只需要修改maven依赖及对应配置。  
目前已支持：阿里云(aliyun)、华为云(huawei)、腾讯云(tencent)、七牛云(qiniu)

## 使用方式

1. 首先导入工具包的核心依赖  
- 最新版本为`1.0.1`

```
<dependency>
    <groupId>cn.chenjy.yums</groupId>
    <artifactId>yums-starter-oss</artifactId>
    <version>1.0.1</version>
</dependency>
```

2. 导入需要使用云厂商SDK 例如阿里云：

```
<dependency>
    <groupId>com.aliyun.oss</groupId>
    <artifactId>aliyun-sdk-oss</artifactId>
    <version>${dependency.version.aliyun}</version>
</dependency>
```

3. `properties`或`yaml`配置文件中配置对应属性 示例：

```
yums.oss.enable=true
yums.oss.name=aliyun
yums.oss.bucket-name=yums
yums.oss.access-key=access-key
yums.oss.secret-key=secret-key
yums.oss.endpoint=oss-cn-hangzhou.aliyuncs.com
```

4.代码中使用 只需要注入`OssTemplate`即可使用

```
@Autowired
OssTemplate ossTemplate;
```

## OssTemplate 方法说明

|方法名|说明|
|:---|:---|
|makeBucket()|创建 存储桶|
|removeBucket()|删除 存储桶|
|bucketExists()|存储桶是否存在|
|getFileInfo(String fileName)|获取文件信息|
|getFileLink(String fileName)|获取文件地址|
|uploadFile(MultipartFile file)|上传文件|
|uploadFile(String fileName, MultipartFile file)|上传文件|
|uploadFile(String fileName, InputStream stream)|上传文件|
|removeFile(String fileName)|删除文件|
|removeFiles(List<String> fileNames)|批量删除文件|

## 配置项说明

|key|说明|
|:---|:---|
|yums.oss.enable|是否开启|
|yums.oss.name|对象存储名称(aliyun、huawei、tencent、qiniu)|
|~~yums.oss.tenant-mode~~|是否开启租户模式(暂不支持)|
|yums.oss.endpoint|aliyun:对象存储服务的URL<br>huawei:对象存储服务的URL<br>qiniu:外链域名|
|yums.oss.location|华为云:当终端节点不归属于华北-北京一（cn-north-1）时需要配置该属性<br>腾讯云:即腾讯云cos的region属性|
|yums.oss.tencent-app-id|腾讯云特有配置项，申请腾讯云账户后所得到的账号，由系统自动分配，具有固定性和唯一性，可在 账号信息 中查看。|
|yums.oss.access-key|Access key就像用户ID，可以唯一标识你的账户|
|yums.oss.secret-key|Secret key是你账户的密码|
|yums.oss.bucket-name|默认的存储桶名称|
|yums.oss.is-public-read|存储桶是否公共读，默认为是(创建存储桶时会使用)|
|yums.oss.cdn-enable|是否开启了CDN|
|yums.oss.cdn-domain|CDN域名|

## 建议的云厂商SDK版本

阿里云(aliyun)

```
<dependency>
    <groupId>com.aliyun.oss</groupId>
    <artifactId>aliyun-sdk-oss</artifactId>
    <version>3.12.0</version>
</dependency>
```

华为云(huawei)

```
<dependency>
    <groupId>com.huaweicloud</groupId>
    <artifactId>esdk-obs-java</artifactId>
    <version>3.21.4</version>
</dependency>
```

腾讯云(tencent)

```
<dependency>
    <groupId>com.qcloud</groupId>
    <artifactId>cos_api</artifactId>
    <version>5.6.38</version>
</dependency>
```

七牛云(qiniu)

```
<dependency>
    <groupId>com.qiniu</groupId>
    <artifactId>qiniu-java-sdk</artifactId>
    <version>7.7.0</version>
</dependency>
```