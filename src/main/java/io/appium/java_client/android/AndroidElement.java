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

package io.appium.java_client.android;

import com.google.common.collect.ImmutableMap;

import io.appium.java_client.FindsByAndroidUIAutomator;
import io.appium.java_client.MobileCommand;
import io.appium.java_client.MobileElement;

import org.openqa.selenium.WebDriverException;

import java.util.List;


public class AndroidElement extends MobileElement
    implements FindsByAndroidUIAutomator<MobileElement> {

    /**
     * @throws WebDriverException This method is not applicable with browser/webview UI.
     */
    @Override public MobileElement findElementByAndroidUIAutomator(String using)
        throws WebDriverException {
        return findElement("-android uiautomator", using);
    }

    /**
     * @throws WebDriverException This method is not applicable with browser/webview UI.
     */
    @Override public List<MobileElement> findElementsByAndroidUIAutomator(String using)
        throws WebDriverException {
        return findElements("-android uiautomator", using);
    }

    /**
     * This method replace current text value.
     * @param value a new value
     */
    @SuppressWarnings({"rawtypes", "unchecked"}) public void replaceValue(String value) {
        ImmutableMap.Builder builder = ImmutableMap.builder();
        builder.put("id", getId()).put("value", new String[] {value});
        execute(MobileCommand.REPLACE_VALUE, builder.build());
    }

    @Override
    public void setValue(String value) {
        replaceValue(value);
    }
}
