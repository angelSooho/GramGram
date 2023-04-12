package com.example.mission_leesooho.base.initData;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Getter
    private static long lst_max;

    @Value("${likeableperson.lst-max}")
    private void setLst_max(long lst_max) {
        AppConfig.lst_max = lst_max;
    }
}
