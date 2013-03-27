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

package org.socialhistoryservices.delivery.permission.controller;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.socialhistoryservices.delivery.DeliveryTestCase;
import org.socialhistoryservices.delivery.permission.entity.RecordPermission;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.ServletException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import static org.junit.Assert.*;


/**
 * Integration unit test for Permission API.
 */
public class PermissionControllerTest extends DeliveryTestCase {

    /**
     * Test GET /permission/[id].
     * @throws java.io.IOException Thrown when the servlet call fails.
     * @throws javax.servlet.ServletException Thrown when the servlet call
     * fails.
     */
    @Test
    public void testGetSingle() throws IOException, ServletException {
        MockHttpServletRequest request;
        MockHttpServletResponse response;
        String content;


        // First try to get a non-existing reservation
        request = new MockHttpServletRequest("GET", "/permission/0");
        request.addHeader("Accept", "text/html");
        response = new MockHttpServletResponse();
        servlet.service(request, response);
        assertEquals(404, response.getStatus());

        // Try to get a JSONP response explicitly
        request = new MockHttpServletRequest("GET",
              "/permission/" +  permission1.getId());
        request.addHeader("Accept", "text/html");
        request.setParameter("callback", "tester");
        request.setParameter("format", "json");
        response = new MockHttpServletResponse();
        servlet.service(request, response);
        content = response.getContentAsString().trim();
        assertEquals(200, response.getStatus());
        assertEquals("application/json; charset=utf-8",
                response.getContentType());
        assertNotSame(0, content.length());
        assertTrue("start with tester(", content.startsWith("tester("));
        assertTrue("end with );", content.endsWith(");"));

        // Try to get JSON response implicitly
        request = new MockHttpServletRequest("GET",
              "/permission/" +  permission1.getId());
        request.addHeader("Accept", "application/json");
        response = new MockHttpServletResponse();
        servlet.service(request, response);
        content = response.getContentAsString().trim();
        assertEquals(200, response.getStatus());
        assertEquals("application/json; charset=utf-8",
                response.getContentType());
        assertNotSame(0, content.length());

        // Validate the JSON
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = null;
        root = mapper.readValue(content,
                                JsonNode.class);
        assertEquals(permission1.getName(),
                root.path("visitor_name").getTextValue());
        assertEquals(permission1.getEmail(),
                root.path("visitor_email").getTextValue());
        assertEquals(permission1.getStatus().toString(),
                root.path("status").getTextValue());
        assertEquals(df.format(permission1.getDateFrom()),
                root.path("from_date").getTextValue());
        assertEquals(df.format(permission1.getDateTo()),
                root.path("to_date").getTextValue());
        assertEquals(permission1.getAddress(),
                root.path("address").getTextValue());
        assertEquals(permission1.getExplanation(),
                root.path("explanation").getTextValue());
        assertEquals(permission1.getResearchOrganization(),
                root.path("research_organization").getTextValue());
        assertEquals(permission1.getResearchSubject(),
                root.path("research_subject").getTextValue());

        Iterator<JsonNode> it = root.path("items").getElements();
        for (RecordPermission rp : permission1.getRecordPermissions()) {
            // Check the lists are equal
            assertTrue(it.hasNext());
            JsonNode m = it.next();
            Iterator<JsonNode> it2 = m.getElements();
            assertTrue(it2.hasNext());
            assertEquals(rp.getRecord().getPid(), it2.next().getTextValue());
            assertTrue(it2.hasNext());
            assertEquals(rp.getGranted(), it2.next().getBooleanValue());
            assertFalse(it2.hasNext());
        }
    }

    /**
     * Test GET /permission/.
     * @throws java.io.IOException Thrown when the servlet call fails.
     * @throws javax.servlet.ServletException Thrown when the servlet call
     * fails.
     */
    @Test
    public void testGetMultiple() throws IOException, ServletException {
        MockHttpServletRequest request;
        MockHttpServletResponse response;
        String content;
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = null;
        Iterator<JsonNode> it;

        // TODO: Add more filter checks. Currently not all are covered.

        // Try to get a HTML response
        request = new MockHttpServletRequest("GET",
                "/permission/");
        request.addHeader("Accept", "text/html");
        response = new MockHttpServletResponse();
        servlet.service(request, response);
        assertEquals(200, response.getStatus());
        assertEquals("text/html; charset=utf-8", response.getContentType());
        assertNotSame(0, response.getContentAsString().length());

        // Setting format explicitly should also work
        request = new MockHttpServletRequest("GET",
              "/permission/");
        request.addHeader("Accept", "application/json");
        request.setParameter("format", "html");
        response = new MockHttpServletResponse();
        servlet.service(request, response);
        assertEquals(200, response.getStatus());
        assertEquals("text/html; charset=utf-8", response.getContentType());
        assertNotSame(0, response.getContentAsString().length());

        // Try to get a JSONP response explicitly
        request = new MockHttpServletRequest("GET",
              "/permission/");
        request.addHeader("Accept", "text/html");
        request.setParameter("callback", "tester");
        request.setParameter("format", "json");
        response = new MockHttpServletResponse();
        servlet.service(request, response);
        content = response.getContentAsString().trim();
        assertEquals(200, response.getStatus());
        assertEquals("application/json; charset=utf-8",
                response.getContentType());
        assertNotSame(0, content.length());
        assertTrue("start with tester(", content.startsWith("tester("));
        assertTrue("end with );", content.endsWith(");"));

        // Try to get JSON response implicitly with valid filter parameters
        request = new MockHttpServletRequest("GET",
              "/permission/");
        request.addHeader("Accept", "application/json");
        request.setParameter("page", "1");
        request.setParameter("page_len", "2");
        request.setParameter("sort", "visitor_name");
        request.setParameter("sort_dir", "desc");
        request.setParameter("visitor_email", "2");
        response = new MockHttpServletResponse();
        servlet.service(request, response);
        content = response.getContentAsString().trim();
        assertEquals(200, response.getStatus());
        assertEquals("application/json; charset=utf-8",
                response.getContentType());
        assertNotSame(0, content.length());

        // Validate the JSON
        root = mapper.readValue(content,
                                JsonNode.class);

        it = root.getElements();
        assertTrue(it.hasNext());
        JsonNode n = it.next();
            assertEquals(permission2.getName(),
                    n.path("visitor_name").getTextValue());
            assertEquals(permission2.getEmail(),
                    n.path("visitor_email").getTextValue());
            assertEquals(permission2.getStatus().toString(),
                    n.path("status").getTextValue());
            assertEquals(df.format(permission2.getDateFrom()),
                    n.path("from_date").getTextValue());
            assertEquals(df.format(permission2.getDateTo()),
                    n.path("to_date").getTextValue());
            assertEquals(permission2.getAddress(),
                    n.path("address").getTextValue());
            assertEquals(permission2.getExplanation(),
                    n.path("explanation").getTextValue());
            assertEquals(permission2.getResearchOrganization(),
                    n.path("research_organization").getTextValue());
            assertEquals(permission2.getResearchSubject(),
                    n.path("research_subject").getTextValue());

            Iterator<JsonNode> it2 = n.path("items").getElements();
            for (RecordPermission rp : permission2.getRecordPermissions()) {
                // Check the lists are equal
                assertTrue(it2.hasNext());
                JsonNode m = it2.next();
                Iterator<JsonNode> it3 = m.getElements();
                assertTrue(it3.hasNext());
                assertEquals(rp.getRecord().getPid(), it3.next().getTextValue());
                assertTrue(it3.hasNext());
                assertEquals(rp.getGranted(),it3.next().getBooleanValue());
                assertFalse(it3.hasNext());
            }
        assertFalse(it.hasNext());
    }

    /**
     * Test creating and editing new permissions.
     * @throws java.io.IOException Thrown when the servlet call fails.
     * @throws javax.servlet.ServletException Thrown when the servlet call
     * fails.
     */
    @Test
    public void testCreateEdit() throws IOException, ServletException {
        MockHttpServletResponse response;
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        // Find date today and tomorrow
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        String todayStr = df.format(cal.getTime());
        cal.add(Calendar.DAY_OF_YEAR, 1);
        String tomorrowStr = df.format(cal.getTime());

        // Check that requesting permission on a closed item fails.
        response = mockJSONRequest("POST", "/permission/",
            "{\"visitor_name\": \"Tester Test\","+
            " \"visitor_email\": \"test@example.com\","+
            " \"from_date\": \""+todayStr+"\","+
            " \"to_date\": \""+tomorrowStr+"\","+
            " \"status\": \"HANDLED\","+
            " \"explanation\": \"Test expl.\","+
            " \"address\": \"Test road\","+
            " \"research_subject\": \"Test Research\","+
            " \"research_organization\": \"Test Ltd.\","+
            " \"items\": [\"12345.4\"]}");
        assertEquals(400, response.getStatus());

         // Check that requesting permission on an open item fails.
        response = mockJSONRequest("POST", "/permission/",
            "{\"visitor_name\": \"Tester Test\","+
            " \"visitor_email\": \"test@example.com\","+
            " \"from_date\": \""+todayStr+"\","+
            " \"to_date\": \""+tomorrowStr+"\","+
            " \"status\": \"HANDLED\","+
            " \"explanation\": \"Test expl.\","+
            " \"address\": \"Test road\","+
            " \"research_subject\": \"Test Research\","+
            " \"research_organization\": \"Test Ltd.\","+
            " \"items\": [\"12345.2\"]}");
        assertEquals(400, response.getStatus());

        // Check that requesting permission without sufficient arguments fails.
        response = mockJSONRequest("POST", "/permission/",
            "{\"visitor_name\": \"Tester Test\","+
            " \"visitor_email\": \"test@example.com\","+
            " \"from_date\": \""+todayStr+"\","+
            " \"to_date\": \""+tomorrowStr+"\","+
            " \"status\": \"HANDLED\","+
            /*" \"explanation\": \"Test expl.\","+*/
            " \"address\": \"Test road\","+
            " \"research_subject\": \"Test Research\","+
            " \"research_organization\": \"Test Ltd.\","+
            " \"items\": [\"12345.3\"]}");
        assertEquals(400, response.getStatus());

        // Create a permission on a restricted item.
        response = mockJSONRequest("POST", "/permission/",
            "{\"visitor_name\": \"Tester Test\","+
            " \"visitor_email\": \"test@example.com\","+
            " \"from_date\": \""+todayStr+"\","+
            " \"to_date\": \""+tomorrowStr+"\","+
            /* Status is optional: " \"status\": \"HANDLED\","+*/
            " \"explanation\": \"Test expl.\","+
            " \"address\": \"Test road\","+
            " \"research_subject\": \"Test Research\","+
            " \"research_organization\": \"Test Ltd.\","+
            " \"items\": [\"12345.3\"]}");
        assertEquals(200, response.getStatus());

        String complRes = response.getRedirectedUrl();

        // Check removing and adding some items to the permission works.
        response = mockJSONRequest("PUT", complRes,
            "{\"visitor_name\": \"Tester Test 2\","+
            " \"visitor_email\": \"test@example2.com\","+
            " \"from_date\": \""+todayStr+"\","+
            " \"to_date\": \""+tomorrowStr+"\","+
            /* Status is optional: " \"status\": \"HANDLED\","+*/
            " \"explanation\": \"Test expl. 2\","+
            " \"address\": \"Test road 2\","+
            " \"research_subject\": \"Test Research 2\","+
            " \"research_organization\": \"Test Ltd2.\","+
            " \"items\": [[\"12345.5\", true]]}");
        assertEquals(200, response.getStatus());

        response = mockJSONRequest("GET", complRes, null);
        assertEquals(200, response.getStatus());
        assertNotSame(0, response.getContentAsString().length());
        JsonNode root = parseJSON(response);

        Iterator<JsonNode> it = root.path("items").getElements();
        assertTrue(it.hasNext());
        JsonNode n = it.next();
        assertEquals("12345.5", n.path(0).getTextValue());
        assertTrue(n.path(1).getBooleanValue());
        assertFalse(it.hasNext());

        // Test updating
        response = mockJSONRequest("POST", complRes + "!PUT",
            "{\"visitor_name\": \"Tester Test 2\","+
            " \"visitor_email\": \"test@example2.com\","+
            " \"from_date\": \""+todayStr+"\","+
            " \"to_date\": \""+tomorrowStr+"\","+
            /* Status is optional: " \"status\": \"HANDLED\","+*/
            " \"explanation\": \"Test expl. 2\","+
            " \"address\": \"Test road 2\","+
            " \"research_subject\": \"Test Research 2\","+
            " \"research_organization\": \"Test Ltd2.\","+
            " \"items\": [[\"12345.5\", false]]}");
        assertEquals(200, response.getStatus());

        response = mockJSONRequest("GET", complRes, null);
        assertEquals(200, response.getStatus());
        assertNotSame(0, response.getContentAsString().length());
        root = parseJSON(response);

        it = root.path("items").getElements();
        assertTrue(it.hasNext());
        n = it.next();
        assertEquals("12345.5", n.path(0).getTextValue());
        assertFalse(n.path(1).getBooleanValue());
        assertFalse(it.hasNext());
    }

    /**
     * Test DELETE /permission/[id] and POST /permission/[id]!DELETE.
     * @throws java.io.IOException Thrown when the servlet call fails.
     * @throws javax.servlet.ServletException Thrown when the servlet call
     * fails.
     */
    @Test
    public void testDelete() throws IOException, ServletException {
        MockHttpServletResponse response;

        // Delete one of the permissions.
        response = mockJSONRequest("DELETE", "/permission/" + permission1.getId(), null);
        assertEquals(200, response.getStatus());
        response = mockJSONRequest("GET", "/permission/" + permission1.getId(),
                null);
        assertEquals(404, response.getStatus());


        // Delete one of the permissions with !DELETE alternative
        response = mockJSONRequest("POST", "/permission/" +
                                           permission2.getId() +
                                           "!DELETE", null);
        assertEquals(200, response.getStatus());
        
        response = mockJSONRequest("GET", "/permission/" + permission2.getId(),
                null);
        assertEquals(404, response.getStatus());

    }
}
