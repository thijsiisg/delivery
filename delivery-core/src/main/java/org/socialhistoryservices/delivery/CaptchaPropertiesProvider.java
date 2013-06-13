/**
 * Copyright (C) 2013 International Institute of Social History
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
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

package org.socialhistoryservices.delivery;

// Default java imports
import java.util.Properties;

// Spring imports
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.i18n.LocaleContextHolder;

/**
 * This class provides the properties to set when showing a reCaptcha.
 * @autor IISH
 * @version 1.0
 * @bug Due to an upstream problem not all properties work as expected
 */
public class CaptchaPropertiesProvider {

    // Properties defined in the properties file
    @Autowired
    @Qualifier("myCustomProperties")
    private Properties properties;

    // Only CAPTCHA properties
    private Properties reCaptchaProperties;

    /**
     * Default constructor, sets the language using the locale and sets properties
     * as defined in the properties file.
     */
    public CaptchaPropertiesProvider() {
        reCaptchaProperties = new Properties();
        reCaptchaProperties.setProperty("lang", LocaleContextHolder.getLocale().getLanguage());
        //TODO read the actual properties as defined in the file!
        //reCaptchaProperties.setProperty("theme", properties.getProperty("prop_reCaptchaTheme", "clean"));
        reCaptchaProperties.setProperty("theme", "clean");
    }

    /**
     * Set a custom property <br />
     * Add or change a property using this method.
     * @param key The key to use
     * @param value The value to assign
     */
    public void setProperty(String key, String value) {
        reCaptchaProperties.setProperty(key, value);
    }

    /**
     * Return the CAPTCHA properties, you can use those properties when you want
     * to show a reCAPTCHA on a webpage.
     */
    public Properties getProperties() {
        return reCaptchaProperties;
    }
}

