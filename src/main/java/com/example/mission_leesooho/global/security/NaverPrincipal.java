package com.example.mission_leesooho.global.security;

import java.util.Map;

public class NaverPrincipal {

    private final Map<String, Object> attributes;

    public NaverPrincipal(Map<String, Object> attributes){
        this.attributes = attributes;
    }

    public String getProviderId() {
        return (String)attributes.get("id");
    }
}