package org.socialhistoryservices.utils;

import org.apache.log4j.Logger;

import java.io.*;
import java.text.MessageFormat;
import java.util.Properties;

/**
 * Property loader
 * <p/>
 * At the application startup this class will load the property values into a properties list.
 * The location of the property file is indicated with a VM property or environment variable. The value is
 * to be added to the constructor.
 * Required properties can be indicated in the 'expected' parameter. If the stated properties are not declared in
 * the properties file, the application will not start.
 *
 * @usage: Set the property file in a VM parameter: -D[projects.properties]=[/path/to/[filename].properties]
 * or environmental setting: APPLICATION_HOME=[/path/to/[filename].properties]
 * @author: Lucien van Wouw <lwo@iisg.nl>
 * @since 2011-07-28
 */
public class PropertiesLoader extends Properties {

    private static final long serialVersionUID = 2L;
    private final Logger log = Logger.getLogger(getClass());
    private String[] expected;
    private String propertyKey;

    public PropertiesLoader() {
    }

    /**
     * @param property A VM property key or environmental variable
     * @param expected Application's properties that must be declared in the property file
     */
    public PropertiesLoader(String property, String[] expected) {

        setExpected(expected);
        setPropertyKey(property);
    }

    private void load() {
        String sorProperties = "";
        InputStream inputStream = null;
        try {
            sorProperties = System.getProperty(propertyKey);
            if (sorProperties != null) {
                log.info("Found system property '" + propertyKey + "', resolved to " + new File(sorProperties).getCanonicalPath());
            }
            inputStream = getInputFromFile(sorProperties);
            if (inputStream == null) {
                log.info("System property '" + propertyKey + "' not found, checking environment for '" + propertyKey + "'.");
                sorProperties = System.getenv(propertyKey);
                if (sorProperties != null) {
                    log.info("Found env property '" + propertyKey + "', resolved to " + new File(sorProperties).getCanonicalPath());
                }
                inputStream = getInputFromFile(sorProperties);
            }
        } catch (Exception e) {
            log.fatal("Error in resolving file defined with " + sorProperties);
            System.exit(1);
        }

        if (inputStream == null)
            offerSolution(propertyKey);
        loadProperties(inputStream, sorProperties);
        checkExpected(expected);
    }

    private void offerSolution(String property) {
        log.fatal(
                "Configuration not available!\n" +
                        "Solutions:\n" +
                        "1) Start the JVM with parameter -D" + property.toLowerCase() + "=/path/to/[filename].properties\n" +
                        "2) Set the environment variable '" + property.toUpperCase() + "' to /path/to/filename.properties"
        );
        System.exit(1);
    }

    private void loadProperties(InputStream inputStream, String sorProperties) {

        try {
            load(inputStream);
        } catch (IOException e) {
            log.fatal("Unable to load '" + sorProperties + "'.properties' from input stream!");
            System.exit(1);
        }
    }

    private void checkExpected(String[] expected) {

        boolean complete = true;
        if (expected != null)
            for (String expect : expected) {
                String value = getProperty(expect);
                if (value == null) {
                    log.warn(MessageFormat.format("Missing property ''{0}''", expect));
                    complete = false;
                }
            }

        if (!complete) {
            log.fatal("Configuration properties incomplete. Check log of this class for warnings.");
            System.exit(1);
        }
    }

    private InputStream getInputFromFile(String filePath) {
        if (filePath != null) {
            try {
                log.info("Going to load properties from '" + filePath + "', resolved to " + new File(filePath).getCanonicalPath());
                return new FileInputStream(filePath);
            } catch (FileNotFoundException e) {
                throw new RuntimeException("No file found: " + filePath, e);
            } catch (IOException e) {
                throw new RuntimeException("IO exception on: " + filePath, e);
            }
        } else {
            return null;
        }
    }

    public void setExpected(String[] expected) {
        this.expected = expected;
    }

    public void setPropertyKey(String propertyKey) {
        this.propertyKey = propertyKey;
        load();
    }
}