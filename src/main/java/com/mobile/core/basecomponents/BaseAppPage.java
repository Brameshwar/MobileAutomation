package com.mobile.core.basecomponents;

import com.mobile.core.config.AppiumDriverConfig;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.interactions.touch.TouchActions;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

@Slf4j
public class BaseAppPage<T extends BaseAppPage<T>> {

    protected final AppiumDriver driver;
    protected WebDriverWait wait = null;
    public boolean isAndroid;


    public BaseAppPage(final AppiumDriver driver){

        PageFactory.initElements(new AppiumFieldDecorator(driver, Duration.ofSeconds(30)),this);
        this.wait = new WebDriverWait(driver,  AppiumDriverConfig.getInstance().getWedDriverWait());
        this.driver = driver;

    }

    protected AppiumDriver getDriver(){
        return this.driver;
    }

    public boolean waitForElement(MobileElement element){
        try{
            wait.until(ExpectedConditions.elementToBeClickable(element));
            return true;
        }
        catch (Exception exe){
            log.error("Failed to check if element is clickable with exception s as {}",exe.getStackTrace());
            return false;
        }

    }

    public boolean click(MobileElement element){
        try{
            if(waitForElement(element)) {
                element.click();
                return true;
            }
            else
                return false;
        }
        catch (Exception exe){
            log.error("Failed to perform click on given element with exception s as {}",exe.getStackTrace());
            return false;
        }
    }


    public boolean singleTap(MobileElement element){
        try{
            if(waitForElement(element)) {
                new TouchActions(getDriver()).singleTap(element).perform();
                return true;
            } else
                return false;
        }
        catch (Exception exe){
            log.error("Failed to perform click on given element with exception s as {}",exe.getStackTrace());
            return false;
        }
    }

}