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

package org.socialhistoryservices.delivery.reservation.service;

import org.socialhistoryservices.delivery.DeliveryTestCase;
import org.junit.Test;

import static org.junit.Assert.assertNull;

/**
 * Test class for ReservationServiceImpl.
 */
public class ReservationServiceImplTest extends DeliveryTestCase {


    /**
     * Test removing a reservation.
     */
    @Test
    public void testRemoveReservation() {
        // First test removing a completed reservation.
        reservationService.removeReservation(rsCompleted);
     //TODO:   assertEquals(Record.Status.IN_USE, rOpen2.getStatus());
        assertNull(reservationService.getReservationById(rsCompleted.getId()));

        // Now remove an active/pending reservation.
        reservationService.removeReservation(rsActive);
      //TODO:  assertEquals(Record.Status.AVAILABLE, rOpen2.getStatus());
        assertNull(reservationService.getReservationById(rsActive.getId()));


    }
}
