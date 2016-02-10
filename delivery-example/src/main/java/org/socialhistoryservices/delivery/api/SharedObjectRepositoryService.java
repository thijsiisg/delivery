package org.socialhistoryservices.delivery.api;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents the Shared Object Repository (SOR) service.
 */
public class SharedObjectRepositoryService {
    private static final Log LOGGER = LogFactory.getLog(SharedObjectRepositoryService.class);

    private String url;

    public SharedObjectRepositoryService(String url) {
        this.url = url;
    }

    /**
     * Find out if the given PID has metadata in the SOR.
     *
     * @param pid The pid.
     * @return The SOR has metadata, if found, for both the master and the first derivative.
     */
    public SorMetadata[] getAllMetadataForPid(String pid) {
        return getMetadataForPid(pid);
    }

    /**
     * Find out if the given PID has metadata for the master file in the SOR.
     *
     * @param pid The pid.
     * @return The SOR has metadata, if found.
     */
    public SorMetadata getMasterMetadataForPid(String pid) {
        return getMetadataForPid(pid, true);
    }

    /**
     * Find out if the given PID has metadata for the first derivative (level 1) file in the SOR.
     *
     * @param pid The pid.
     * @return The SOR has metadata, if found.
     */
    public SorMetadata getFirstLevelMetadataForPid(String pid) {
        return getMetadataForPid(pid, false);
    }

    /**
     * Find out if the given PID has metadata in the SOR, for both the master and the first derivative.
     *
     * @param pid The pid.
     * @return The SOR has metadata, if found, for both the master and the first derivative.
     */
    private SorMetadata[] getMetadataForPid(String pid) {
        try {
            URL req = new URL(url + "/metadata/" + pid + "?accept=text/xml&format=xml");

            LOGGER.debug(String.format("hasMetadata(): Querying SOR API: %s", req.toString()));
            HttpURLConnection conn = (HttpURLConnection) req.openConnection();
            conn.connect();

            // Try to parse the received XML
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setIgnoringComments(true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(conn.getInputStream());

            return new SorMetadata[]{
                    getMetadataFromDocument(document, pid, true),
                    getMetadataFromDocument(document, pid, false)
            };
        } catch (IOException ioe) {
            LOGGER.error("hasMetadata(): SOR API connection failed", ioe);
            return null;
        } catch (ParserConfigurationException pce) {
            LOGGER.debug("hasMetadata(): Could not build document builder", pce);
            return null;
        } catch (SAXException saxe) {
            LOGGER.debug("hasMetadata(): Could not parse received metadata", saxe);
            return null;
        }
    }

    /**
     * Find out if the given PID has metadata in the SOR.
     *
     * @param pid    The pid.
     * @param master Whether to obtain those of the master, or the first derivative (level 1).
     * @return The SOR has metadata, if found.
     */
    private SorMetadata getMetadataForPid(String pid, boolean master) {
        try {
            URL req = new URL(url + "/metadata/" + pid + "?accept=text/xml&format=xml");

            LOGGER.debug(String.format("hasMetadata(): Querying SOR API: %s", req.toString()));
            HttpURLConnection conn = (HttpURLConnection) req.openConnection();
            conn.connect();

            // Try to parse the received XML
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setIgnoringComments(true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(conn.getInputStream());

            return getMetadataFromDocument(document, pid, master);
        } catch (IOException ioe) {
            LOGGER.error("hasMetadata(): SOR API connection failed", ioe);
            return null;
        } catch (ParserConfigurationException pce) {
            LOGGER.debug("hasMetadata(): Could not build document builder", pce);
            return null;
        } catch (SAXException saxe) {
            LOGGER.debug("hasMetadata(): Could not parse received metadata", saxe);
            return null;
        }
    }

    /**
     * Parses the document to a SorMetadata object.
     *
     * @param document The document.
     * @param pid      The pid of the item of which we request metadata.
     * @param master   Whether to obtain metadata of the master, or the first derivative (level 1).
     * @return The metadata from the SOR.
     */
    private SorMetadata getMetadataFromDocument(Document document, String pid, boolean master) {
        // See if there is an element with the PID and make sure it matches the PID we're requesting
        Node pidNode = document.getElementsByTagName("pid").item(0);
        if (!pidNode.getTextContent().equals(pid))
            return null;

        // See if the required level exists
        String levelTagName = master ? "master" : "level1";
        NodeList levelNodes = document.getElementsByTagName(levelTagName);
        if ((levelNodes.getLength() != 1) || (levelNodes.item(0).getNodeType() != Node.ELEMENT_NODE))
            return null;

        // Obtain the various metadata
        Element levelElement = (Element) levelNodes.item(0);
        NodeList contentTypeNodes = levelElement.getElementsByTagName("contentType");
        NodeList contentNodes = levelElement.getElementsByTagName("content");

        String contentType = null;
        if ((contentTypeNodes.getLength() == 1) && (contentTypeNodes.item(0).getNodeType() == Node.ELEMENT_NODE)) {
            Element contentTypeElement = (Element) contentTypeNodes.item(0);
            contentType = contentTypeElement.getTextContent();
        }

        Map<String, String> contentAttributes = new HashMap<String, String>();
        if ((contentNodes.getLength() == 1) && (contentNodes.item(0).getNodeType() == Node.ELEMENT_NODE)) {
            Element contentElement = (Element) contentNodes.item(0);
            if (contentElement.hasAttributes()) {
                NamedNodeMap contentElementAttributes = contentElement.getAttributes();
                for (int i = 0; i < contentElementAttributes.getLength(); i++) {
                    Node contentElementAttribute = contentElementAttributes.item(i);
                    contentAttributes.put(
                            contentElementAttribute.getNodeName(),
                            contentElementAttribute.getNodeValue()
                    );
                }
            }
        }

        return new SorMetadata(master, contentType, contentAttributes);
    }
}
