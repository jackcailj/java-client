/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.appium.java_client.pagefactory.interceptors;

import com.dangdang.reader.client.core.LoggerUtils;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.pagefactory.ElementLocator;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public abstract class InterceptorOfAListOfElements implements MethodInterceptor {

    static Logger logger = Logger.getLogger(InterceptorOfAListOfElements.class);

    protected final ElementLocatorEx locator;

    public InterceptorOfAListOfElements(ElementLocator locator) {
        this.locator = (ElementLocatorEx) locator;
    }

    protected abstract Object getObject(List<WebElement> elements, Method method, Object[] args)
        throws InvocationTargetException, IllegalAccessException, InstantiationException, Throwable;

    /**
     * Look at
     * {@link net.sf.cglib.proxy.MethodInterceptor#intercept(Object, Method, Object[], MethodProxy)}
     */
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy)
        throws Throwable {
        if (Object.class.equals(method.getDeclaringClass())) {
            return proxy.invokeSuper(obj, args);
        }

        try{

            By by = locator.getLocator();
            logger.info("元素："+by.toString()+"\r\n执行操作："+method.getName());

            ArrayList<WebElement> realElements = new ArrayList<WebElement>();
            realElements.addAll(locator.findElements());
            return getObject(realElements, method, args);
        }catch (Exception e){
            logger.error("异常："+ LoggerUtils.getStrackTrace(e));

            /*//截图并保存
            TakesScreenshot takesScreenshot = (TakesScreenshot) locator.getDriver();
            File screenShot =takesScreenshot.getScreenshotAs(OutputType.FILE);
            String screenShotFullName = LoggerUtils.getLogsFile()+"_"+System.currentTimeMillis()+".jpg";
            FileUtils.copyFile(screenShot, new File(screenShotFullName));*/

            throw e;
        }
    }
}
