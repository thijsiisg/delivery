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

import org.socialhistoryservices.delivery.request.service.RequestPrintable;
import org.socialhistoryservices.delivery.reservation.entity.HoldingReservation;
import org.springframework.context.MessageSource;

import java.text.DateFormat;
import java.util.Properties;

/**
 * Represents a printable reservation.
 */
public class ReservationPrintable extends RequestPrintable {

    /**
     * Construct the printable.
     * @param hr The holding reservation to construct from.
     * @param mSource The message source to fetch localized messages.
     * @param format The date format to use.
     */
    public ReservationPrintable(HoldingReservation hr, MessageSource mSource, DateFormat format, Properties prop) {
        super(hr, mSource, format, prop);
    }

    /**
     * Draw the reservation information.
     *
     * @param drawInfo    Draw offsets.
     * @param halfWidth   width of one half of the page
     * @param rightMargin margin on the right of the page.
     * @param i           1 = left side, 2 = right side
     */
    @Override
    protected void draw(DrawInfo drawInfo, int halfWidth, int rightMargin, int i) {
        super.draw(drawInfo, halfWidth, rightMargin, i);
        drawName(drawInfo);
    }

    /**
     * Draws all reservation info.
     * @param drawInfo Draw offsets.
     */
    @Override
    protected void drawRequestInfo(DrawInfo drawInfo) {
        drawName(drawInfo);
        drawDate(drawInfo);
        drawCreationDate(drawInfo);
        // Disable this for now:
        // offsetY = drawQueueNo(g2d, pw, offsetX,
        // offsetY,
        // valueOffset);
    }

    /**
     * Draws the queue number.
     * @param drawInfo Draw offsets.
     */
    private void drawQueueNo(DrawInfo drawInfo) {
        HoldingReservation hr = (HoldingReservation) holdingRequest;
        if (hr.getReservation().getQueueNo() == null) {
            return;
        }

        String queueLabel = getMessage("reservation.queueNo", "Queue Number");
        drawKeyValue(drawInfo, queueLabel, String.valueOf(hr.getReservation().getQueueNo()));
    }

    /**
     * Draws the date of access.
     * @param drawInfo Draw offsets.
     */
    private void drawDate(DrawInfo drawInfo) {
        HoldingReservation hr = (HoldingReservation) holdingRequest;
        String dateLabel = getMessage("reservation.date", "Date");
        drawKeyValue(drawInfo, dateLabel, df.format(hr.getReservation().getDate()));
    }
}
