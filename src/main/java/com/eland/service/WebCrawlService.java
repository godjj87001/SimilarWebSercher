package com.eland.service;

import com.eland.controller.WebCrawlController;
import com.google.gson.Gson;
import org.apache.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class WebCrawlService {
    private static final Logger logger = Logger.getLogger(WebCrawlController.class.getName());

    public String crawlDomain(String domain, String driverPath, String crawUrl, String browserPath, String httpProxy) {

        return crawl(domain, driverPath, crawUrl, browserPath, httpProxy);
    }

    /**
     * @param domainName  domain，例如yahoo.com.tw
     * @param driverPath  browser driver 例如 chromedriver
     * @param crawUrl     要爬的網站
     * @param browserPath 二進制瀏覽器位置，windows目前版本需要設定
     * @param httpProxy   application.properties設定proxy 目前用免費的
     * @return
     */
    public String crawl(String domainName, String driverPath, String crawUrl, String browserPath, String httpProxy) {
        System.setProperty("webdriver.gecko.driver", driverPath);
        FirefoxOptions options = new FirefoxOptions();
        //設置firefox瀏覽器位置
        options.setBinary(browserPath);
        //設置代理
        Proxy proxy = new Proxy();
        proxy.setHttpProxy(httpProxy);
        options.setCapability("proxy", proxy);
        //設置無頭、無沙盒模式、無gpu、顯示器為1
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-gpu");
//        options.addArguments("--display=:1");

        //新增身分useragent
        options.addPreference("general.useragent.override", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:47.0) Gecko/20100101 Firefox/47.0");

        WebDriver driver = new FirefoxDriver(options);

//        driver.manage().window().maximize();
        crawUrl = crawUrl.replace("<domainName>", domainName);
        try {
            driver.get(crawUrl);
        } catch (Exception e) {
            logger.error(domainName+" : driver啟動失敗 :" + e);
        }
        //網站延遲5秒,程式60秒
        Duration timeout = Duration.ofSeconds(60);
        WebDriverWait wait = new WebDriverWait(driver, timeout);

        HashMap<String, String> ageDataMap = new HashMap<>();
        ArrayList<String> ageData = new ArrayList<>();
        HashMap<String, String> genderMap = new HashMap<>();
        ArrayList<String> genderData = new ArrayList<>();
        HashMap<String, Object> webMap = new HashMap<>();
        //標題的計數，用於新增map使用
        int i = 0;
        logger.info("開始爬蟲：" + domainName);
        logger.info("使用driver：" + driverPath);
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
                //example : 18 - 34
                if (ageDataText.matches("\\d+\\s*-\\s*\\d+|\\d+\\+")) {
                    ageData.add(ageDataText);
                }
            }
            //獲取年齡百分比
            List<WebElement> ageDataPercentList = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("wa-demographics__age-data-label")));
            JavascriptExecutor js = (JavascriptExecutor) driver;
            for (WebElement ageDataPercent : ageDataPercentList) {
                String text = (String) js.executeScript("return arguments[0].textContent", ageDataPercent);
//                String data = ageDataPercent.getText();
                ageDataMap.put(ageData.get(i), text);
                i++;
            }
            webMap.put("domainName", domainName);
            webMap.put("age", ageDataMap);
            webMap.put("gender", genderMap);
        } catch (Exception e) {
            logger.error(domainName + "searcher is failed" + e);
        } finally {
            //關閉driver
            driver.quit();
        }

        Gson gson = new Gson();
        String webToJson;
        String message = "轉Json失敗";

        try {
            webToJson = gson.toJson(webMap);
        } catch (Exception e) {
            logger.error(message);
            return message;
        }

        return webToJson;
    }
}
