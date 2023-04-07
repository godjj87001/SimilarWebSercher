package com.eland.service;

import com.eland.dao.SimilarDao;
import com.eland.pojo.WebCrawlEntity;
import com.eland.pojo.gTLDEnum;
import com.google.gson.Gson;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

@Service
public class SimilarService {
    @Value("${apiUrl}")
    String apiUrl;
    @Value("${apiUrl}")
    String originalApiUrl;
    @Autowired
    SimilarDao similarDao;
    @Autowired
    WebCrawlEntity webCrawlEntity;
    private static final org.apache.log4j.Logger logger = Logger.getLogger(SimilarService.class.getName());

    /**
     * 抓取今日日期，取得昨日dmp_stat.url
     */
    public void similarUpdateDaily() {
        //獲取今日的stat
        String date = getStatDate();
        //獲得dmp_stat的url
        ArrayList<String> urlList = getUrlList(date);
        //url字串切割
        ArrayList<String> domainList = getDomainList(urlList);
        for (String domain : domainList) {
            //不是null才更新
            if (domain != null) {
                similarUpdate(domain);
            }
        }
    }

    /**
     * 輸入日期，更新dmp_stat_yyyyMMdd的資料
     *
     * @param date
     */
    public void similarUpdateDate(String date) {

        String pattern = "^\\d{4}(0[1-9]|1[012])(0[1-9]|[12][0-9]|3[01])$";
        if (!date.matches(pattern)) {
            logger.info("格式錯誤!");
        }
        //獲得dmp_stat的url
        ArrayList<String> urlList = getUrlList(date);
        //url字串切割
        ArrayList<String> domainList = getDomainList(urlList);
        for (String domain : domainList) {
            //不是null才更新
            if (domain != null) {
                similarUpdate(domain);
            }
        }
    }

    /**
     * 測試爬蟲+寫入
     */
    public String similarUpdateT() {
        //url字串切割
        ArrayList<String> domainList = new ArrayList<>();
//        domainList.add("poya.com.tw");
        domainList.add("abccar.com.tw");
//        domainList.add("168abc.net");
//        domainList.add("jeans.com");
//        domainList.add("10.20.90.5");

        for (String domain : domainList) {
            //不是null才更新
            if (domain != null) {
//                logger.info(domain);
                similarUpdate(domain);
            }
        }
        return "done";
    }

    /**
     * 初始化apiUrl
     */
    public void init() {
        apiUrl = originalApiUrl;
    }

    /**
     * apiUrl替換domain值
     * 使用callApi取值
     * status 網站狀態
     * domainSn 取出similar_domain的sn
     *
     * @param domain 切割字串後的domain
     */
    public void similarUpdate(String domain) {
        init();
        if (apiUrl.equals("") || apiUrl.equals(null)) {
            return;
        } else {
            apiUrl = apiUrl.replace("<domain>", domain);
        }
        //call爬蟲API
        WebCrawlEntity webData = callApi();

        //status=-1 ,domain
        int status = -1;
        //time out就存入domain -1
        if (webData == null) {
            logger.error(domain + " webData is null");
            updateSimilarSiteDistribution(domain, status, 0, null);
//            insertUpdateSimilarDomain(status, domain);
            return;
        }
        try {
            if (!webData.getGender().get("Female").contains("%")) {
                logger.error(domain + " data not found %");
                //爬不到資料,domain表狀態為0
                status = 0;
                updateSimilarSiteDistribution(domain, status, 0, null);
                return;
            }
        } catch (Exception e) {
            logger.error(domain + " catch data not found %");
            //爬不到資料,domain表狀態為0
            status = 0;
            updateSimilarSiteDistribution(domain, status, 0, null);
            return;
        }
        //status網站狀態
        //selectSimilarDomain<domain,sn>,找出sn 並寫入
        int domainSn = getDomainSn(domain);
        updateSimilarSiteDistribution(domain, status, domainSn, webData);

    }


    /**
     * 檢查updateTime是否是本月,不是則更新
     *
     * @param domain   domainUrl
     * @param status   網站狀態
     * @param domainSn 查詢資料庫的sn
     * @param webData  爬蟲的性別年齡資料
     */
    private void updateSimilarSiteDistribution(String domain, int status, int domainSn, WebCrawlEntity webData) {
        String updateTime = "19700101";
        try {
            // if domain沒重複則寫入
            if (selectSimilarDomainUpdate().containsKey(domain)) {
                //yyyy-MM-dd
                updateTime = selectSimilarDomainUpdate().get(domain);
                //轉換格式yyyyMM
                updateTime = formatUpdateTime(updateTime);
            }

            //yyyyMM
            String today = getYearMonth();
            //similar_domain.url沒有新增過,新增similar_domain,status=0
            if (updateTime.equals("19700101")) {
                status = 0;
                insertUpdateSimilarDomain(status, domain);
                //新增後重新尋找domainSn
                domainSn = getDomainSn(domain);

                //如果domainSn=0 , webData =null,就不新增similar_site_distribution
                if (domainSn > 0 && webData != null) {
                    status = 1;
                    updateSimilarDomain(status, domain);
                    insertUpdateSimilarData(webData, domainSn);
                }
                return;
            }
            //如果今天跟更新時間年+月不同,就更新
            if (updateTime.equals(today)) {
                // similar_site_distribution有值更新 status = 1
//                if (selectSimilarDataSn().containsKey(domain)) {
//                    status = 1;
//                } else {
//                    status = 0;
//                }
//                updateSimilarDomain(status+1, domain);
//                updateSimilarDomain(status, domain);
            } else {
                //如果更新時間不是本月
                //如果Domain有在similar_domain 則更新
                if (selectSimilarDomain().containsKey(domain)) {
                    logger.info("update...");
                    logger.info("domainSn=" + domainSn);
                    updateSimilarDomain(status, domain);
                } else {
                    //反之沒有在similar_domain 則新增
                    status = 1;
                    insertUpdateSimilarDomain(status, domain);
                }
                //取得domainSn
                domainSn = getDomainSn(domain);
                //如果domainSn=0 , webData =null,就不新增similar_site_distribution
                if (domainSn > 0 && webData != null) {
                    status = 1;
                    updateSimilarDomain(status+1, domain);
                    updateSimilarDomain(status, domain);
                    insertUpdateSimilarData(webData, domainSn);
                }
            }
        } catch (NullPointerException e) {
            logger.error("null...: " + e);
        } catch (Exception e) {
            logger.error("something wrong...: " + e);
        }
    }

    /**
     * 如果domain找不到回傳-1
     *
     * @param domain
     * @return domainSn
     */
    private int getDomainSn(String domain) {

        if (selectSimilarDomain().get(domain) == null) {
            return -1;
        }
        int domainSn = selectSimilarDomain().get(domain);
        return domainSn;
    }

    private String formatUpdateTime(String updateTime) {
        String updateTimeArr[] = updateTime.split("-");
        //yyyy+MM = yyyyMM
        updateTime = updateTimeArr[0] + updateTimeArr[1];
        return updateTime;
    }

    private String getYearMonth() {
        LocalDate today = LocalDate.now();
        String year = String.valueOf(today.getYear());
        String month = String.valueOf(today.getMonthValue());
        //2023/3   else 2023/12
        if (month.length() == 1) {
            month = "0" + month;
        }

        return year + month;
    }

    /**
     * call爬蟲的Api 如:apiUrl=localhost:8080/api/domain{domain}
     *
     * @return
     */
    private WebCrawlEntity callApi() {
        CloseableHttpClient client = null;
        CloseableHttpResponse response = null;
        BufferedReader bufferedReader;
        StringBuilder result;
        String line;
        try {
            // 使用Apache HttpClient發送HTTP請求
            client = HttpClients.createDefault();
            logger.info(apiUrl);
            HttpGet httpGet = new HttpGet(apiUrl);
            // 直到有數據或者 一分鐘
            boolean dataAvailable = false;
            int waitTime = 0;
            while (!dataAvailable && waitTime < 60) {
                response = client.execute(httpGet);
                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    dataAvailable = true;
                } else {
                    response.close();
                    Thread.sleep(1000);
                    waitTime++;
                }
            }

            // 從HTTP中獲取返回的字符串
            bufferedReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), StandardCharsets.UTF_8));
            result = new StringBuilder();

            while ((line = bufferedReader.readLine()) != null) {
                result.append(line);
            }
            // 將字符串轉換為Java對象
            Gson gson = new Gson();
            //將字符串存入WebCrawlEntity
            //            Map<String, Object> map = gson.fromJson(response.toString(), Map.class);
            WebCrawlEntity webData = gson.fromJson(result.toString(), WebCrawlEntity.class);

            return webData;
        } catch (Exception e) {
            logger.error("call API wrong:" + e);
            return null;
        } finally {
            // 關閉連接
            try {
                if (response != null) {
                    response.close();
                }
                if (client != null) {
                    client.close();
                }
            } catch (IOException e) {
                logger.error("close failed" + e);
            }
        }

    }

    /**
     * 檢查是否重複similar_site_distribution.sn
     *
     * @return sn
     */
    public HashMap<String, Integer> selectSimilarDataSn() {
        return similarDao.selectSimilarDataSn();
    }

    public HashMap<String, String> selectSimilarDomainMap(String url) {
        return similarDao.selectSimilarDomainMap(url);
    }

    /**
     * 檢查是否重複similar_domain.sn
     *
     * @return siteUrl:similar_domain_sn
     */
    public HashMap<String, Integer> selectSimilarDomain() {
        return similarDao.selectSimilarDomainSn();
    }

    /**
     * 查詢similar_domain更新時間
     *
     * @return site_url : update_time
     */
    public HashMap<String, String> selectSimilarDomainUpdate() {
        return similarDao.selectSimilarDomainUpdate();
    }

    /**
     * 搜尋similar_domain.status
     *
     * @return status 1=有 0=無
     */
    public HashMap<String, Integer> selectSimilarDomainStatus() {
        return similarDao.selectSimilarDomainStatus();
    }

    /**
     * 新增similar_domain
     *
     * @param status similar網站有無資料 0=無 , 1=有
     * @param domain
     */
    public void insertUpdateSimilarDomain(int status, String domain) {
        if (selectSimilarDomain().containsKey(domain)) {
            updateSimilarDomain(status, domain);
        }
        similarDao.insertUpdateSimilarDomain(status, domain);
    }

    /**
     * 更新similar_domain
     *
     * @param status similar網站有無資料 0=無 , 1=有
     * @param domain
     */
    public void updateSimilarDomain(int status, String domain) {
        similarDao.updateSimilarDomain(status, domain);
    }

    /**
     * 新增similar_site_distribution
     *
     * @param webData  爬蟲回傳的similar網站資料 status
     * @param domainSn
     */
    public void insertUpdateSimilarData(WebCrawlEntity webData, int domainSn) {
        HashMap dataMap;
        String type;
        //檢查是否有值
        if (selectSimilarDataSn().containsValue(domainSn)) {
            updateSimilarData(webData, domainSn);
        } else {
            for (Map.Entry<String, String> entry : webData.getGender().entrySet()) {
                type = "gender";
                dataMap = setDataMap(type, entry);
                similarDao.insertSimilar(dataMap, domainSn);
            }
            for (Map.Entry<String, String> entry : webData.getAge().entrySet()) {
                type = "age";
                dataMap = setDataMap(type, entry);
                similarDao.insertSimilar(dataMap, domainSn);
            }
        }
    }

    /**
     * 新增similar_site_distribution
     *
     * @param webData  爬蟲回傳的similar網站資料 status
     * @param domainSn
     */
    public void updateSimilarData(WebCrawlEntity webData, int domainSn) {
        HashMap dataMap;
        String type;
        for (Map.Entry<String, String> entry : webData.getGender().entrySet()) {
            type = "gender";
            dataMap = setDataMap(type, entry);
            similarDao.updateSimilar(dataMap, domainSn);
        }
        for (Map.Entry<String, String> entry : webData.getAge().entrySet()) {
            type = "age";
            dataMap = setDataMap(type, entry);
            similarDao.updateSimilar(dataMap, domainSn);
        }
    }

    /**
     * similar網站資料處理
     *
     * @param type
     * @param entry
     * @return
     */
    private HashMap setDataMap(String type, Map.Entry<String, String> entry) {
        //男性女性年齡
        String tag;
        //百分比值
        String value;
        tag = entry.getKey();
        value = entry.getValue();
        value = formatPercent(value);
        HashMap map = new HashMap<>();
        map.put("type", type);
        map.put("tag", tag);
        map.put("value", value);
        return map;
    }

    /**
     * value去除%只留數字
     *
     * @param value
     * @return
     */
    private String formatPercent(String value) {
        return value.replaceAll("[^\\d.]", "");
    }

    /**
     * 取得date的dmp_stat的url
     *
     * @param date 日期
     * @return urlList dmp_stat的url
     */
    public ArrayList<String> getUrlList(String date) {
        ArrayList urlList = similarDao.selectStat(date);
        return urlList;
    }

    /**
     * 將url字串切割成domain,再將domain放入containSet過濾重複,將set存入list,由於數據太大list.contain效能很慢,因此用set再存入
     *
     * @param urlList getUrlList() dmp_stat的url
     * @return domainList 去重複的similar爬文domain
     */
    public ArrayList<String> getDomainList(ArrayList<String> urlList) {

        HashSet<String> containSet = new HashSet<>();
        ArrayList<String> domainList = new ArrayList<>();
        String domain;
        for (String url : urlList) {
            //字串切割
            domain = getDomain(url);
            if (domain == null) {
                logger.info(url + "  url split failed:dmp_stat");
            }
            //過濾重複元素set
            containSet.add(domain);
        }
        domainList.addAll(containSet);
        return domainList;
    }

    /**
     * 檢查是否有http://localhost ,沒有就回傳subdomain+domain
     *
     * @param url
     * @return domain
     */
    private String getUrlSplit(String url) {
        String[] urlSplit = url.split("/");
        //http://xxxx.com/ 取出第三個 在urlSplit[2]
        //如果沒有http 就不處理
        if (urlSplit.length < 3 || !url.contains("http")) {
            return null;
        }
        String domain = null;
        domain = urlSplit[2];
        //localhost 不處理
        if (url.contains("localhost") || url.contains("127.0.0.1") || url.contains("10.0.2.2")) {
            return null;
        } else {
            return domain;
        }
    }

    /**
     * @param url
     * @return Similar_domain
     */
    private String getDomain(String url) {

        String domain = null;
        domain = getUrlSplit(url);
        //localhost不處理
        if (domain == null) {
            return null;
        }
        //分割port
        String[] domainSplitPort;
        //10.0.0.1:8080  >> 10.0.0.0 , 8080
        //檢查有無分號
        if (domain.contains(":")) {
            domainSplitPort = domain.split(":");
            domain = domainSplitPort[0];
        }
        String similarDomain = "";
        String[] domainSplit = domain.split("\\.");
        //TLD頂級域名,tw jp
        String TLD = domainSplit[domainSplit.length - 1];
        //(Generic Top Level Domain)通用頂級域名,com,net
        String gTLD = domainSplit[domainSplit.length - 2];


        int status = 0;
        //如果有國家取後三位 a.com.tw
        //
        try {
            //如果只有一個就null
            if (domainSplit.length < 2) {
                //檢查是否重複
                updateSimilarSiteDistribution(domain, status, 0, null);
                return null;
            }
            //如果TLD沒有英文就不是similarDomain, 直接寫入similarDomain
            if (!TLD.matches("[A-Za-z]+")) {
                //檢查是否重複
                updateSimilarSiteDistribution(domain, status, 0, null);
                return null;
            }
            if (domainSplit.length > 2) {
                for (gTLDEnum gTLDAll : gTLDEnum.values()) {
                    if (gTLD.equals(gTLDAll.toString())) {
                        similarDomain = domainSplit[domainSplit.length - 3] + "." + domainSplit[domainSplit.length - 2] + "." + domainSplit[domainSplit.length - 1];
                        return similarDomain;
                    }
                }
            }
            //沒有國家TLD就取二位a.net
            similarDomain = domainSplit[domainSplit.length - 2] + "." + domainSplit[domainSplit.length - 1];
        } catch (Exception e) {
            logger.error(url + ":dmp_stat url獲取失敗");
            logger.error(e);
        }
        return similarDomain;
    }

    /**
     * 獲取 昨日的日期
     * dmp_stat_yyyyMMdd會晚一天
     *
     * @return yyyyMMdd
     */
    public String getStatDate() {
        LocalDate today = LocalDate.now();
        //dmp_stat.yyyyMMdd = 今日-1
        LocalDate oneDaysAgo = today.minusDays(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String date = oneDaysAgo.format(formatter);
        return date;
    }
}
