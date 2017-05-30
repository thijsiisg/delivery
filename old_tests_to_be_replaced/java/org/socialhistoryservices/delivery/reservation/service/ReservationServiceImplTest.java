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
