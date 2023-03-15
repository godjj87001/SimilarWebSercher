package com.eland.pojo;

import org.openqa.selenium.WebElement;

import java.util.HashMap;
import java.util.List;

public class WebCrawlEnity {
    private HashMap<String,String> dataMapGson ;
    private List<WebElement> WebElementList;

    public HashMap<String, String> getDataMapGson() {
        return dataMapGson;
    }

    public void setDataMapGson(HashMap<String, String> dataMapGson) {
        this.dataMapGson = dataMapGson;
    }
}
