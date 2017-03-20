package org.socialhistoryservices.delivery;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Test UrlDecodedUrlPathHelper.
 */
public class UrlDecodedUrlPathHelperTest {

    private UrlDecodedUrlPathHelper helper;

    @Before
    public void setUp() {
       helper = spy(new UrlDecodedUrlPathHelper());
    }

    @Test
    public void testGetPathWithinServletMapping() throws Exception {

        HttpServletRequest request = mock(HttpServletRequest.class);
        final String path = "/record/12345%2F12345";
        final String decodedPath = "/record/12345/12345";
        doReturn(decodedPath).when(helper).getServletPath(request);
        doReturn(path).when(helper).getPathWithinApplication(request);

        assertEquals("Should be decoded correctly so the decoded path and servlet path are equal.", "", helper.getPathWithinServletMapping(request));

        // Test special case.
        String appPath = "/";
        String servletPath = "/index";
        doReturn(servletPath).when(helper).getServletPath(request);
        doReturn(appPath).when(helper).getPathWithinApplication(request);
        assertEquals("Should equal servlet path.", servletPath, helper.getPathWithinServletMapping(request));
    }
}
