package com.eland.service;

import com.eland.controller.WebCrawlController;
import org.springframework.stereotype.Service;

@Service
public class WebCrawlService {
    public String crawlDomain(String domain, String driverPath, String crawUrl,String browserPath) {
        WebCrawlController webCrawlController = new WebCrawlController();
        return webCrawlController.crawlDomain(domain,driverPath,crawUrl,browserPath);
    }
}
