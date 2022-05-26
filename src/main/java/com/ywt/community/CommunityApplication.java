package com.ywt.community;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
@MapperScan("com.ywt.community.mapper")
public class CommunityApplication {
    @PostConstruct
    public void init() {
        /*
         * 解决netty冲突
         * see Netty4Utils.setAvailableProcessors()
         */
        System.setProperty("es.set.netty.runtime.available.processors", "false");
    }

    public static void main(String[] args) {
        SpringApplication.run(CommunityApplication.class, args);
    }

}
