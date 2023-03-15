package com.eland;

import com.google.gson.Gson;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.apache.log4j.Logger;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WebCraw {
    private static final Logger logger = Logger.getLogger(WebCraw.class.getName());


    public static void main(String[] args) {

//        System.setProperty("webdriver.chrome.driver", "D:\\Tools\\\\chromedriver\\chromedriver.exe");
//        WebDriver driver = new ChromeDriver();
        // 设置EdgeDriver的路径

//        System.setProperty("webdriver.edge.driver", "D:/Tools/msedgedriver.exe");
//        EdgeOptions options = new EdgeOptions();
        System.setProperty("webdriver.gecko.driver", "D:/Tools/geckodriver.exe");
        FirefoxOptions options = new FirefoxOptions();

        options.setBinary("C:/Program Files/Mozilla Firefox/firefox.exe");
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-gpu");
        WebDriver driver =new FirefoxDriver(options);
//        WebDriver driver = new EdgeDriver(options);

        String domainName = "poya.com.tw";
        String url = "https://www.similarweb.com/zh-tw/website/" + domainName;
        driver.get(url);
        //網站延遲5秒，延遲300秒
        Duration timeout = Duration.ofSeconds(60);
        WebDriverWait wait = new WebDriverWait(driver, timeout);

        HashMap<String, String> ageDataMap = new HashMap<>();
        ArrayList<String> ageData = new ArrayList<>();
        HashMap<String, String> genderMap = new HashMap<>();
        ArrayList<String> genderData = new ArrayList<>();
        HashMap<String, Object> webMap = new HashMap<>();
        int i = 0;
        logger.info("開始爬蟲"+domainName);
        try {

            List<WebElement> genderList = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("wa-demographics__gender-legend-item-title")));
            for (WebElement gender : genderList) {
                genderData.add(gender.getText());
            }

            List<WebElement> genderPercentList = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("wa-demographics__gender-legend-item-value")));
            for (WebElement genderPercent : genderPercentList) {
                String genderText = genderPercent.getText();
                genderMap.put(genderData.get(i),genderText);
                i++;
            }

            //初始化次數i
            i=0;

            List<WebElement> ageDataList = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                    By.cssSelector("text[style*='color: rgba(9, 37, 46, 0.6);']")));
            for (WebElement age : ageDataList) {
                String ageDataText = age.getText();
                if (ageDataText.matches("\\d+\\s*-\\s*\\d+|\\d+\\+")) {
                    ageData.add(ageDataText);
                }
            }
            List<WebElement> ageDataPercentList = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("wa-demographics__age-data-label")));
            JavascriptExecutor js = (JavascriptExecutor) driver;
            for (WebElement ageDataPercent : ageDataPercentList) {
                String text = (String) js.executeScript("return arguments[0].textContent", ageDataPercent);
                ageDataMap.put(ageData.get(i), text);
                i++;
            }

            webMap.put("domainName",domainName);
            webMap.put("age", ageDataMap);
            webMap.put("gender", genderMap);

        } catch (Exception e) {
            e.printStackTrace();
            logger.error(domainName + "searcher is failed");
            //將來可新增dao到某張上傳失敗表到資料庫， 可以做補爬蟲
        } finally {
            driver.quit();
        }
        logger.info(domainName+"爬蟲完成"+"轉Json開始");


        Gson gson = new Gson();
        String webToJson = gson.toJson(webMap);

        logger.info(webToJson);
    }
}
