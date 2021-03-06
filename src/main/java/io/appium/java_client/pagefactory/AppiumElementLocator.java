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

package io.appium.java_client.pagefactory;

import static io.appium.java_client.pagefactory.ThrowableUtil.extractReadableException;
import static io.appium.java_client.pagefactory.ThrowableUtil.isInvalidSelectorRootCause;
import static io.appium.java_client.pagefactory.ThrowableUtil.isStaleElementReferenceException;


import com.dangdang.reader.client.core.DeviceOrientation;
import com.dangdang.reader.client.core.DeviceUtils;
import com.google.common.base.Function;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.pagefactory.locator.CacheableLocator;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.FluentWait;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

class AppiumElementLocator implements CacheableLocator {

    final boolean shouldCache;
    final By by;
    final TimeOutDuration timeOutDuration;
    final WebDriver originalWebDriver;
    private final SearchContext searchContext;
    private final WaitingFunction waitingFunction;
    private WebElement cachedElement;
    private List<WebElement> cachedElementList;
    /**
     * Creates a new mobile element locator. It instantiates {@link WebElement}
     * using @AndroidFindBy (-s), @iOSFindBy (-s) and @FindBy (-s) annotation
     * sets
     *
     * @param searchContext     The context to use when finding the element
     * @param by                a By locator strategy
     * @param shouldCache       is the flag that signalizes that elements which
     *                          are found once should be cached
     * @param duration          is a POJO which contains timeout parameters
     * @param originalWebDriver is an instance of WebDriver that is going to be
     *                          used by a proxied element
     */

    public AppiumElementLocator(SearchContext searchContext, By by, boolean shouldCache,
        TimeOutDuration duration, WebDriver originalWebDriver) {
        this.searchContext = searchContext;
        this.shouldCache = shouldCache;
        this.timeOutDuration = duration;
        this.by = by;
        this.originalWebDriver = originalWebDriver;
        waitingFunction = new WaitingFunction(this.searchContext);
    }

    private void changeImplicitlyWaitTimeOut(long newTimeOut, TimeUnit newTimeUnit) {
        originalWebDriver.manage().timeouts().implicitlyWait(newTimeOut, newTimeUnit);
    }

    // This method waits for not empty element list using all defined by
    private List<WebElement> waitFor() {
        // When we use complex By strategies (like ChainedBy or ByAll)
        // there are some problems (StaleElementReferenceException, implicitly
        // wait time out
        // for each chain By section, etc)
        try {
            changeImplicitlyWaitTimeOut(0, TimeUnit.SECONDS);
            FluentWait<By> wait = new FluentWait<>(by);
            wait.withTimeout(timeOutDuration.getTime(), timeOutDuration.getTimeUnit());
            return wait.until(waitingFunction);
        } catch (TimeoutException e) {
            return new ArrayList<>();
        } finally {
            changeImplicitlyWaitTimeOut(timeOutDuration.getTime(), timeOutDuration.getTimeUnit());
        }
    }

    /**
     * Find the element.
     */
    public WebElement findElement() {
        if (cachedElement != null && shouldCache) {
            return cachedElement;
        }
        List<WebElement> result = waitFor();
        if (result.size() == 0) {
            String message = "Can't locate an element by this strategy: " + by.toString();
            if (waitingFunction.foundStaleElementReferenceException != null) {
                throw new NoSuchElementException(message,
                    waitingFunction.foundStaleElementReferenceException);
            }
            throw new NoSuchElementException(message);
        }
        if (shouldCache) {
            cachedElement = result.get(0);
        }
        return result.get(0);
    }

    /**
     * Find the element list.
     */
    public List<WebElement> findElements() {
        if (cachedElementList != null && shouldCache) {
            return cachedElementList;
        }
        List<WebElement> result = waitFor();
        if (shouldCache) {
            cachedElementList = result;
        }
        return result;
    }

    @Override public boolean isLookUpCached() {
        return shouldCache;
    }

    @Override
    public By getLocator() {
        return by;
    }

    @Override
    public WebDriver getDriver() {
        if(searchContext instanceof WebDriver) {
            return (WebDriver) searchContext;
        }
        else if(searchContext instanceof Widget){
            return ((Widget)searchContext).getWrappedDriver();
        }

        return null;
    }


    // This function waits for not empty element list using all defined by
    private static class WaitingFunction implements Function<By, List<WebElement>> {
        private final SearchContext searchContext;
        Throwable foundStaleElementReferenceException;
        int nCount=0;
        int upTimes=0;
        boolean swipUp =true;

        private WaitingFunction(SearchContext searchContext) {
            this.searchContext = searchContext;
        }

        public List<WebElement> apply(By by) {
            List<WebElement> result = new ArrayList<>();
            Throwable shouldBeThrown = null;
            boolean isRootCauseInvalidSelector;
            boolean isRootCauseStaleElementReferenceException = false;
            foundStaleElementReferenceException = null;

            try {

                if(searchContext instanceof AndroidDriver) {
                    //超过3次向上滑动页面
                    if (nCount >=3) {

                        if(swipUp) {
                            DeviceUtils.swip((AppiumDriver) searchContext, DeviceOrientation.UP);
                            upTimes++;

                            if(upTimes==4){
                                swipUp=false;
                            }
                        }

                        if(swipUp ==false){
                            DeviceUtils.swip((AppiumDriver) searchContext, DeviceOrientation.DOWN);
                            upTimes--;
                            if(upTimes==0){
                                swipUp=true;
                            }
                        }



                        nCount = 0;
                    } else {
                        nCount++;
                    }
                }
                result.addAll(searchContext.findElements(by));

            } catch (Throwable e) {

                isRootCauseInvalidSelector = isInvalidSelectorRootCause(e);
                if (!isRootCauseInvalidSelector) {
                    isRootCauseStaleElementReferenceException = isStaleElementReferenceException(e);
                }

                if (isRootCauseStaleElementReferenceException) {
                    foundStaleElementReferenceException = extractReadableException(e);
                }

                if (!isRootCauseInvalidSelector & !isRootCauseStaleElementReferenceException) {
                    shouldBeThrown = extractReadableException(e);
                }
            }

            if (shouldBeThrown != null) {
                if (RuntimeException.class.isAssignableFrom(shouldBeThrown.getClass())) {
                    throw (RuntimeException) shouldBeThrown;
                }
                throw new RuntimeException(shouldBeThrown);
            }

            if (result.size() > 0) {
                nCount=0;
                upTimes=0;
                return result;
            } else {
                return null;
            }
        }
    }
}
