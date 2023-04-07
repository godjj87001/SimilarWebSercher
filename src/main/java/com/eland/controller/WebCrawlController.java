package com.eland.controller;

import com.eland.service.WebCrawlService;
import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;


@RestController
public class WebCrawlController {
    private static final Logger logger = Logger.getLogger(WebCrawlController.class.getName());

    @Value("${driverPath}")
    String driverPath;
    @Value("${crawUrl}")
    String crawUrl;
    @Value("${browserPath}")
    String browserPath;
    @Value("${httpProxy}")
    String httpProxy;

    @GetMapping("api/domain/{domain}")
    public String getCrawlDomain(@PathVariable String domain) {
        WebCrawlService webCrawlService = new WebCrawlService();
        return webCrawlService.crawlDomain(domain, driverPath, crawUrl, browserPath, httpProxy);
    }

    @PostMapping("api/{domain}")
    public String postCrawlDomain(@RequestBody String domain) {
        WebCrawlService webCrawlService = new WebCrawlService();
        return webCrawlService.crawlDomain(domain, driverPath, crawUrl, browserPath, httpProxy);
    }


}