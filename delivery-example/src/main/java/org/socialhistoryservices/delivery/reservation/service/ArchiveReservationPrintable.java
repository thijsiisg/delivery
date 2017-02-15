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

import org.socialhistoryservices.delivery.record.entity.ArchiveHoldingInfo;
import org.socialhistoryservices.delivery.record.entity.ExternalRecordInfo;
import org.socialhistoryservices.delivery.record.entity.Record;
import org.socialhistoryservices.delivery.reservation.entity.HoldingReservation;
import org.springframework.context.MessageSource;

import java.text.DateFormat;
import java.util.Properties;

/**
 * Represents a printable archive reservation.
 */
public class ArchiveReservationPrintable extends ReservationPrintable {

    /**
     * Construct the printable.
     * @param hr The holding reservation to construct from.
     * @param mSource The message source to fetch localized messages.
     * @param format The date format to use.
     */
    public ArchiveReservationPrintable(HoldingReservation hr, MessageSource mSource, DateFormat format, Properties prop) {
        super(hr, mSource, format, prop);
    }

    /**
     * Draw the archive reservation information.
     *
     * @param drawInfo Draw offsets.
     */
    @Override
    protected void draw(DrawInfo drawInfo) {
        drawBarcode(drawInfo, holdingRequest.getHolding().getId());

        drawName(drawInfo);
        drawDate(drawInfo);

        drawNewLine(drawInfo);

        drawArchive(drawInfo);
        drawInventoryNumber(drawInfo);
        drawRestrictedNotice(drawInfo);

        drawNewLine(drawInfo);

        drawHoldingInfo(drawInfo);

        drawInfo.setOffsetY(drawInfo.getHeight() - 100);

        drawReturnNotice(drawInfo);
    }

    /**
     * Draw the archive.
     *
     * @param drawInfo Draw offsets.
     */
    private void drawArchive(DrawInfo drawInfo) {
        Record record = getHoldingRequest().getHolding().getRecord();
        if (record.getParent() != null)
            record = record.getParent();

        String value = record.getTitle() + " (" + record.getHoldings().get(0).getSignature() + ")";
        drawValueNewLine(drawInfo, value, boldFont, false);
    }

    /**
     * Draw the inventory number.
     *
     * @param drawInfo Draw offsets.
     */
    private void drawInventoryNumber(DrawInfo drawInfo) {
        if (holdingRequest.getHolding().getRecord().getParent() != null) {
            String label = getMessage("record.inventory", "Inventory Nr");
            String inventoryNumber = holdingRequest.getHolding().getSignature();
            drawKeyValueNewLine(drawInfo, label, inventoryNumber);
        }
    }

    /**
     * Draw the restriction notice.
     *
     * @param drawInfo Draw restriction notice.
     */
    private void drawRestrictedNotice(DrawInfo drawInfo) {
        String key = getMessage("record.restriction", "Restriction");

        ExternalRecordInfo.Restriction restriction =
            holdingRequest.getHolding().getRecord().getExternalInfo().getRestriction();
        String restrictionValue = getMessage("record.externalInfo.restriction." + restriction, "");

        String permissionRequired = "";
        if (holdingRequest.getHolding().getRecord().getRestriction() == ExternalRecordInfo.Restriction.RESTRICTED) {
            permissionRequired = " (";
            permissionRequired += getMessage("record.restriction.required", "Permission required!");
            permissionRequired += ")";
        }

        drawKeyValueNewLine(drawInfo, key, restrictionValue + permissionRequired);
    }

    /**
     * Draw the archive holding info.
     *
     * @param drawInfo Draw offsets.
     */
    private void drawHoldingInfo(DrawInfo drawInfo) {
        Record record = holdingRequest.getHolding().getRecord();
        if (record.getParent() != null)
            record = record.getParent();

        boolean printNoMoreSpaceNotice = true;
        for (ArchiveHoldingInfo ahi : record.getArchiveHoldingInfo()) {
            int heightOneLine = determineHeight(drawInfo, "-", smallNormalFont);

            // Empty string: assumption that the first line will never exceed more than one line
            int heightFirstLine = 0;
            if ((ahi.getShelvingLocation() != null) || (ahi.getMeter() != null) || (ahi.getNumbers() != null)
                || (ahi.getFormat() != null))
                heightFirstLine = heightOneLine;

            int heightLineNote = 0;
            if (ahi.getNote() != null)
                heightLineNote = determineHeight(drawInfo, ahi.getNote(), smallNormalFont);

            // Only if there is still space, draw holding info
            int afterOffsetY = (drawInfo.getOffsetY() + heightFirstLine + heightLineNote);
            int maxOffsetY = (drawInfo.getHeight() - 100 - (heightOneLine * 2));
            if (afterOffsetY < maxOffsetY) {
                if (ahi.getShelvingLocation() != null) {
                    String label = getMessage("archiveHoldingInfo.shelvingLocation", "Location");
                    drawKeyValue(drawInfo, label, ahi.getShelvingLocation(), smallNormalFont, false, false);
                }

                if (ahi.getMeter() != null) {
                    String label = getMessage("archiveHoldingInfo.meter", "Meters");
                    drawKeyValue(drawInfo, label, ahi.getMeter(), smallNormalFont, false, false);
                }

                if (ahi.getNumbers() != null) {
                    String label = getMessage("archiveHoldingInfo.numbers", "Amount");
                    drawKeyValue(drawInfo, label, ahi.getNumbers(), smallNormalFont, false, false);
                }

                if (ahi.getFormat() != null) {
                    String label = getMessage("archiveHoldingInfo.format", "Format");
                    drawKeyValue(drawInfo, label, ahi.getFormat(), smallNormalFont, false, false);
                }

                if (ahi.getNote() != null) {
                    drawNewLine(drawInfo);
                    drawValue(drawInfo, ahi.getNote(), smallItalicFont, false);
                }

                drawNewLine(drawInfo);
                drawInfo.setOffsetY(drawInfo.getOffsetY() + 2);
            }
            else if (printNoMoreSpaceNotice) {
                printNoMoreSpaceNotice = false;
                String label = getMessage("archiveHoldingInfo.more", "");
                drawValue(drawInfo, label, smallBoldFont, true);
                drawNewLine(drawInfo);
            }
        }
    }
}
