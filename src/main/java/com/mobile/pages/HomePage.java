package com.mobile.pages;


import com.mobile.core.basecomponents.BaseAppPage;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class HomePage  extends BaseAppPage<HomePage>{

    public HomePage(AppiumDriver driver) {
        super(driver);
    }

    @AndroidFindBy(xpath="Xpath for Android")
    private MobileElement flipKartLogo;

    @AndroidFindBy(xpath="Xpath for Android")
    private MobileElement searchText;

    @AndroidFindBy(xpath="Xpath for Android")
    private MobileElement searchIcon;
    
}
