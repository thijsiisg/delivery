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
     * @param drawInfo Draw offsets.
     */
    @Override
    protected void draw(DrawInfo drawInfo) {
        drawBarcode(drawInfo, holdingRequest.getHolding().getId());

        drawName(drawInfo);
        drawDate(drawInfo);
        drawCreationDate(drawInfo);

        drawNewLine(drawInfo);

        drawRecordInfo(drawInfo);
        drawMaterialInfo(drawInfo);
        drawType(drawInfo, holdingRequest.getHolding().getSignature());
        drawLocationInfo(drawInfo);

        drawInfo.setOffsetY(drawInfo.getHeight() - 100);

        drawReturnNotice(drawInfo);
    }

    /**
     * Draws the date of access.
     * @param drawInfo Draw offsets.
     */
    protected void drawDate(DrawInfo drawInfo) {
        HoldingReservation hr = (HoldingReservation) holdingRequest;
        String dateLabel = getMessage("reservation.date", "Date");
        drawKeyValueNewLine(drawInfo, dateLabel, df.format(hr.getReservation().getDate()));
    }
}
