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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailPreparationException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.ui.Model;

import java.util.Locale;

/**
 * Abstract class to be extended by mailers.
 */
public abstract class Mailer extends TemplatePreparation {
    /**
     * The object to use for sending mail.
     */
    @Autowired
    protected JavaMailSender mailSender;

    /**
     * Parse a template to a string.
     *
     * @param name  The name of the template.
     * @param model The model to pass to the template.
     * @return The template contents.
     * @throws org.springframework.mail.MailPreparationException Thrown when template parsing fails.
     */
    protected String templateToString(String name, Model model) throws MailPreparationException {
        try {
            return super.templateToString(name, model);
        } catch (TemplatePreparationException mpe) {
            throw new MailPreparationException(mpe);
        }
    }

    /**
     * Parse a template to a string providing the locale.
     *
     * @param name   The name of the template.
     * @param model  The model to pass to the template.
     * @param locale The locale to parse the template in.
     * @return The template contents.
     * @throws org.springframework.mail.MailPreparationException Thrown when template parsing fails.
     */
    protected String templateToString(String name, Model model, Locale locale) throws MailPreparationException {
        try {
            return super.templateToString(name, model, locale);
        } catch (TemplatePreparationException mpe) {
            throw new MailPreparationException(mpe);
        }
    }
}
