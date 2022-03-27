package com.mobile.pages;


import com.mobile.core.basecomponents.BaseAppPage;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import lombok.Getter;

@Getter
public class HomePage  extends BaseAppPage<HomePage>{

    public HomePage(AppiumDriver driver) {
        super(driver);
    }

    @AndroidFindBy(xpath="Xpath for Android")
    @iOSXCUITFindBy(xpath = "Xpath for iOS")
    private MobileElement firstElement;

}
