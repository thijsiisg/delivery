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

package org.iisg.delivery.reservation.service;

import org.iisg.delivery.record.entity.ExternalRecordInfo;
import org.iisg.delivery.record.entity.Holding;
import org.iisg.delivery.record.entity.Record;
import org.iisg.delivery.reservation.entity.HoldingReservation;
import org.iisg.delivery.reservation.entity.Reservation;
import org.krysalis.barcode4j.BarcodeDimension;
import org.krysalis.barcode4j.impl.code128.Code128Bean;
import org.krysalis.barcode4j.output.java2d.Java2DCanvasProvider;
import org.krysalis.barcode4j.tools.UnitConv;
import org.springframework.context.MessageSource;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.Line2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

/**
 * Represents a printable reservation.
 */
public class ReservationPrintable implements Printable {

    private Reservation reservation;
    private HoldingReservation[] holdingReservations;
    private Locale l;

    private Font normalFont;
    private Font boldFont;

    /** The source to get localized messages of. */
    private MessageSource msgSource;
    private Properties properties;

    private DateFormat df;

    private class DrawInfo {

        private int offsetX;
        private int offsetY;
        private int width;
        private int valueOffset;
        private Graphics2D g2d;

        public DrawInfo(Graphics2D g2d) {
            this.g2d = g2d;
        }

        public Graphics2D getG2d() {
            return g2d;
        }

        public int getOffsetX() {
            return offsetX;
        }

        public void setOffsetX(int offsetX) {
            this.offsetX = offsetX;
        }

        public int getOffsetY() {
            return offsetY;
        }

        public void setOffsetY(int offsetY) {
            this.offsetY = offsetY;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getValueOffset() {
            return valueOffset;
        }

        public void setValueOffset(int valueOffset) {
            this.valueOffset = valueOffset;
        }
    }

    /**
     * Construct the printable.
     * @param res The reservation to construct from.
     * @param mSource The message source to fetch localized messages.
     * @param format The date format to use.
     */
    public ReservationPrintable(Reservation res, MessageSource mSource,
                                DateFormat format, Properties prop) {
        reservation = res;
        properties = prop;
        // Add holdings from reservation to array for easy traversing.
        List<HoldingReservation> hrs = reservation.getHoldingReservations();
        holdingReservations = new HoldingReservation[hrs.size()];
        holdingReservations = hrs.toArray(holdingReservations);

        l = new Locale("en");
        msgSource = mSource;
        df = format;

        // Set the normal and bold font.
        normalFont = new Font("Arial", Font.PLAIN, 10);
        boldFont = normalFont.deriveFont(Font.BOLD);
    }

    /**
     * Prints one page for a reservation (one record per page).
     * @param g The graphics object to draw on.
     * @param pf The page format.
     * @param page The current page number.
     * @return Whether more pages are coming or not.
     * @throws PrinterException Thrown when something went wrong.
     */
    public int print(Graphics g, PageFormat pf, int page) throws
                                                        PrinterException {

        if (page >= holdingReservations.length) {
            return NO_SUCH_PAGE;
        }

        int pageWidth = (int)pf.getImageableWidth();
        int pageHeight = (int)pf.getImageableHeight();

        // Set the imageable area to correspond with the g2d draw area.
        Graphics2D g2d = (Graphics2D)g;
        g2d.translate(pf.getImageableX(), pf.getImageableY());

        int halfWidth = pageWidth/2;

        // Draw line through middle.
        Line2D lin = new Line2D.Float(halfWidth, 0, halfWidth, pageHeight);
        g2d.draw(lin);

        DrawInfo drawInfo = new DrawInfo(g2d);
        drawInfo.setValueOffset(80);
        int rightMargin = 20;
        // Draw all info to the g2d.
        for (int i = 1; i <= 2; i++) {

            drawInfo.setWidth(halfWidth - rightMargin);
            drawInfo.setOffsetX(halfWidth * (i-1) + 10);
            drawBarcode(drawInfo, holdingReservations[page].getHolding().getId());
            drawReservationInfo(drawInfo);
            drawInfo.setOffsetY(drawInfo.getOffsetY()+20);
            drawHoldingInfo(drawInfo, holdingReservations[page]);
        }
        // Tell the caller that this page is part of the printed document.
        return PAGE_EXISTS;
    }

    /**
     * Draws all reservation info.
     * @param drawInfo Draw offsets.
     */
    private void drawReservationInfo(DrawInfo drawInfo) {

        drawDate(drawInfo);
        drawCreationDate(drawInfo);
        // Disable this for now:
        // offsetY = drawQueueNo(g2d, pw, offsetX,
        // offsetY,
        // valueOffset);
        drawVisitorName(drawInfo);
    }

    /**
     * Draws the queue number.
     * @param drawInfo Draw offsets.
     */
    private void drawQueueNo(DrawInfo drawInfo) {
        if (reservation.getQueueNo() == null) {
            return;
        }
        String queueLabel = getMessage("reservation.queueNo",
                "Queue Number");

        drawKeyValue(drawInfo,
                queueLabel,
                String.valueOf(reservation.getQueueNo()));
    }

    /**
     * Draws the visitor's name.
     * @param drawInfo Draw offsets.
     */
    private void drawVisitorName(DrawInfo drawInfo) {
        String visitorNameLabel = getMessage("reservation.visitorName",
                "Visitor Name");
        drawKeyValue(drawInfo, visitorNameLabel,
                               reservation.getVisitorName());
    }

    /**
     * Draws the creation date of the reservation.
     * @param drawInfo Draw offsets.
     */
    private void drawCreationDate(DrawInfo drawInfo) {
        String dateLabel = getMessage("reservation.creationDate", "Creation");
        SimpleDateFormat spdf = new SimpleDateFormat( properties.getProperty("prop_dateFormat") +
                                                      " " + properties.getProperty("prop_timeFormat", "HH:mm:ss"));

        drawKeyValue(drawInfo, dateLabel, spdf.format(reservation.getCreationDate()));
    }

    /**
     * Draws the date of access.
     * @param drawInfo Draw offsets.
     */
    private void drawDate(DrawInfo drawInfo) {
        String dateLabel = getMessage("reservation.date", "Date");
        drawKeyValue(drawInfo, dateLabel, df.format(reservation.getDate()));
    }

    /**
     * Draw all holding info.
     * @param drawInfo Draw Offsets.
     * @param hr The holdingReservation to use for getting values from.
     */
    private void drawHoldingInfo(DrawInfo drawInfo,
                                HoldingReservation hr) {
        Holding h = hr.getHolding();
        Record r = h.getRecord();

        // Draw title + pid.
        drawTitle(drawInfo, r.getTitle());
        drawAuthor(drawInfo, r.getExternalInfo().getAuthor());
        //drawPid(drawInfo, r.getPid());

        // Draw location info.
        drawMaterialType(drawInfo, r.getExternalInfo().getMaterialType());
        drawInventory(drawInfo, r);
        drawComment(drawInfo, hr.getComment());
        drawType(drawInfo, h.getSignature());
        drawFloor(drawInfo, h.getFloor());
        drawDirection(drawInfo, h.getDirection());
        drawCabinet(drawInfo, h.getCabinet());
        drawShelf(drawInfo, h.getShelf());
        if (r.getRealRestrictionType() != Record.RestrictionType.OPEN) {
            drawInfo.setOffsetY(drawInfo.getOffsetY()+10);
            drawRestrictedNotice(drawInfo);
        }
    }

     /**
     * Draw the comment of the holding reservation (used to specify
     * years/numbers for serials).
     * @param drawInfo Draw offsets.
     * @param value The value to draw.
     */
    private void drawComment(DrawInfo drawInfo, String value) {
        String commentLabel = getMessage("holdingReservations.comment",
                "Comment");
        drawKeyValue(drawInfo, commentLabel, value);
    }

     /**
     * Draw the shelf of the location.
     * @param drawInfo Draw offsets.
     */
    private void drawRestrictedNotice(DrawInfo drawInfo) {
        String key = getMessage("record.restriction", "Restriction Details");
        String value = getMessage("print.accessGranted", "This item is not publicly available, but this visitor has been granted access.");

        drawKeyValue(drawInfo,key, value);
    }

     /**
     * Draw the shelf of the location.
     * @param drawInfo Draw offsets.
     * @param value The value to draw.
     */
    private void drawShelf(DrawInfo drawInfo, String value) {
        if (value == null) {
            return;
        }
        String shelfLabel = getMessage("holding.shelf",
                "Shelf");
        drawKeyValue(drawInfo, shelfLabel, value);
    }

    /**
     * Draw the direction of the location.
     * @param drawInfo Draw offsets.
     * @param value The value to draw.
     */
    private void drawDirection(DrawInfo drawInfo, String value) {
        String directionLabel = getMessage("holding.direction",
                "Direction");
        drawKeyValue(drawInfo, directionLabel, value);
    }

    /**
     * Draw the cabinet of the location.
     * @param drawInfo Draw offsets.
     * @param value The value to draw.
     */
    private void drawCabinet(DrawInfo drawInfo, String value) {
        if (value == null) {
            return;
        }
        String cabinetLabel = getMessage("holding.cabinet",
                "Cabinet");
        drawKeyValue(drawInfo, cabinetLabel, value);
    }

    /**
     * Draw the floor of the location.
     * @param drawInfo Draw offsets.
     * @param value The value to draw.
     */
    private void drawFloor(DrawInfo drawInfo, Integer value) {
        if (value == null) {
            return;
        }
        String floorLabel = getMessage("holding.floor", "Floor");
        drawKeyValue(drawInfo, floorLabel, String.valueOf(value));
    }

    /**
     * Draw the pid of the record.
     * @param drawInfo Draw offsets.
     * @param value The value to draw.
     */
    private void drawPid(DrawInfo drawInfo, String value) {
        drawKeyValue(drawInfo, "PID", value);
    }

   /**
     * Draw the material type.
     * @param drawInfo Draw offsets.
     * @param value The value to draw.
     */
    private void drawMaterialType(DrawInfo drawInfo,
                               ExternalRecordInfo.MaterialType
            value) {
        String typeLabel = getMessage("record.externalInfo.materialType",
                "Material Type");
        String val = getMessage("record.externalInfo.materialType." + value, "");
        drawKeyValue(drawInfo, typeLabel, val);
    }

    /**
     * Draws the item number.
     * @param drawInfo Draw offsets.
     * @param r Record to check.
     */
    private void drawInventory(DrawInfo drawInfo, Record r) {

        if (r.getExternalInfo().getMaterialType() != ExternalRecordInfo.MaterialType.ARCHIVE) {
            return;
        }
        String queueLabel = getMessage("record.inventory",
                "Inventory Nr");

        String pid = r.getPid();

        drawKeyValue(drawInfo, queueLabel, pid.substring(pid.lastIndexOf(".")+1));
    }

    /**
     * Draw the type of the location.
     * @param drawInfo Draw offsets.
     * @param value The value to draw.
     */
    private void drawType(DrawInfo drawInfo, String value) {
        String typeLabel = getMessage("holding.signature", "Signature");
        drawKeyValue(drawInfo, typeLabel, value);
    }

    /**
     * Draw the title of the record.
     * @param drawInfo Draw offsets.
     */
    private void drawTitle(DrawInfo drawInfo, String value) {
        String titleLabel = getMessage("record.title", "Title");
        drawKeyValue(drawInfo, titleLabel, value);
    }

    /**
     * Draw the author of the record (if applicable).
     * @param drawInfo Draw offsets.
     * @param value The value to draw.
     */
    private void drawAuthor(DrawInfo drawInfo, String value) {
        String authorLabel = getMessage("record.externalInfo.author", "Author");
        drawKeyValue(drawInfo, authorLabel, value);
    }



    /**
     * Draw a key value pair on the provided graphics object.
     * @param drawInfo Draw offsets.
     * @param key The key to use.
     * @param value The value to use.
     */
    private void drawKeyValue(DrawInfo drawInfo, String key, String value) {

        // Do not print key-value pairs with missing value.
        if (value == null || value.isEmpty()) {
            return;
        }
        Graphics2D g2d = drawInfo.getG2d();
        int x = drawInfo.getOffsetX();
        int y = drawInfo.getOffsetY();
        int width = drawInfo.getWidth();
        int offset = drawInfo.getValueOffset();

        // Draw key (word-wrap disabled)
        g2d.setFont(boldFont);
        FontMetrics fm = g2d.getFontMetrics();

        g2d.drawString(key + ":", x, y);

        // Draw value (word-wrap enabled)
        FontRenderContext frc = g2d.getFontRenderContext();

        AttributedString styledText = new AttributedString(value);
        styledText.addAttribute(TextAttribute.FONT, normalFont);

        AttributedCharacterIterator styledTextIterator = styledText.getIterator();
        LineBreakMeasurer measurer = new LineBreakMeasurer(styledTextIterator, frc);

        while (measurer.getPosition() < value.length()) {
            TextLayout textLayout = measurer.nextLayout(width - offset);
            textLayout.draw(g2d, x + offset, y );
            y += fm.getHeight();
        }
        drawInfo.setOffsetY(y);
    }


    /**
     * Draw a key value pair on the provided graphics object.
     * @param g2d The graphics object to draw on.
     * @param key The key to use.
     * @param value The value to use.
     * @param x The x coordinate to start drawing.
     * @param y The y coordinate to start drawing.
     * @param width The width of the graphics object.
     * @return The y coordinate to continue drawing.
     */
    private int drawKeyValue(Graphics2D g2d, String key, String value,
                              int x, int y, int width) {
        g2d.setFont(boldFont);
        int offset = g2d.getFontMetrics().stringWidth(key);
        return 0;//drawKeyValue(g2d, key, value, x, y, offset + 10, width);
    }


    /**
     * Draw a barcode on the provided graphics.
     * @param drawInfo Draw offsets.
     * @param number The barcode number to create a barcode from.
     */
    private void drawBarcode(DrawInfo drawInfo, int number) {
        String msg = String.format("%09d", number);
        final int dpi = 150;
        Code128Bean barcode = new Code128Bean();
        barcode.setModuleWidth(UnitConv.in2mm(5.0f / dpi));
        barcode.setBarHeight(30);
        barcode.setFontSize(12);

        // Align to the right of the page.
        BarcodeDimension dim = barcode.calcDimensions(msg);
        barcode.doQuietZone(true);
        barcode.setQuietZone(drawInfo.getWidth() + drawInfo.getOffsetX() - dim.getWidth() - 10);
        barcode.setVerticalQuietZone(0);


        Java2DCanvasProvider canvas = new Java2DCanvasProvider(drawInfo.getG2d(), 0);
        // Generate the barcode.
        barcode.generateBarcode(canvas, msg);
        drawInfo.setOffsetY((int)dim.getHeight() + 30);
    }

    /**
     * Get a localized message.
     * @param code The code of the message.
     * @param def The default to return.
     * @return The message if found, or the default.
     */
    protected String getMessage(String code, String def) {
        return msgSource.getMessage(code, null, def, l);
    }
}
