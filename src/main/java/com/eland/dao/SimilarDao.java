package com.eland.dao;


import com.eland.pojo.SimilarEnum;
import com.eland.pojo.WebCrawlEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.lang.invoke.SwitchPoint;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

@Configuration
public class SimilarDao {
    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(SimilarDao.class.getName());
    @Autowired
    WebCrawlEntity webCrawlEntity;
    @Value("${driverDb}")
    String driver;
    @Value("${reportDb}")
    String reportDb;
    @Value("${mainDB}")
    String mainDB;
    @Value("${databaseUser}")
    String databaseUser;
    @Value("${password}")
    String password;

    /**
     * 關閉連線
     */
    private void close(Connection conn, ResultSet rs, PreparedStatement pstmt) {
        try {
            if (conn != null) {
                conn.close();
            }
            if (rs != null) {
                rs.close();
            }
            if (pstmt != null) {
                pstmt.close();
            }
        } catch (Exception e) {
            logger.error(e);
        }
    }

    /**
     * 連線DB
     *
     * @param dbName
     * @return
     */
    private Connection connectDatabase(String dbName) {
        Connection conn = null;
        try {
            Class.forName(driver);
            conn = DriverManager.getConnection(dbName, databaseUser, password);
        } catch (Exception e) {
            logger.error(e);
        }
        return conn;
    }

    /**
     * insert Domain
     *
     * @param status
     * @param domain
     * @return
     */
    public Boolean insertUpdateSimilarDomain(int status, String domain) {
        logger.info("insert similar_domain....");
        Connection conn = connectDatabase(reportDb);
        ResultSet rs = null;

        // 創建處理對象
        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement
                    ("INSERT INTO dmp_site_info.similar_domain(site_url,sitedata_status)" +
                            "values(?,?)");
            int slotIndex = 0;

            pstmt.setString(++slotIndex, domain);
            pstmt.setInt(++slotIndex, status);

            pstmt.executeUpdate();
            return true;
        } catch (Exception e) {
            logger.info(e);
        } finally {
            close(conn, rs, pstmt);
        }
        return false;
    }

    /**
     * 更新similar_domain
     *
     * @param status
     * @param domain
     * @return
     */
    public Boolean updateSimilarDomain(int status, String domain) {
        Connection conn = connectDatabase(reportDb);
        ResultSet rs = null;

        // 創建處理對象
        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement
                    ("update dmp_site_info.similar_domain set sitedata_status=? where site_url=?");
            int slotIndex = 0;
            pstmt.setInt(++slotIndex, status);
            pstmt.setString(++slotIndex, domain);

            pstmt.executeUpdate();
            return true;
        } catch (Exception e) {
            logger.info(e);
        } finally {
            close(conn, rs, pstmt);
        }
        return false;
    }

    /**
     * insertSimilar爬蟲的資料
     *
     * @param dataMap
     * @param domainSn 查找 similar_domain的流水號
     * @return
     */
    public Boolean insertSimilar(HashMap dataMap, int domainSn) {
        ResultSet res = null;
        Connection conn = connectDatabase(reportDb);
        // 創建處理對象
        PreparedStatement pstmt = null;
        double value;

        try {
            value = Double.valueOf(dataMap.get("value").toString());
        } catch (Exception e) {
            return false;
        }
        String tagString = ageReplaceString(dataMap.get("tag").toString());

        try {
            pstmt = conn.prepareStatement
                    ("INSERT INTO dmp_site_info.similar_site_distribution(similar_domain_sn,type,tag,value)" +
                            "values(?,?,?,?)");
            int slotIndex = 0;

            pstmt.setInt(++slotIndex, domainSn);
            pstmt.setString(++slotIndex, dataMap.get("type").toString());
            pstmt.setString(++slotIndex, tagString);
//            pstmt.setString(++slotIndex, dataMap.get("tag").toString());
            pstmt.setDouble(++slotIndex, value);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            logger.error("insert failed : " + e);

        } finally {
            close(conn, res, pstmt);
        }
        return false;
    }

    private String ageReplaceString(String tagString) {

        
        if (tagString.equals(SimilarEnum.ageEnum.RANGE1.getValue()) || tagString.equals(SimilarEnum.genderEnum.Male.name())) {
            tagString = "1";
        }
        if (tagString.equals(SimilarEnum.ageEnum.RANGE2.getValue()) || tagString.equals(SimilarEnum.genderEnum.Female.name())) {
            tagString = "2";
        }
        if (tagString.equals(SimilarEnum.ageEnum.RANGE3.getValue())) {
            tagString = "3";
        }
        if (tagString.equals(SimilarEnum.ageEnum.RANGE4.getValue())) {
            tagString = "4";
        }
        if (tagString.equals(SimilarEnum.ageEnum.RANGE5.getValue())) {
            tagString = "5";
        }
        if (tagString.equals(SimilarEnum.ageEnum.RANGE6.getValue())) {
            tagString = "6";
        }

        return tagString;
    }

    /**
     * insertSimilar爬蟲的資料
     *
     * @param dataMap
     * @param domainSn 查找
     * @return
     */
    public Boolean updateSimilar(HashMap dataMap, int domainSn) {
        ResultSet res = null;
        Connection conn = connectDatabase(reportDb);
        // 創建處理對象
        PreparedStatement pstmt = null;
        double value;
        try {
            value = Double.valueOf(dataMap.get("value").toString());
        } catch (Exception e) {
            return false;
        }
        String tagString = ageReplaceString(dataMap.get("tag").toString());
        try {
            //可考慮改INSERT ... ON DUPLICATE
            pstmt = conn.prepareStatement
                    ("update dmp_site_info.similar_site_distribution set value=? where similar_domain_sn=? and tag=?");
            int slotIndex = 0;
            pstmt.setDouble(++slotIndex, value);
            pstmt.setInt(++slotIndex, domainSn);
//            pstmt.setString(++slotIndex, dataMap.get("tag").toString());
            pstmt.setString(++slotIndex, tagString);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(conn, res, pstmt);
        }
        return false;
    }

    /**
     * 查詢dmp_stat的url
     *
     * @param date
     * @return urlList
     */
    public ArrayList<String> selectStat(String date) {
        Connection conn = connectDatabase(mainDB);
        ResultSet rs = null;
        // 創建處理對象
        PreparedStatement pstmt = null;
        ArrayList<String> urlList = new ArrayList<>();
        String sql = "SELECT DISTINCT url FROM dmp_stat." + date;
        try {
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                String url = rs.getString("url");
                urlList.add(url);
            }
            return urlList;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            close(conn, rs, pstmt);
        }
    }

    /**
     * 檢查similar_domain_sn是否存在
     *
     * @return similar_domain_sn
     */
    public HashMap<String, String> selectSimilarDomainMap(String url) {
        Connection conn = connectDatabase(reportDb);
        ResultSet rs = null;

        // 創建處理對象
        PreparedStatement pstmt = null;
        HashMap urlMap = new HashMap<>();
        String sql = "SELECT sn,site_url,update_time FROM dmp_site_info.similar_domain where site_url =?";
        try {

            pstmt = conn.prepareStatement(sql);
            int slotIndex = 0;
            pstmt.setString(++slotIndex, url);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                int sn = rs.getInt("sn");
                String siteUrl = rs.getString("site_url");
                String updateTime = rs.getString("update_time");
                urlMap.put("sn", sn);
                urlMap.put("siteUrl", siteUrl);
                urlMap.put("updateTime", updateTime);
            }
            return urlMap;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            close(conn, rs, pstmt);
        }
    }

    /**
     * 檢查similar_domain_sn是否存在
     *
     * @return siteUrl:similar_domain_sn
     */
    public HashMap<String, Integer> selectSimilarDomainSn() {
        Connection conn = connectDatabase(reportDb);
        ResultSet rs = null;

        // 創建處理對象
        PreparedStatement pstmt = null;
        HashMap<String, Integer> urlMap = new HashMap<>();
        String sql = "SELECT sn,site_url FROM dmp_site_info.similar_domain";
        try {
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                int sn = rs.getInt("sn");
                String siteUrl = rs.getString("site_url");
                urlMap.put(siteUrl, sn);
            }
            return urlMap;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            close(conn, rs, pstmt);
        }
    }

    /**
     * 查詢status
     *
     * @return siteUrl:status
     */
    public HashMap<String, Integer> selectSimilarDomainStatus() {
        Connection conn = connectDatabase(reportDb);
        ResultSet rs = null;

        PreparedStatement pstmt = null;
        HashMap<String, Integer> urlStatusMap = new HashMap<>();
        String sql = "SELECT site_url,sitedata_status FROM dmp_site_info.similar_domain";
        try {
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                int status = rs.getInt("sitedata_status");
                String siteUrl = rs.getString("site_url");
                urlStatusMap.put(siteUrl, status);
            }
            return urlStatusMap;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            close(conn, rs, pstmt);
        }
    }

    /**
     * 查詢site_url ,update_time
     *
     * @return site_url :update_time
     */
    public HashMap<String, String> selectSimilarDomainUpdate() {
        Connection conn = connectDatabase(reportDb);
        ResultSet rs = null;

        // 創建處理對象
        PreparedStatement pstmt = null;
        HashMap<String, String> urlUpdateMap = new HashMap<String, String>();
        String sql = "SELECT DISTINCT site_url,update_time FROM dmp_site_info.similar_domain";
        try {
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                String siteUrl = rs.getString("site_url");
                String updateTime = rs.getString("update_time");
                urlUpdateMap.put(siteUrl, updateTime);
            }
            return urlUpdateMap;
        } catch (SQLException e) {

            return null;
        } finally {
            close(conn, rs, pstmt);
        }
    }


    public HashMap<String, Integer> selectSimilarDataSn() {
        Connection conn = connectDatabase(reportDb);
        ResultSet rs = null;

        // 創建處理對象
        PreparedStatement pstmt = null;
        HashMap<String, Integer> domainSnMap = new HashMap<>();
        String sql = "SELECT DISTINCT sd.site_url,ssd.similar_domain_sn FROM dmp_site_info.similar_domain as sd " +
                "join dmp_site_info.similar_site_distribution as ssd ON sd.sn = ssd.similar_domain_sn";
        try {
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                String siteUrl = rs.getString("sd.site_url");
                int sn = rs.getInt("ssd.similar_domain_sn");
                domainSnMap.put(siteUrl, sn);
            }
            return domainSnMap;
        } catch (SQLException e) {

            return null;
        } finally {
            close(conn, rs, pstmt);
        }

    }
}
