package org.socialhistoryservices.delivery.reproduction.service;

import org.socialhistoryservices.delivery.reproduction.entity.HoldingReproduction;
import org.socialhistoryservices.delivery.request.service.RequestPrintable;
import org.springframework.context.MessageSource;

import java.awt.*;
import java.text.DateFormat;
import java.util.Properties;

/**
 * Represents a printable reproduction.
 */
public class ReproductionPrintable extends RequestPrintable {

    /**
     * Construct the printable.
     *
     * @param hr      The holding reproduction to construct from.
     * @param mSource The message source to fetch localized messages.
     * @param format  The date format to use.
     */
    public ReproductionPrintable(HoldingReproduction hr, MessageSource mSource, DateFormat format, Properties prop) {
        super(hr, mSource, format, prop);
    }

    /**
     * Draws all reproduction info.
     *
     * @param drawInfo Draw offsets.
     */
    @Override
    protected void drawRequestInfo(DrawInfo drawInfo) {
        drawRepro(drawInfo);
        drawId(drawInfo);
        drawCreationDate(drawInfo);
    }

    /**
     * Draw the notice that this print is intended for repro.
     *
     * @param drawInfo Draw offsets.
     */
    private void drawRepro(DrawInfo drawInfo) {
        String printRepro = getMessage("print.repro", "Intended for repro");

        Graphics2D g2d = drawInfo.getG2d();
        g2d.setFont(boldFont);

        int x = drawInfo.getOffsetX();
        int y = drawInfo.getOffsetY();
        g2d.drawString(printRepro, x, y);
        y += MIN_LINE_HEIGHT;

        drawInfo.setOffsetY(y);
    }

    /**
     * Draws the name of the person making the request.
     *
     * @param drawInfo Draw offsets.
     */
    private void drawId(DrawInfo drawInfo) {
        HoldingReproduction hr = (HoldingReproduction) holdingRequest;
        String idLabel = getMessage("reproduction.id", "Reproduction");
        drawKeyValue(drawInfo, idLabel, String.valueOf(hr.getReproduction().getId()));
    }
}
