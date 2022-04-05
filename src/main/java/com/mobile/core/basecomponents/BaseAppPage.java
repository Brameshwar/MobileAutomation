package com.mobile.core.basecomponents;

import com.mobile.core.config.AppiumDriverConfig;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.interactions.touch.TouchActions;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
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

    public BaseAppPage(BaseAppPage page){
        this.driver = page.driver;
        this.wait = page.wait;
    }

    protected AppiumDriver getDriver(){
        return this.driver;
    }

    public boolean checkIfExists(MobileElement element){
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
            if(checkIfExists(element)) {
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
            if(checkIfExists(element)) {
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

    public boolean enterTextAndClickEnter(MobileElement element,String value){
        try{
            if(checkIfExists(element)) {
                element.sendKeys(value);
                if(isAndroid){
                    AndroidDriver a = (AndroidDriver)getDriver();
                    a.pressKey(new KeyEvent(AndroidKey.ENTER));
                }
                else{
                    // Write logic to perform enter operation in iOSDriver
                }

                return true;
            } else
                return false;
        }
        catch (Exception exe){
            log.error("Failed to perform click on given element with exception s as {}",exe.getStackTrace());
            return false;
        }
    }

    public <P extends BaseAppPage> P getPage(Class<P> cls)  {

        P pageInstance = null;

        Constructor<T>[] constructors = (Constructor<T>[]) cls.getConstructors();
        for(Constructor c:constructors){
            Class<T>[] paramTypes = c.getParameterTypes();
            if(paramTypes.length == 1 && BaseAppPage.class.isAssignableFrom(paramTypes[0])) {
                try {
                    pageInstance = (P) c.newInstance(this);
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
        return  pageInstance;
    }
}
