package org.openqa.selenium;

import org.openqa.selenium.support.pagefactory.ElementLocator;

/**
 * Created by cailianjie on 2016-6-27.
 */
public interface ElementLocatorEx extends ElementLocator {

    By getLocator();

    WebDriver getDriver();
}
