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

package org.socialhistoryservices.delivery.reservation.controller;

import org.codehaus.jackson.JsonNode;
import org.socialhistoryservices.delivery.DeliveryTestCase;
import org.socialhistoryservices.delivery.record.entity.Holding;
import org.socialhistoryservices.delivery.reservation.entity.HoldingReservation;
import org.socialhistoryservices.delivery.reservation.entity.Reservation;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Integration unit test for Reservation API.
 */
public class ReservationControllerTest extends DeliveryTestCase {


    /**
     * Helper method to test if the json node contains the correct reservation.
     * @param rs The reservation to test.
     * @param node The JsonNode to test.
     */
    protected void validateReservation(Reservation rs, JsonNode node) {
        assertEquals(rs.getVisitorName(),
                     node.path("visitorName").getTextValue());
        assertEquals(rs.getVisitorEmail(),
                     node.path("visitorEmail").getTextValue());
        assertEquals(rs.getStatus().toString(),
                     node.path("status").getTextValue());
        assertEquals(df.format(rs.getDate()),
                     node.path("date").getTextValue());
        assertEquals(rs.getSpecial(),
                     node.path("special").getBooleanValue());
        assertEquals(rs.isPrinted(),
                     node.path("printed").getBooleanValue());
        checkIntIfNotNull("queueNo", rs.getQueueNo(), node);
        validateItems(rs.getHoldingReservations() , node.path("items"));
    }

    private void validateItems(List<HoldingReservation> hrs, JsonNode node) {
        Iterator<String> it = node.getFieldNames();
        int totalHoldings = 0;
        while (it.hasNext()) {
            String pid = it.next();
            JsonNode hNodes = node.path(pid);
            Iterator<JsonNode> it2 = hNodes.iterator();
            while (it2.hasNext()) {
                JsonNode hNode = it2.next();
                totalHoldings++;
                boolean has = false;
                for (HoldingReservation hr : hrs) {
                    Holding h = hr.getHolding();
                    if (h.getSignature().equals(hNode.getTextValue()) && h.getRecord().getPid().equals(pid)) {
                        has = true;
                    }
                }
                assertTrue(has);
            }
        }
        assertEquals(hrs.size(), totalHoldings);
    }

    /**
     * Helper method to test getting a single reservation.
     * @param rs Reservation to test.
     * @throws java.io.IOException Thrown when the servlet call fails.
     * @throws javax.servlet.ServletException Thrown when the servlet call
     * fails.
     */
    protected void validateGetReservation(Reservation rs)
            throws IOException, ServletException {
        MockHttpServletResponse response = mockJSONRequest(
                "GET", "/reservation/"+rs.getId(), null);
        assertEquals(200, response.getStatus());
        JsonNode root = parseJSON(response);

        validateReservation(rs, root);
    }


    /**
     * Test GET /reservation/[id].
     * @throws java.io.IOException Thrown when the servlet call fails.
     * @throws javax.servlet.ServletException Thrown when the servlet call
     * fails.
     */
    @Test
    public void testGetSingle() throws IOException, ServletException {
        // First try to get a non-existing reservation
        MockHttpServletResponse response = mockJSONRequest(
                "GET", "/reservation/0", null);
        assertEquals(404, response.getStatus());

        validateGetReservation(rsActive);
        validateGetReservation(rsCompleted);
    }

    /**
     * Test GET /reservation/.
     * @throws java.io.IOException Thrown when the servlet call fails.
     * @throws javax.servlet.ServletException Thrown when the servlet call
     * fails.
     */
    @Test
    public void testGetMultiple() throws IOException, ServletException {
        MockHttpServletResponse response = mockJSONRequest(
                "GET", "/reservation/", null);
        assertEquals(200, response.getStatus());
        JsonNode root = parseJSON(response);

        Iterator<JsonNode> it = root.iterator();
        assertTrue(it.hasNext());
        // Auto sorts on date desc, so rsActive comes first.
        validateReservation(rsActive, it.next());
        assertTrue(it.hasNext());
        validateReservation(rsCompleted, it.next());
    }

    /**
     * Test DELETE /reservation/[id] and POST /reservation/[id]!DELETE.
     * @throws java.io.IOException Thrown when the servlet call fails.
     * @throws javax.servlet.ServletException Thrown when the servlet call
     * fails.
     */
    @Test
    public void testDelete() throws IOException, ServletException {
        MockHttpServletResponse response;

        // Delete the record
        response = mockJSONRequest("DELETE", "/reservation/"+rsActive.getId(), null);
        assertEquals(200, response.getStatus());
        response = mockJSONRequest("GET", "/reservation/"+rsActive.getId(),  null);
        assertEquals(404, response.getStatus());
    }

    /**
     * Test editing reservations.
     * @throws java.io.IOException Thrown when the servlet call fails.
     * @throws javax.servlet.ServletException Thrown when the servlet call
     * fails.
     */
    @Test
    public void testEdit() throws IOException, ServletException {

        MockHttpServletResponse response;

        Reservation checkReservation = new Reservation();
        checkReservation.mergeWith(rsActive);
        checkReservation.setPermission(rsActive.getPermission());
        checkReservation.setQueueNo(rsActive.getQueueNo());
        checkReservation.setVisitorName("Wimpie");
        List<HoldingReservation> holdings = new ArrayList<HoldingReservation>();
        for (Holding h : rOpen2.getHoldings()) {
            if (h.getSignature().equals("Microfilm")) {
                HoldingReservation hr = new HoldingReservation();
                hr.setHolding(h);
                hr.setReservation(checkReservation);
                holdings.add(hr);
            }
        }
        checkReservation.setHoldingReservations(holdings);

        // Edit the reservation
        response = mockJSONRequest("PUT", "/reservation/"+rsActive.getId(),
            "{\"visitorName\": \"Wimpie\"," +
                    "\"items\": {\"12345.2\": [\"Microfilm\"]} }");
        assertEquals(200, response.getStatus());



        // Check that it was edited
        response = mockJSONRequest(
                "GET", "/reservation/"+rsActive.getId(), null);
        assertEquals(200, response.getStatus());
        JsonNode root = parseJSON(response);

        validateReservation(checkReservation, root);
    }

/**
     * Test creating new reservations.
     * @throws java.io.IOException Thrown when the servlet call fails.
     * @throws javax.servlet.ServletException Thrown when the servlet call
     * fails.
     */
    @Test
    public void testCreate() throws IOException, ServletException {

        MockHttpServletResponse response;

        Calendar cal = GregorianCalendar.getInstance();
        cal.add(Calendar.WEEK_OF_MONTH, 1);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);

        Reservation checkReservation = new Reservation();
        List<HoldingReservation> holdings = new ArrayList<HoldingReservation>();
        for (Holding h : rRestricted1.getHoldings()) {
            if (h.getSignature().equals("Microfilm")) {
                HoldingReservation hr = new HoldingReservation();
                hr.setHolding(h);
                hr.setReservation(checkReservation);
                holdings.add(hr);
            }
        }
        checkReservation.setHoldingReservations(holdings);
        checkReservation.setQueueNo(null);
        checkReservation.setVisitorName("Wimpie");
        checkReservation.setVisitorEmail("wimpie@example.com");
        checkReservation.setDate(cal.getTime());

        // Create the reservation
        response = mockJSONRequest("POST", "/reservation/",
            "{\"visitorName\": \"Wimpie\"," +
                    "\"visitorEmail\": \"wimpie@example.com\"," +
                    "\"date\": \""+ df.format(cal.getTime()) +"\"," +
                    "\"items\": {\"12345.3\": [\"Microfilm\"]} }");
        assertEquals(200, response.getStatus());

        // Check that it was created
        response = mockJSONRequest("GET", response.getRedirectedUrl(), null);
        assertEquals(200, response.getStatus());
        JsonNode root = parseJSON(response);

        validateReservation(checkReservation, root);
    }


}
