/*
 *      Copyright (c) 2018-2028, Chill Zhuang All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *  Neither the name of the dreamlu.net developer nor the names of its
 *  contributors may be used to endorse or promote products derived from
 *  this software without specific prior written permission.
 *  Author: Chill 庄骞 (smallchill@163.com)
 */
package cn.chenjy.yums.oss.config;

import cn.chenjy.yums.core.constant.CharConst;
import cn.chenjy.yums.core.util.DateUtils;
import cn.chenjy.yums.core.util.FileUtils;
import cn.chenjy.yums.core.util.IdUtils;

/**
 * 默认存储桶生成规则
 *
 * @author Chill
 */
public class YumsOssRule implements OssRule {

    /**
     * 租户模式
     */
    private final Boolean tenantMode;

    public YumsOssRule(Boolean tenantMode) {
        this.tenantMode = tenantMode;
    }


    @Override
    public String fileName(String originalFilename) {
        return "upload" + CharConst.SLASH + DateUtils.today() + CharConst.SLASH + IdUtils.uuid() + CharConst.DOT + FileUtils.getFileExtension(originalFilename);
    }

}
