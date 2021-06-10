package com.example.demo.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class KakaoUtil {
    @Value("${api-key}")
    private String key;

    public String getKey(){
        return key;
    }
    public void setKey(String key){
        this.key = key;
    }
}