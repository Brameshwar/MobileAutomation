package com.mobile.core.basecomponents;

import com.mobile.core.annotations.TestInfo;
import com.mobile.core.config.AppiumDriverConfig;
import com.mobile.core.utils.SocketUtils;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.remote.AndroidMobileCapabilityType;
import io.appium.java_client.remote.MobileCapabilityType;
import io.appium.java_client.screenrecording.CanRecordScreen;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.MDC;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;


@Slf4j
public class BaseAppTest {

    private final ThreadLocal<AppiumDriver> appDriver = new ThreadLocal<>();
    private final ThreadLocal<Map<String,String>> testParams = new ThreadLocal<>();
    private final ThreadLocal<Boolean> isVideoRecording = new ThreadLocal<>();

    private boolean isLocal;
    private boolean captureVideo;
    private String videosLocation;


    protected <T extends BaseAppPage> T getPage(Class<T> cls){

        BaseAppPage baseAppPage = new BaseAppPage(appDriver.get());
        return (T) baseAppPage.getPage(cls);

    }

    @BeforeTest(alwaysRun = true)
    public void initTestNGTest(ITestContext iTestContext){

        Map<String, String> temp = iTestContext.getCurrentXmlTest().getAllParameters();

        if(temp.containsKey("isLocalRun"))
            isLocal = temp.get("isLocalRun").equalsIgnoreCase("true");
        if(temp.containsKey("isCaptureVideoEnabled"))
            captureVideo = temp.get("isCaptureVideoEnabled").equalsIgnoreCase("true");


        if(isLocal){
            if(captureVideo)
                videosLocation = System.getProperty("user.dir")+"/Videos";

            AppiumDriverLocalService appiumDriverLocalService = AppiumDriverLocalService.
                    buildService(new AppiumServiceBuilder()
                            .usingAnyFreePort()
                            .usingPort(4723)
                            .withStartUpTimeOut(AppiumDriverConfig.getInstance().getAppiumServerTimeOut()
                                    , TimeUnit.SECONDS));

            appiumDriverLocalService.start();

        }
        else
            throw new RuntimeException("Configuration is pending for remote Execution");

    }



    @BeforeMethod(alwaysRun = true)
    protected void initMethod(Method method, ITestContext iTestContext, Object[] methodParams, ITestResult iTestResult)
    throws Exception{
        clearThreadLocals();

        if(!isLocal){
            log.error("Configuration is pending for remote Execution");
            throw new RuntimeException("Configuration is pending for remote Execution");
        }

        TestInfo additionalTestInfo = method.getAnnotation(TestInfo.class);

        if( null!= additionalTestInfo){

            MDC.put("testCaseID", additionalTestInfo.testId());
            log.info("Starting the execution for test case ID {}", additionalTestInfo.testId());
            log.info("Test owner : {}", additionalTestInfo.testOwner());
            log.info("Test priority : {}", additionalTestInfo.priority());

        }
        else {

            log.error("TestInfo Annotation is missing for the test case");
            throw new RuntimeException("TestInfo Annotation is missing for the test case");

        }

        String baseDir = MDC.get("testOutputDir");
        String relativePath = MDC.get("testCaseId");
        StringBuilder logPath = new StringBuilder();
        logPath.append(baseDir);
        if(!(baseDir.endsWith("\\") || baseDir.endsWith("/"))){
            logPath.append("/");
        }
        logPath.append(relativePath).append("/");

        TestExecutionContext.getInstance().initContext(logPath.toString(),relativePath);

    }

    @AfterMethod(alwaysRun = true)
    public void tearDownMethod(ITestResult result, Method testMethod){

        try{
            saveExecutionVideo(result.getMethod().getMethodName());
            appDriver.get().quit();
        }catch (Exception e){}

        if(!result.isSuccess())
            log.error("Exception during the test case execution: {}", result.getThrowable());



    }

    private void saveExecutionVideo(String methodName){

        try{

            if(!videosLocation.isEmpty() && isVideoRecording.get()){

                String video = ((CanRecordScreen)getAppDriver()).stopRecordingScreen();
                byte[] decodedVideo = Base64.getMimeDecoder().decode(video);
                Path testVideoDir = Paths.get(videosLocation);
                Files.createDirectories(testVideoDir);
                Path testVideoFile = Paths.get(testVideoDir.toString(),String.format("%s.%s"),
                        methodName, "mp4");
                Files.write(testVideoFile,decodedVideo);
                log.info("Video save at {}",videosLocation);
            }

        }catch (Exception exe){
            log.error(""+exe.getStackTrace());
        }

    }

    private void clearThreadLocals(){

        try{
            appDriver.remove();
        }catch (Exception e){}
        try{
            isVideoRecording.remove();
        }catch (Exception e){}
        try{
            testParams.remove();
        }catch (Exception e){}
    }

    public AppiumDriver getAppDriver(){

        if(appDriver.get() instanceof  AppiumDriver){
            return  appDriver.get();
        }else{
            log.info("Initialing the Appium Driver");

        }
        return appDriver.get();
    }

    private void initLocalAppiumDriver(Map<String, String> testNGParams){
        try{

            DesiredCapabilities localCaps = getLocalCapabilities(testNGParams);
            if(testNGParams.get("platformName").equalsIgnoreCase("android")){
                AppiumDriver<MobileElement> temp = new AndroidDriver(localCaps);
                appDriver.set(temp);
            }
            else
                throw new RuntimeException("Configuration does not exist for platformName name "+ testNGParams.get("platformName"));

            if(captureVideo) {
                isVideoRecording.set(true);
                ((CanRecordScreen) getAppDriver()).startRecordingScreen();
            }

        }catch (Exception e){
            log.error("Failed to initialize the Appium Driver in Simulator");
        }

    }


    private DesiredCapabilities getLocalCapabilities(Map<String, String> testNGParams) throws IOException {

        String appPath = System.getProperty("user.dir").replace("/target","");
        String[] token = appPath.split("/");
        appPath = appPath.replace(token[token.length-1],"");

        DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
        desiredCapabilities.setCapability(MobileCapabilityType.DEVICE_NAME, testNGParams.get("deviceName"));
        desiredCapabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, testNGParams.get("platformName"));
        desiredCapabilities.setCapability(MobileCapabilityType.PLATFORM_VERSION, testNGParams.get("platformVersion"));
        desiredCapabilities.setCapability(MobileCapabilityType.APP,""+appPath+""+testNGParams.get("appLocation"));
        desiredCapabilities.setCapability("idleTimeOut",500);
        desiredCapabilities.setCapability("maxDuration",2500);


        if(testNGParams.get("platformName").equalsIgnoreCase("android")){
            desiredCapabilities.setCapability(MobileCapabilityType.APPLICATION_NAME,"UIAutomator2");
            desiredCapabilities.setCapability(AndroidMobileCapabilityType.SYSTEM_PORT, SocketUtils.nextFreePort(8200,8300));
            desiredCapabilities.setCapability(AndroidMobileCapabilityType.AVD, testNGParams.get("deviceName"));
            desiredCapabilities.setCapability(AndroidMobileCapabilityType.AVD_LAUNCH_TIMEOUT, 300000);
            desiredCapabilities.setCapability(AndroidMobileCapabilityType.AVD_READY_TIMEOUT, 300000);

            if(null!=testNGParams.get("appBasePackage")){
                desiredCapabilities.setCapability(AndroidMobileCapabilityType.APP_WAIT_ACTIVITY, testNGParams.get("appBasePackage"));
            }

        }
        else
            throw new RuntimeException("Configuration does not exist for platformName name "+ testNGParams.get("platformName"));

        return  desiredCapabilities;
    }


}
