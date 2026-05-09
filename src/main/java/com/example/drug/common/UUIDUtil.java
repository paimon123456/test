package com.example.drug.common;

import java.util.UUID;

/**
 * UUID主键生成工具（对应数据库varchar(32)主键）
 */
public class UUIDUtil {
    public static String getUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
