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

package org.iisg.delivery.response;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/**
 * Base class to be used to extend controllers from which can throw special
 * runtime exceptions.
 */
public class ErrorHandlingController {




    @Autowired
    private SimpleDateFormat df;

    @Autowired
    @Qualifier("myCustomProperties")
    protected Properties properties;


    /**
     * Split a set of pids given in a url to an array of pids.
     * @param pids The combined pids (URL encoded).
     * @return Separate pids.
     */
    protected String[] getPidsFromURL(String pids) {
        try {
            return URLDecoder.decode(pids, "utf-8").split(properties.getProperty("prop_pidSeparator"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Date.class, new CustomDateEditor(df,
                true));
        binder.registerCustomEditor(String.class, new StringTrimmerEditor
                (true));
    }


    /**
     * Exception handler for showing invalid request exceptions in a
     * human-readable way.
     * @param exception The exception that was thrown.
     * @param response The response to send to the user.
     * @return The response body.
     */
    @ExceptionHandler(InvalidRequestException.class)
    @ResponseBody
    public String handleInvalid(Throwable exception, HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        return exception.getMessage();
    }

    @ExceptionHandler(JsonMappingException.class)
    @ResponseBody
    public String handleInvalidJson(Throwable exception,
                                    HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        return exception.getMessage();
    }


    /**
     * Parses json from a HTTP request body string.
     * @param json The JSON string to parse.
     * @return The JsonNode produced.
     * @throws InvalidRequestException Thrown when the body was not valid JSON.
     */
    protected JsonNode parseJSONBody(String json) throws InvalidRequestException {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(json, JsonNode.class);
        }
        catch (IOException e) {
            throw new InvalidRequestException("Invalid JSON: "+e.getMessage());
        }
    }

}
