package com.mobile.core.config;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

@Slf4j
@Getter
public class AppiumDriverConfig {

    private Integer appiumServerTimeOut;
    private Integer wedDriverWait;

    private static AppiumDriverConfig appiumDriverConfig = null;

    private AppiumDriverConfig() {}

    public static AppiumDriverConfig getInstance() {

        try {
            if (appiumDriverConfig == null) {
                appiumDriverConfig = new AppiumDriverConfig();
                Properties prop = new Properties();
                prop.load(new FileInputStream("src/main/resources/DriverDetails.properties"));
                appiumDriverConfig.appiumServerTimeOut = Integer.valueOf(prop.getProperty("APPIUM_SERVER_TIMEOUT"));
                appiumDriverConfig.wedDriverWait = Integer.valueOf(prop.getProperty("WEB_DRIVER_WAIT"));
            }

            return appiumDriverConfig;

        }
        catch (IOException exp){
            log.error("Exception occurred while initialing the as {}",exp.getStackTrace());
            return null;
        }

    }

}

