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

import org.springframework.web.util.UriUtils;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Compare the path urldecoded.
 */
public class UrlDecodedUrlPathHelper extends UrlPathHelper {


    /**
     * Compares the url decoded path instead of the encoded.
     * @param request The request.
     * @return The difference.
     */
    public String getPathWithinServletMapping(HttpServletRequest request) {
        String pathWithinApp = getPathWithinApplication(request);
		String servletPath = getServletPath(request);
        String decodedPathWithinApp;

        // If the URL decoded pathWithinApp is alright, return it.
        decodedPathWithinApp = URLDecoder.decode(pathWithinApp);

		if (decodedPathWithinApp.startsWith(servletPath)) {
			// Normal case: URI contains servlet path.
			return decodedPathWithinApp.substring(servletPath.length());
		}
		else {
			// Special case: URI is different from servlet path.
			// Can happen e.g. with index page: URI="/", servletPath="/index.html"
			// Use path info if available, as it indicates an index page within
			// a servlet mapping. Otherwise, use the full servlet path.
			String pathInfo = request.getPathInfo();
			return (pathInfo != null ? pathInfo : servletPath);
		}
    }
}
