package org.socialhistoryservices.delivery;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.RequestContext;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;

/**
 * Test RequestContextToViewInterceptor.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(RequestContextUtils.class)
public class RequestContextToViewInterceptorTest {

    private RequestContextToViewInterceptor interceptor;

    @Before
    public void setUp() {
        interceptor = spy(new RequestContextToViewInterceptor());
    }

    @Test
    public void testPostHandle() throws Exception {
        ModelAndView model = mock(ModelAndView.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        Object handler = mock(Object.class);

        mockStatic(RequestContextUtils.class);
        when(RequestContextUtils.getWebApplicationContext(request, null)).thenReturn(mock(WebApplicationContext.class));

        interceptor.postHandle(request, response, handler, model);

        verify(model).addObject(eq("rc"), any(RequestContext.class));
    }
}
