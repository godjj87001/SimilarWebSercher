package com.eland.controller;

import com.eland.WebCraw;
import com.eland.service.WebCrawlService;
import com.google.gson.Gson;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
public class WebCrawlController {
    private static final Logger logger = Logger.getLogger(WebCraw.class.getName());

    @Value("${driverPath}")
    String driverPath;
    @Value("${crawUrl}")
    String crawUrl;
    @Value("${browserPath}")
    String browserPath;

    @GetMapping("get/crawl/{domain}")
    public String getCrawlDomain(@PathVariable String domain) {
        WebCrawlService webCrawlService = new WebCrawlService();
        return webCrawlService.crawlDomain(domain,driverPath,crawUrl,browserPath);
    }

    @PostMapping("post/crawl/{domain}")
    public String postCrawlDomain(@PathVariable String domain) {
        WebCrawlService webCrawlService = new WebCrawlService();
        return webCrawlService.crawlDomain(domain,driverPath,crawUrl,browserPath);
    }

    public String crawlDomain(String domainName,String driverPath,String crawUrl,String browserPath) {
        System.setProperty("webdriver.gecko.driver", driverPath);
        FirefoxOptions options = new FirefoxOptions();
        //設置firefox瀏覽器位置，CentOS7不需要
//        options.setBinary(browserPath);
        //設置無頭、無沙盒模式、顯示器為1
        options.addArguments("headless");
        options.addArguments("no-sandbox");
        options.addArguments("--display=:1");
        options.addPreference("general.useragent.override", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/110.0.0.0 Safari/537.36 Edg/110.0.1587.69");


        WebDriver driver =new FirefoxDriver(options);

        driver.manage().window().maximize();
        crawUrl = crawUrl.replace("<domainName>", domainName);
        driver.get(crawUrl);
        //網站延遲5秒,程式60秒
        Duration timeout = Duration.ofSeconds(60);
        WebDriverWait wait = new WebDriverWait(driver, timeout);

        HashMap<String, String> ageDataMap = new HashMap<>();
        ArrayList<String> ageData = new ArrayList<>();
        HashMap<String, String> genderMap = new HashMap<>();
        ArrayList<String> genderData = new ArrayList<>();
        HashMap<String, Object> webMap = new HashMap<>();
        int i = 0;
        logger.info("開始爬蟲：" + domainName);
        logger.info("使用driver："+driverPath);
        try {

            //獲取性別標題
            List<WebElement> genderList = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("wa-demographics__gender-legend-item-title")));
            for (WebElement gender : genderList) {
                genderData.add(gender.getText());
            }

            //獲取性別百分比
            List<WebElement> genderPercentList = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("wa-demographics__gender-legend-item-value")));
            for (WebElement genderPercent : genderPercentList) {
                String genderText = genderPercent.getText();
                genderMap.put(genderData.get(i), genderText);
                i++;
            }

            //初始化次數i
            i = 0;

            //獲取年齡範圍
            List<WebElement> ageDataList = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                    By.cssSelector("text[style*='color: rgba(9, 37, 46, 0.6);']")));
            for (WebElement age : ageDataList) {
                String ageDataText = age.getText();
                if (ageDataText.matches("\\d+\\s*-\\s*\\d+|\\d+\\+")) {
                    ageData.add(ageDataText);
                }
            }
            //獲取年齡百分比
            List<WebElement> ageDataPercentList = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("wa-demographics__age-data-label")));
            JavascriptExecutor js = (JavascriptExecutor) driver;
            for (WebElement ageDataPercent : ageDataPercentList) {
                String text = (String) js.executeScript("return arguments[0].textContent", ageDataPercent);
                ageDataMap.put(ageData.get(i), text);
                i++;
            }

            webMap.put("domainName", domainName);
            webMap.put("age", ageDataMap);
            webMap.put("gender", genderMap);

        } catch (Exception e) {
            e.printStackTrace();
            logger.error(domainName + "searcher is failed" + e);
            //將來可新增dao到某張上傳失敗表到資料庫， 可以做補爬蟲
        } finally {
            driver.quit();
        }
        logger.info(domainName + "爬蟲完成" + "轉Json開始");


        Gson gson = new Gson();
        String webToJson;
        String message = "轉Json失敗";

        try {
            webToJson = gson.toJson(webMap);
        } catch (Exception e) {
            logger.error(message);
            return message;
        }
        message = ",轉Json成功";
        logger.info(domainName + message);
        logger.info(webToJson);
        return webToJson;
    }
}