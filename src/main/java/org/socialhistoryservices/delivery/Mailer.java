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

import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mail.MailPreparationException;
import org.springframework.mail.MailSender;
import org.springframework.ui.Model;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

/**
 * Abstract class to be extended by mailers.
 */
public abstract class Mailer {

    /** The object to use for sending mail. */
    @Autowired
    protected MailSender mailSender;

    /** The freemarker configuration holder. */
    @Autowired
    private FreeMarkerConfig fConfig;


    /** The source to get localized messages of. */
    @Autowired
    private MessageSource msgSource;

    /** The property files. */
    @Autowired
    @Qualifier("myCustomProperties")
    protected Properties properties;

    /**
     * Get a localized message.
     * @param code The code of the message.
     * @param def The default to return.
     * @param locale The locale to get the message in.
     * @return The message if found, or the default.
     */
    protected String getMessage(String code, String def, Locale locale) {
        return msgSource.getMessage(code, null, def, locale);
    }

    /**
     * Get a localized message.
     * @param code The code of the message.
     * @param def The default to return.
     * @return The message if found, or the default.
     */
    protected String getMessage(String code, String def) {
        return getMessage(code, def, LocaleContextHolder.getLocale());
    }



    /**
     * Parse a template to a string.
     * @param name The name of the template.
     * @param model The model to pass to the template.
     * @return The template contents.
     * @throws org.springframework.mail.MailPreparationException Thrown when
     * template parsing fails.
     */
    protected String templateToString (String name,
                                       Model model) throws
            MailPreparationException {
        return templateToString(name, model, LocaleContextHolder.getLocale());
    }

    /**
     * Parse a template to a string providing the locale.
     * @param name The name of the template.
     * @param model The model to pass to the template.
     * @param locale The locale to parse the template in.
     * @return The template contents.
     * @throws org.springframework.mail.MailPreparationException Thrown when
     * template parsing fails.
     */
    protected String templateToString(String name, Model model, Locale locale
                                      ) throws MailPreparationException {
        model.addAttribute("msgResolver", new EasyMessage(locale));

        Map map = new HashMap();
        CollectionUtils.mergePropertiesIntoMap(properties, map);
        model.addAllAttributes(map);

        try {
            return FreeMarkerTemplateUtils.processTemplateIntoString(
                    fConfig.getConfiguration().getTemplate(
                            name, locale), model);
        } catch (TemplateException ex) {
            throw new MailPreparationException(ex);
        } catch (IOException ex) {
            throw new MailPreparationException(ex);
        }
    }


    /**
     * Get a localized message easily, without need of the spring macro
     * context.
     */
    public class EasyMessage {

        private Locale locale;

        public EasyMessage(Locale l) {
            locale = l;
        }

        /**
         * Get a localized message.
         * @param code The code of the message.
         * @param def The default to return.
         * @return The message if found, or the default.
         */
        public String getMessage(String code, String def) {
            return msgSource.getMessage(code, null, def,
                    locale);
        }
    }

}
