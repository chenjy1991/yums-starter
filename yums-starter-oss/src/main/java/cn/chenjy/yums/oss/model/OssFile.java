package cn.chenjy.yums.oss.model;


import java.time.LocalDateTime;

/**
 * @author ChenJY
 * @create 2021/5/10 2:19 上午
 * @DESCRIPTION
 */
public class OssFile {
    /**
     * 文件地址
     */
    private String link;
    /**
     * 文件名
     */
    private String name;
    /**
     * 文件hash值
     */
    public String hash;
    /**
     * 文件大小
     */
    private long length;
    /**
     * 文件上传时间
     */
    private LocalDateTime uploadTime;
    /**
     * 文件contentType
     */
    private String contentType;

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public LocalDateTime getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(LocalDateTime uploadTime) {
        this.uploadTime = uploadTime;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}
