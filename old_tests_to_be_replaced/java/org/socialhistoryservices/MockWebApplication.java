/*
 * Copyright 2011 International Institute of Social History
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.socialhistoryservices;

import java.lang.annotation.*;

/**
 * Configures a mock.
 * Each test class (or parent class) using
 * {@link MockWebApplicationContextLoader} must be annotated with this.
 * http://tedyoung.me/2011/02/14/spring-mvc-integration-testing-controllers/
 * @author Ted Young
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface MockWebApplication {
        /**
         * The location of the webapp directory relative to your project.
         * For maven users, this is generally src/main/webapp (default).
         * @return webapp dir
         */
        String webapp();

        /**
         * The servlet name as defined in the web.xml.
         * @return name of the servlet
         */
        String name();
}

