package cn.chenjy.yums.oss.templates;

import cn.chenjy.yums.oss.model.OssFile;
import cn.chenjy.yums.oss.model.YumsFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

/**
 * OSS 抽象方法
 *
 * @author ChenJY
 * @create 2021/5/10 2:07 上午
 * @DESCRIPTION
 */
public interface OssTemplate {
    String SLASH = "/";
    String DOT = ".";
    String EMPTY = "";

    /**
     * 创建 存储桶
     */
    void makeBucket();

    /**
     * 删除 存储桶
     */
    void removeBucket();

    /**
     * 存储桶是否存在
     *
     * @return boolean
     */
    boolean bucketExists();


    /**
     * 获取文件信息
     *
     * @param fileName 存储桶文件名称
     * @return InputStream
     */
    OssFile getFileInfo(String fileName);

    /**
     * 获取文件地址
     *
     * @param fileName 存储桶对象名称
     * @return String
     */
    String getFileLink(String fileName);

    /**
     * 上传文件
     *
     * @param file 上传文件类
     * @return BladeFile
     */
    YumsFile uploadFile(MultipartFile file);

    /**
     * 上传文件
     *
     * @param file     上传文件类
     * @param fileName 上传文件名
     * @return BladeFile
     */
    YumsFile uploadFile(String fileName, MultipartFile file);

    /**
     * 上传文件
     *
     * @param fileName 存储桶对象名称
     * @param stream   文件流
     * @return BladeFile
     */
    YumsFile uploadFile(String fileName, InputStream stream);

    /**
     * 删除文件
     *
     * @param fileName 存储桶对象名称
     */
    void removeFile(String fileName);

    /**
     * 批量删除文件
     *
     * @param fileNames 存储桶对象名称集合
     */
    void removeFiles(List<String> fileNames);

}
