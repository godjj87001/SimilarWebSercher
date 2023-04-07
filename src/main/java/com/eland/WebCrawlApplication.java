package com.eland;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * 啟動SpringBootServletInitializer war檔
 */
@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})

public class WebCrawlApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(WebCrawlApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(WebCrawlApplication.class, args);
    }
}