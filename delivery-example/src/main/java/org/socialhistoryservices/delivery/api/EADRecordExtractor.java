package org.socialhistoryservices.delivery.api;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.socialhistoryservices.delivery.record.entity.ExternalHoldingInfo;
import org.socialhistoryservices.delivery.record.entity.ExternalRecordInfo;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.namespace.QName;
import javax.xml.xpath.*;
import java.util.*;

public class EADRecordExtractor implements IISHRecordExtractor {
    private static final Log logger = LogFactory.getLog(EADRecordExtractor.class);

    private XPath xpath;
    private XPathExpression xpTitle, xpAuthor, xpPhysicalDescription, xpUnitId, xpDidUnitId, xpParent;

    public EADRecordExtractor() {
        XPathFactory factory = XPathFactory.newInstance();
        xpath = factory.newXPath();
        xpath.setNamespaceContext(new IISHNamespaceContext());

        try {
            xpTitle = xpath.compile("normalize-space(.//ead:unittitle)");
            xpAuthor = xpath.compile("normalize-space(.//ead:origination[@label='Creator']/ead:persname)");
            xpPhysicalDescription = xpath.compile(
                "normalize-space(.//ead:physdesc[@label='Physical Description']/ead:extent)");
            xpUnitId = xpath.compile(".//ead:unitid");
            xpDidUnitId = xpath.compile("./ead:did/ead:unitid");
            xpParent = xpath.compile("(" +
                "./ancestor::ead:c01|" +
                "./ancestor::ead:c02|" +
                "./ancestor::ead:c03|" +
                "./ancestor::ead:c04|" +
                "./ancestor::ead:c05|" +
                "./ancestor::ead:c06|" +
                "./ancestor::ead:c07|" +
                "./ancestor::ead:c08|" +
                "./ancestor::ead:c09|" +
                "./ancestor::ead:c10|" +
                "./ancestor::ead:c11|" +
                "./ancestor::ead:c12)[last()]");
        }
        catch (XPathExpressionException ex) {
            logger.error("Failed initializing XPath expressions");
        }
    }

    /**
     * Parses a node to metadata of a record.
     *
     * @param node The node to parse.
     * @return The metadata of the record, if found.
     * @throws NoSuchPidException Thrown when the PID does not exist.
     */
    @Override
    public ExternalRecordInfo getRecordMetadata(Node node) throws NoSuchPidException {
        return getRecordMetadata(node, null);
    }

    /**
     * Parses a node to metadata of a record.
     *
     * @param node The node to parse.
     * @param item The item.
     * @return The metadata of the record, if found.
     * @throws NoSuchPidException Thrown when the PID does not exist.
     */
    @Override
    public ExternalRecordInfo getRecordMetadata(Node node, String item) throws NoSuchPidException {
        ExternalRecordInfo externalInfo = new ExternalRecordInfo();

        Node itemNode = (item != null) ? findItemNode(node, item) : node;
        if (itemNode == null)
            throw new NoSuchPidException();

        String author = evaluateAuthor(node);
        if (author != null && !author.isEmpty()) {
            author = author.trim();
            externalInfo.setAuthor(stripToSize(author, 125));
        }

        String title = evaluateTitle(itemNode, (item != null));
        if (title != null && !title.isEmpty()) {
            // Strip trailing slashes
            title = title.trim();
            // Some titles from the API SRW exceed 255 characters.
            // Strip them to 251 characters (up to 252 , exclusive) to save up for the dash and \0 termination.
            // EDIT: Trim this to ~125 characters for readability (this is the current max size of the field).
            title = stripToSize(title, 125);
            externalInfo.setTitle(title);
        }
        else {
            externalInfo.setTitle("Unknown Record");
        }

        externalInfo.setMaterialType(ExternalRecordInfo.MaterialType.ARCHIVE);
        externalInfo.setPublicationStatus(ExternalRecordInfo.PublicationStatus.UNKNOWN);

        String physicalDescription = evaluatePhysicalDescription(node);
        externalInfo.setPhysicalDescription((physicalDescription != null) ? physicalDescription.trim() : null);

        return externalInfo;
    }

    /**
     * Get a map of holding signatures associated with this node,
     * linking to additional holding info provided by the API.
     *
     * @param node The node to parse.
     * @return A map of found (signature,holding info) tuples, or an empty map if none were found.
     * @throws NoSuchPidException Thrown when the PID does not exist.
     */
    @Override
    public Map<String, ExternalHoldingInfo> getHoldingMetadata(Node node) throws NoSuchPidException {
        return getHoldingMetadata(node, null);
    }

    /**
     * Get a map of holding signatures associated with this node,
     * linking to additional holding info provided by the API.
     *
     * @param node The node to parse.
     * @param item The item.
     * @return A map of found (signature,holding info) tuples, or an empty map if none were found.
     * @throws NoSuchPidException Thrown when the PID does not exist.
     */
    @Override
    public Map<String, ExternalHoldingInfo> getHoldingMetadata(Node node, String item) throws NoSuchPidException {
        Map<String, ExternalHoldingInfo> retMap = new HashMap<String, ExternalHoldingInfo>();

        Node itemNode = (item != null) ? findItemNode(node, item) : node;
        if (itemNode == null)
            throw new NoSuchPidException();

        try {
            String signature = xpUnitId.evaluate(node);
            if (item != null)
                signature += "." + item;

            ExternalHoldingInfo eh = new ExternalHoldingInfo();
            eh.setBarcode(signature);

            retMap.put(signature, eh);
        }
        catch (XPathExpressionException ignored) {
            logger.debug("getHoldingMetadata(): Invalid XPath", ignored);
        }

        return retMap;
    }

    private String stripToSize(String string, int size) {
        if (string.length() > size) {
            string = string.substring(0, size);
            string = string.trim();
        }
        return string;
    }

    private Node findItemNode(Node node, String item) {
        try {
            Node itemNode = (Node) xpath.evaluate(".//ead:dsc//ead:unitid[text()='" + item + "']",
                node, XPathConstants.NODE);
            Node parentNode = (Node) xpParent.evaluate(itemNode, XPathConstants.NODE);

            // An item node is only valid if is a leaf item node (has no children with items)
            if (((NodeList) xpUnitId.evaluate(parentNode, XPathConstants.NODESET)).getLength() == 1)
                return parentNode;
            return null;
        }
        catch (XPathExpressionException ex) {
            return null;
        }
    }

    private String evaluatePhysicalDescription(Node node) {
        try {
            return xpPhysicalDescription.evaluate(node);
        }
        catch (XPathExpressionException e) {
            return null;
        }
    }

    private String evaluateTitle(Node node, boolean isItemLevel) {
        try {
            List<String> titles = new LinkedList<String>();

            Node curNode = node;
            while ((curNode != null) && (!isItemLevel || (xpDidUnitId.evaluate(curNode, XPathConstants.NODE) != null))) {
                String title = xpTitle.evaluate(curNode);
                if (title != null)
                    titles.add(0, title.trim());
                curNode = (Node) xpParent.evaluate(curNode, XPathConstants.NODE);
            }

            return titles.isEmpty() ? null : StringUtils.join(titles, " / ");
        }
        catch (XPathExpressionException e) {
            return null;
        }
    }

    private String evaluateAuthor(Node node) {
        try {
            return xpAuthor.evaluate(node);
        }
        catch (XPathExpressionException ex) {
            return null;
        }
    }
}
