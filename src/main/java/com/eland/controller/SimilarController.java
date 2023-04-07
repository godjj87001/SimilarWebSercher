package com.eland.controller;

import com.eland.dao.SimilarDao;
import com.eland.service.SimilarService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;


import java.util.ArrayList;

@RestController
public class SimilarController {
    private static final org.apache.log4j.Logger logger = Logger.getLogger(SimilarController.class.getName());
    @Autowired
    SimilarService similarService;
    @Autowired
    SimilarDao similarDao;


    /**
     * 測試新增similar有資料的domain寫入資料庫
     *
     * @param domain
     */
    @GetMapping("add/domain/{domain}")
    public void similarUpdater(@PathVariable String domain) {
        if (domain == "") {
            return;
        }
        similarService.similarUpdate(domain);
    }

    /**
     * 補更新yyyyMMdd日期的domain
     *
     * @param date yyyyMMdd日期
     */
    @GetMapping("put/date/{date}")
    public void similarUpdaterDate(@PathVariable String date) {
        logger.info("update : "+date);
        long startTime = System.nanoTime();
        similarService.similarUpdateDate(date);
        long endTime = System.nanoTime();
        long duration = (endTime - startTime); // in nanoseconds
        logger.info("used nanosecond = " + duration);
    }

    /**
     * 每日更新
     */
    @GetMapping("add/daily")
    public void similarUpdaterDaily() {
        logger.info("update today ...");
        similarService.similarUpdateDaily();
    }

    @GetMapping("add/test")
    public void similarUpdaterAutoTest() {
        long startTime = System.nanoTime();
        similarService.similarUpdateT();
        long endTime = System.nanoTime();
        long duration = (endTime - startTime); // in nanoseconds
        logger.info("used nanosecond = " + duration);
    }

    /**
     * 查詢今日的不重複domain數量
     *
     * @param date
     */
    @GetMapping("get/count/{date}")
    public void dateCount(@PathVariable String date) {
        logger.info("check daily domain...");
        if (date == null) {
            date = similarService.getStatDate();
        }
        ArrayList urlList = similarService.getUrlList(date);
        ArrayList domainList = similarService.getDomainList(urlList);
        logger.info("dateCount=" + domainList.size());
    }


    @GetMapping("test/url")
    public String testUrl() {
        ArrayList<String> url = new ArrayList<>();
        int intIndex = -1;
        url.add(++intIndex, "http://localhost:8080/123");
        ArrayList<String> domainList = similarService.getDomainList(url);
        for (String domain : domainList) {
            System.out.println(domain);
            return domain;
        }
        return "";
    }

    @GetMapping("hello")
    public String Hello() {
        System.out.println("hello");
        return "hello";
    }
}
