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

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.support.RequestContext;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Inserts the request context attribute into the model. This class is needed
 * because there is no other way to achieve setting URLDecode to false when
 * retrieving context URIs.
 */
public class RequestContextToViewInterceptor extends HandlerInterceptorAdapter {

    /**
     * Expose the request context to the views.
     */
    public void postHandle(HttpServletRequest request,
            HttpServletResponse response, Object handler,
            ModelAndView model) {
        if (model != null) {
            UrlPathHelper uph = new UrlPathHelper();
            uph.setUrlDecode(false);
            uph.setDefaultEncoding("utf-8");
            RequestContext rc = new RequestContext(request);
            rc.setUrlPathHelper(uph);
            model.addObject("rc", rc);
        }
    }
}
