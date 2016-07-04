package com.dangdang.reader.client.core;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileBy;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidKeyCode;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.pagefactory.bys.ContentMappedBy;
import io.appium.java_client.pagefactory.bys.ContentType;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.*;

import java.io.File;
import java.net.MalformedURLException;

/**
 * Created by cailianjie on 2016-6-2.
 */
public class DeviceUtils {

    static Logger logger = Logger.getLogger(DeviceUtils.class);



    /*
    滑动屏幕
     */
    public static void swip(AppiumDriver driver, DeviceOrientation orientation) throws MalformedURLException {

        logger.info("滑动一页，方向:"+orientation.toString());

        Dimension screenSize = driver.manage().window().getSize();

        Double startX=0d;
        Double startY=0d;
        Double endX=0d;
        Double endY=0d;

        if(orientation == DeviceOrientation.UP){
            endX=startX = Double.valueOf(screenSize.getWidth()/2);
            startY = screenSize.getHeight()*(3.0/4.0);
            endY = screenSize.getHeight()*(1.0/4.0);
        }
        else if(orientation == DeviceOrientation.DOWN){
            endX=startX = Double.valueOf(screenSize.getWidth()/2);
            startY = screenSize.getHeight()*(1.0/4.0);
            endY = screenSize.getHeight()*(3.0/4.0);
        }
        else if(orientation == DeviceOrientation.LEFT){
            endY=startY = Double.valueOf(screenSize.getHeight()/2);
            startX =  screenSize.getWidth()*(1.0/4.0);
            endX =  screenSize.getWidth()*(3.0/4.0);
        }

        else if(orientation == DeviceOrientation.RIGHT){
            endY=startY = Double.valueOf(screenSize.getHeight()/2);
            startX =  screenSize.getWidth()*(3.0/4.0);
            endX =  screenSize.getWidth()*(1.0/4.0);
        }

        driver.swipe(startX.intValue(),startY.intValue(),endX.intValue(),endY.intValue(),1000);

    }


    /*
    只有Ios、使用uiautomator方式时,滚动,将元素显示到屏幕上。ß
     */
    public static void iosScrollToVisible(AppiumDriver driver, ContentMappedBy by){
        //ios设备,滚动到元素位置。
        if(driver instanceof IOSDriver) {
            ContentMappedBy uiAutomationBy = (ContentMappedBy) by;
            By nativeBy = uiAutomationBy.getBy(ContentType.NATIVE_MOBILE_SPECIFIC);
            if (nativeBy instanceof MobileBy.ByIosUIAutomation) {
                logger.info("执行scrollToVisible命令");
                JavascriptExecutor js = (JavascriptExecutor) driver;
                js.executeScript("UIATarget.localTarget().frontMostApp().mainWindow()" + uiAutomationBy.getByString(ContentType.NATIVE_MOBILE_SPECIFIC) + ".scrollToVisible()");
            }
        }
    }


}
