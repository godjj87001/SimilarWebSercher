package com.eland.pojo;

import org.springframework.stereotype.Component;

import java.util.HashMap;


@Component
public class WebCrawlEntity {

    private HashMap<String, String> gender;

    public HashMap<String, String> getGender() {
        return gender;
    }

    public void setGender(HashMap<String, String> gender) {
        this.gender = gender;
    }

    public HashMap<String, String> getAge() {
        return age;
    }

    public void setAge(HashMap<String, String> age) {
        this.age = age;
    }

    private HashMap<String, String> age;


}
