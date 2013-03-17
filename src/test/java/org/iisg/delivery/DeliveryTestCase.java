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

package org.iisg.delivery;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.iisg.MockWebApplication;
import org.iisg.MockWebApplicationContextLoader;
import org.iisg.delivery.api.RecordLookupService;
import org.iisg.delivery.permission.entity.Permission;
import org.iisg.delivery.permission.entity.RecordPermission;
import org.iisg.delivery.permission.service.PermissionService;
import org.iisg.delivery.record.entity.ExternalRecordInfo;
import org.iisg.delivery.record.entity.Holding;
import org.iisg.delivery.record.entity.Record;
import org.iisg.delivery.record.service.RecordService;
import org.iisg.delivery.reservation.entity.HoldingReservation;
import org.iisg.delivery.reservation.entity.Reservation;
import org.iisg.delivery.reservation.service.ReservationService;
import org.iisg.delivery.user.dao.AuthorityDAO;
import org.iisg.delivery.user.entity.User;
import org.iisg.delivery.user.service.UserService;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Test scaffold for use in integration test unit tests.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-context.xml",
        loader=MockWebApplicationContextLoader.class)
// Tip: If you want to run this test scaffold in a debugger such as the
// internal IntelliJ one, make sure the current directory of the test target is
// set to the directory where the Maven pom.xml is located.
@MockWebApplication(name="testApp",
        webapp="src/main/webapp")
@Transactional
public abstract class DeliveryTestCase {

    /** The record service to use. */
    @Autowired
    protected RecordService recordService;

    /** The reservation service to use. */
    @Autowired
    protected ReservationService reservationService;

    /** The permission service to use. */
    @Autowired
    protected PermissionService permissionService;

    /** The record lookup service to use. */
    @Autowired
    protected RecordLookupService lookupService;

    @Autowired
    @Qualifier("myCustomProperties")
    protected Properties properties;

    /** The user service to use. */
    @Autowired
    @Qualifier("userDetailsService")
    protected UserService userService;

    /** The DAO for adding user permissions. */
    @Autowired
    protected AuthorityDAO authorities;

    /** Mocked servlet to use to do fake HTTP calls. */
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    protected DispatcherServlet servlet;

    /** Test archive which is OPEN and AVAILABLE. */
    protected Record aOpen;

    /** Test record which is OPEN and AVAILABLE. */
    protected Record rOpen1;

    /** Test record which is OPEN and IN_USE. */
    protected Record rOpen2;

    /** Test record which is RESTRICTED and AVAILABLE. */
    protected Record rRestricted1;

    /** Test record which is RESTRICTED and AVAILABLE. */
    protected Record rRestricted2;

    /** Test record which is CLOSED and AVAILABLE. */
    protected Record rClosed;

    /** Test reservation which is ACTIVE. */
    protected Reservation rsActive;

    /** Test reservation which is COMPLETED. */
    protected Reservation rsCompleted;

    /** Test permission. */
    protected Permission permission1;

    /** Test permission. */
    protected Permission permission2;

    /** The default API date format used throughout the system. */
    protected SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * Check a string if not null (provided by json).
     * @param key The key of the node path to check.
     * @param value The value it should be.
     * @param node The node to use.
     */
    protected void checkStringIfNotNull(String key, String value, JsonNode node) {
        JsonNode n = node.path(key);
            assertEquals("Key value check for key '"+key+"' failed.", value,
                    n.getTextValue());
    }

    /**
     * Check an int if not null.
     * @param key The key of the node path to check.
     * @param value The value it should be.
     * @param node The node to use.
     */
    protected void checkIntIfNotNull(String key, Integer value,
                                     JsonNode node) {
        JsonNode n = node.path(key);
        if (!n.isMissingNode()) {
            assertEquals("Key value check for key '"+key+"' failed.", value,
                    new Integer(n.getIntValue()));
        } else {
            assertNull(value);
        }
    }

    /**
     * Helper method to do a JSON request to the API.
     * @param method HTTP method to use.
     * @param page The page to make a request to.
     * @param content Optional content to send.
     * @return The response.
     * @throws java.io.IOException Thrown when the servlet call fails.
     * @throws javax.servlet.ServletException Thrown when the servlet call
     * fails.
     */
    protected MockHttpServletResponse mockJSONRequest(String method,
                                                      String page,
                                                      String content) throws ServletException, IOException {
        MockHttpServletRequest request =
            new MockHttpServletRequest(method, page);

        if (content != null) {
            request.addHeader("Content-type", "application/json");
            request.setContent(content.getBytes());
        }
        else {
            request.addHeader("Accept", "application/json");
        }

        MockHttpServletResponse response;
        response = new MockHttpServletResponse();

        servlet.service(request, response);

        return response;
    }

    /**
     * Helper method to parse JSON from a response.
     * @param response The response.
     * @return The parsed json node.
     * @throws java.io.IOException Thrown when parsing the JSON fails.
     */
    protected JsonNode parseJSON(MockHttpServletResponse response)
            throws IOException {
        // Parse the Json
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(response.getContentAsString(),
                                         JsonNode.class);
    }

    protected void generateHoldingsForRecord(Record r) {
        Holding h = new Holding();
        h.setRecord(r);
        h.setSignature("Original");
        Holding h2 = new Holding();
        h2.setRecord(r);
        h2.setSignature("Microfilm");
        List<Holding> holdingList = new ArrayList<Holding>();
        holdingList.add(h);
        holdingList.add(h2);
        r.setHoldings(holdingList);
    }

    /**
     * Setup security context.
     */
    @Before
    public void setUpSecurityContext() {
        GrantedAuthority[] grantedAuthorities = new GrantedAuthority[]{new GrantedAuthorityImpl("ROLE_RECORD_CONTACT_VIEW")};
        UserDetails userDetails = new User();
        Authentication authentication = new TestingAuthenticationToken(userDetails, "password", grantedAuthorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }


    /**
     * Setup test data in database.
     */
    @Before
    public void setUpDataModel() {
        setUpRecords();
        setUpPermissions();
        setUpReservations();
    }

    private void setUpReservations() {
        // Active Reservation on Open Record 2
        rsActive = new Reservation();
        rsActive.setDate(new Date());
        rsActive.setVisitorEmail("john.doe@iisg.nl");
        rsActive.setVisitorName("John Doe");
        List<HoldingReservation> hrs = new ArrayList<HoldingReservation>();
        for (Holding h : rOpen2.getHoldings()) {
            HoldingReservation hr = new HoldingReservation();
            hr.setHolding(h);
            hr.setReservation(rsActive);
            hrs.add(hr);
        }
        rsActive.setHoldingReservations(hrs);
        rsActive.updateStatusAndAssociatedHoldingStatus(Reservation.Status.ACTIVE);
        reservationService.addReservation(rsActive);

        // Add a completed reservation on Open Record 2
        rsCompleted = new Reservation();
        Calendar cal = GregorianCalendar.getInstance();
        cal.add(Calendar.YEAR, -2);
        rsCompleted.setDate(cal.getTime());
        rsCompleted.setVisitorEmail("old.joe@iisg.nl");
        rsCompleted.setVisitorName("Old Joe");
        hrs = new ArrayList<HoldingReservation>();
        for (Holding h : rOpen1.getHoldings()) {
            HoldingReservation hr = new HoldingReservation();
            hr.setHolding(h);
            hr.setReservation(rsCompleted);
            hrs.add(hr);
        }
        rsCompleted.setHoldingReservations(hrs);
        rsCompleted.updateStatusAndAssociatedHoldingStatus(Reservation.Status
                .COMPLETED);
        reservationService.addReservation(rsCompleted);

    }

    private void setUpPermissions() {
        // Create two permission request on the restricted record.
        permission1 = new Permission();
        permission1.setName("John Doe Permission");
        permission1.setEmail("john.doe@iisg.nl");
        permission1.setAddress("Countryroad 1");
        permission1.setDateFrom(new Date());
        permission1.setDateTo(new Date());
        permission1.setResearchOrganization("Doe Research");
        permission1.setResearchSubject("Restricted Items");
        permission1.setStatus(Permission.Status.PENDING);
        permission1.setExplanation("None");
        RecordPermission rp1 = new RecordPermission();
        rp1.setRecord(rRestricted1);
        permission1.addRecordPermission(rp1);
        permissionService.addPermission(permission1);

        permission2 = new Permission();
        permission2.setName("John Doe Permission 2");
        permission2.setEmail("john.doe.2@iisg.nl");
        permission2.setAddress("Countryroad 2");
        permission2.setDateFrom(new Date());
        permission2.setDateTo(new Date());
        permission2.setResearchOrganization("Doe Research 2");
        permission2.setResearchSubject("Restricted Items 2");
        permission2.setStatus(Permission.Status.PENDING);
        permission2.setExplanation("None 2");
        RecordPermission rp2 = new RecordPermission();
        rp2.setRecord(rRestricted1);
        permission2.addRecordPermission(rp2);
        permissionService.addPermission(permission2);
    }

    private void setUpRecords() {
        // Open Archive
        aOpen = new Record();
        aOpen.setPid("12345");
        aOpen.setTitle("Open Archive");
        aOpen.setRestrictionType(Record.RestrictionType.OPEN);
        aOpen.getExternalInfo().setMaterialType(ExternalRecordInfo.MaterialType.ARCHIVE);
        recordService.addRecord(aOpen);

        // Open Record 1
        rOpen1 = new Record();
        rOpen1.setPid("12345.1");
        rOpen1.setTitle("Open Record 1");
        rOpen1.setRestrictionType(Record.RestrictionType.OPEN);
        rOpen1.setParent(aOpen);
        generateHoldingsForRecord(rOpen1);
        rOpen1.getExternalInfo().setMaterialType(ExternalRecordInfo
                .MaterialType.ARCHIVE);
        recordService.addRecord(rOpen1);

        // Open Record 2 (Status=IN_USE)
        rOpen2 = new Record();
        rOpen2.setPid("12345.2");
        rOpen2.setTitle("Open Record 2");
        rOpen2.setRestrictionType(Record.RestrictionType.OPEN);
        rOpen2.setParent(aOpen);
        generateHoldingsForRecord(rOpen2);
        rOpen2.getExternalInfo().setMaterialType(ExternalRecordInfo
                .MaterialType.ARCHIVE);
        recordService.addRecord(rOpen2);

        // Restricted Record 1
        rRestricted1 = new Record();
        rRestricted1.setPid("12345.3");
        rRestricted1.setTitle("Restricted item");
        rRestricted1.setRestrictionType(Record.RestrictionType.RESTRICTED);
        rRestricted1.setRestriction("Only available after contacting owner");
        rRestricted1.setParent(aOpen);
        generateHoldingsForRecord(rRestricted1);
        rRestricted1.getExternalInfo().setMaterialType(ExternalRecordInfo.MaterialType
                .ARCHIVE);
        recordService.addRecord(rRestricted1);

        // Restricted Record 2
        rRestricted2 = new Record();
        rRestricted2.setPid("12345.5");
        rRestricted2.setTitle("Restricted item 2");
        rRestricted2.setRestrictionType(Record.RestrictionType.RESTRICTED);
        rRestricted2.setRestriction("Only available after contacting owner");
        rRestricted2.setParent(aOpen);
        generateHoldingsForRecord(rRestricted2);
        rRestricted2.getExternalInfo().setMaterialType(ExternalRecordInfo.MaterialType
                .ARCHIVE);
        recordService.addRecord(rRestricted2);

        // Closed Record
        rClosed = new Record();
        rClosed.setPid("12345.4");
        rClosed.setTitle("Closed item");
        rClosed.setRestrictionType(Record.RestrictionType.CLOSED);
        rClosed.setParent(aOpen);

        // Item will automatically open 50 years from now
        Calendar cal = GregorianCalendar.getInstance();
        cal.add(Calendar.YEAR, 50);
        rClosed.setEmbargo(cal.getTime());
        rClosed.getExternalInfo().setMaterialType(ExternalRecordInfo.MaterialType
                .ARCHIVE);
        recordService.addRecord(rClosed);
    }

    /**
     * Remove test data from database.
     */
    @After
    public void tearDownDataModel() {
        // Remove records, permissions and reservations

    }
}
