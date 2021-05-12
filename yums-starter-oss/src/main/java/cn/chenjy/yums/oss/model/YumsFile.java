package cn.chenjy.yums.oss.model;

/**
 * @author ChenJY
 * @create 2021/5/10 3:02 上午
 * @DESCRIPTION
 */
public class YumsFile {
    /**
     * 文件地址
     */
    private String link;
    /**
     * 域名地址
     */
    private String domain;
    /**
     * 文件名
     */
    private String name;
    /**
     * 初始文件名
     */
    private String originalName;

    /**
     * 文件记录表id
     */
    private Long fileId;

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public Long getFileId() {
        return fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }
}
