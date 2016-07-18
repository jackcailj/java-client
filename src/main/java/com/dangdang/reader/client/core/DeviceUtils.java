package com.dangdang.reader.client.core;

import io.appium.java_client.*;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidKeyCode;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.pagefactory.bys.ContentMappedBy;
import io.appium.java_client.pagefactory.bys.ContentType;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.DoubleClickAction;

import java.io.File;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


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
    public static MobileElement iosScrollToVisible(ElementLocatorEx locatorEx, final MobileElement element){

        MobileElement returnElement=element;

        try {
            AppiumDriver driver= (AppiumDriver) locatorEx.getDriver();
            //ios设备,滚动到元素位置。
            if (driver instanceof IOSDriver) {
                if(!element.isDisplayed()){

                    String parentString = getScrollElementUIAutomationString(locatorEx);
                    JavascriptExecutor js = (JavascriptExecutor) driver;
                    js.executeScript(parentString);

                    returnElement = (MobileElement) locatorEx.findElement();

                }


                /*ContentMappedBy uiAutomationBy = (ContentMappedBy) by;
                By nativeBy = uiAutomationBy.getBy(ContentType.NATIVE_MOBILE_SPECIFIC);
                if (nativeBy instanceof MobileBy.ByIosUIAutomation) {
                    logger.info("执行scrollToVisible命令");
                    JavascriptExecutor js = (JavascriptExecutor) driver;
                    js.executeScript("UIATarget.localTarget().frontMostApp().mainWindow()" + uiAutomationBy.getByString(ContentType.NATIVE_MOBILE_SPECIFIC) + ".scrollToVisible()");
                }
*/
            }
            else {



                /*JavascriptExecutor js = (JavascriptExecutor) driver;
                HashMap scrollObject = new HashMap();
                scrollObject.put("direction", "down");
                js.executeScript("mobile: scroll", scrollObject);*/
/*

                Map<String,String> arg= new HashMap<String, String>() {{ put("element", element.getId()); }};

                JavascriptExecutor javascriptExecutor = (JavascriptExecutor) driver;
                javascriptExecutor.executeScript("mobile: scroll", arg);
*/

                //element.swipe(SwipeElementDirection.DOWN,1000);

            }

            //避免被顶部、底部标题栏挡住，移到中间,需要过滤掉上部和顶部标题栏?
            Dimension screenSize = driver.manage().window().getSize();
            Point location = returnElement.getLocation();
            Dimension elementSzie = returnElement.getSize();
            Point elementCenter = new Point(location.getX()+elementSzie.getWidth()/2,location.getY()+elementSzie.getHeight()/2);

            //屏幕下方低端，需要上移到中间
            Double bottomLine =screenSize.getHeight()*(9.0/10.0);
            Double upLine =screenSize.getHeight()*(1.0/10.0);
            if(elementCenter.getY()>bottomLine){
                logger.info("元素位于屏幕下方,可能被下方工具栏遮挡,移到中间");
                driver.swipe(screenSize.getWidth()/2,bottomLine.intValue(),screenSize.getWidth()/2,bottomLine.intValue()-(location.getY()-screenSize.getHeight()/2),1000);
                //returnElement = (MobileElement) locatorEx.findElement();
            }
            else if(elementCenter.getY()<upLine){//在顶端，往下滑。
                logger.info("元素位于屏幕上方,可能被下方工具栏遮挡,移到中间");
                driver.swipe(screenSize.getWidth()/2,upLine.intValue(),screenSize.getWidth()/2,upLine.intValue()+(screenSize.getHeight()/2-location.getY()),1000);
                //returnElement = (MobileElement) locatorEx.findElement();
            }


        }catch (Exception e){
            logger.error(LoggerUtils.getStrackTrace(e));
        }

        return returnElement;
    }


    /*
        获取locator中父层级路径,拼装成uiautomation滚动语句。
        目前只支持了cells,以后发现继续扩展。
     */
    public static String getScrollElementUIAutomationString(ElementLocatorEx locatorEx){
        ContentMappedBy uiAutomationBy = (ContentMappedBy) locatorEx.getLocator();
        By nativeBy = uiAutomationBy.getBy(ContentType.NATIVE_MOBILE_SPECIFIC);
        if (nativeBy instanceof MobileBy.ByIosUIAutomation) {

            Matcher matcher = Pattern.compile("(^.*?cells().*?)\\.").matcher(uiAutomationBy.getByString(ContentType.NATIVE_MOBILE_SPECIFIC));

            if(matcher.find()){
                String scrollString = "UIATarget.localTarget().frontMostApp().mainWindow()"+matcher.group(1)+".scrollToVisible()";
                return scrollString;
            }
        }

        return "UIATarget.localTarget().frontMostApp().mainWindow()";
    }


}
