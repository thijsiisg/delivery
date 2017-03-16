package org.socialhistoryservices.delivery.reproduction.service;

import org.socialhistoryservices.delivery.config.DeliveryProperties;
import org.socialhistoryservices.delivery.reproduction.entity.HoldingReproduction;
import org.socialhistoryservices.delivery.reproduction.entity.Order;
import org.socialhistoryservices.delivery.reproduction.entity.Reproduction;
import org.socialhistoryservices.delivery.reproduction.entity.ReproductionStandardOption;
import org.socialhistoryservices.delivery.request.service.RequestPrintable;
import org.springframework.context.MessageSource;

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
    public ReproductionPrintable(HoldingReproduction hr, MessageSource mSource, DateFormat format, DeliveryProperties prop) {
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
        drawPayed(drawInfo);

        drawInfo.setOffsetY(drawInfo.getOffsetY() + MIN_LINE_HEIGHT);
        drawName(drawInfo);
        drawEmail(drawInfo);
    }

    /**
     * Draw all holding info.
     *
     * @param drawInfo Draw Offsets.
     */
    @Override
    protected void drawHoldingInfo(DrawInfo drawInfo) {
        drawRecordInfo(drawInfo);
        drawMaterialInfo(drawInfo);
        drawHoldingPid(drawInfo, holdingRequest.getHolding().determinePid());
        drawType(drawInfo, holdingRequest.getHolding().getSignature());
        drawLocationInfo(drawInfo);

        drawReproductionInformation(drawInfo);
    }

    /**
     * Draw the notice that this print is intended for repro.
     *
     * @param drawInfo Draw offsets.
     */
    private void drawRepro(DrawInfo drawInfo) {
        String printRepro = getMessage("print.repro", "Intended for repro");
        drawKeyValue(drawInfo, null, printRepro, boldFont, false);
    }

    /**
     * Draws the number (id) of the reproduction.
     *
     * @param drawInfo Draw offsets.
     */
    private void drawId(DrawInfo drawInfo) {
        HoldingReproduction hr = (HoldingReproduction) holdingRequest;
        String idLabel = getMessage("reproduction.short.id", "Repr. nr");
        drawKeyValue(drawInfo, idLabel, String.valueOf(hr.getReproduction().getId()));
    }

    /**
     * Draws the number (id) of the reproduction.
     *
     * @param drawInfo Draw offsets.
     */
    private void drawPayed(DrawInfo drawInfo) {
        HoldingReproduction hr = (HoldingReproduction) holdingRequest;
        Order order = hr.getReproduction().getOrder();
        String idLabel = getMessage("reproduction.payed", "Paid?");

        if ((order != null) && (hr.getReproduction().getStatus().ordinal() >= Reproduction.Status.ACTIVE.ordinal())) {
            drawKeyValue(drawInfo, idLabel, getMessage("yes", "Yes") + " (#" + String.valueOf(order.getId()) + ")");
        }
        else {
            drawKeyValue(drawInfo, idLabel, getMessage("no", "No"));
        }
    }

    /**
     * Draws the email address of the person making the request.
     *
     * @param drawInfo Draw offsets.
     */
    private void drawEmail(DrawInfo drawInfo) {
        String nameLabel = getMessage("request.email", "Email");
        drawKeyValue(drawInfo, nameLabel, holdingRequest.getRequest().getEmail(), italicFont, false);
    }

    /**
     * Draws the reproduction information.
     *
     * @param drawInfo Draw offsets.
     */
    private void drawReproductionInformation(DrawInfo drawInfo) {
        drawInfo.setOffsetY(drawInfo.getOffsetY() + MIN_LINE_HEIGHT);

        HoldingReproduction hr = (HoldingReproduction) holdingRequest;
        if (hr.getStandardOption() != null) {
            ReproductionStandardOption standardOption = hr.getStandardOption();
            drawKeyValue(drawInfo, null, standardOption.getOptionName(), boldFont, false);
            drawKeyValue(drawInfo, null, standardOption.getOptionDescription(), italicFont, false);
        } else {
            drawKeyValue(drawInfo, null, getMessage("reproduction.customReproduction.backend", "Custom reproduction"),
                    boldFont, false);
            drawKeyValue(drawInfo, null, hr.getCustomReproductionCustomer(), italicFont, false);
        }
    }

    /**
     * Draw the PID of the holding.
     *
     * @param drawInfo Draw offsets.
     * @param value    The value to draw.
     */
    private void drawHoldingPid(DrawInfo drawInfo, String value) {
        String typeLabel = getMessage("holding.pid", "Item PID");
        drawKeyValue(drawInfo, typeLabel, value, italicFont, true);
    }
}
