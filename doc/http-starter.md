# OSS 对象存储工具包

## 介绍

该工具包用于简单的http请求，基于okhttp封装

## 使用方式

1. 首先导入工具包的核心依赖
- 最新版本为`0.1.0`

```
<dependency>
    <groupId>cn.chenjy.yums</groupId>
    <artifactId>yums-starter-http</artifactId>
    <version>0.1.0</version>
</dependency>
```
2.代码中使用 只需要注入`OssTemplate`即可使用

```
@Autowired
HttpTemplate httpTemplate;
```