package com.example.drug.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.example.drug.mapper")
public class MyBatisPlusConfig {
}
