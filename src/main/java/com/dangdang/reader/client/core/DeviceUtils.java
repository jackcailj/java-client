package com.dangdang.reader.client.core;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidKeyCode;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

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

}
