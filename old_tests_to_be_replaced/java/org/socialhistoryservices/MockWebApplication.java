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

