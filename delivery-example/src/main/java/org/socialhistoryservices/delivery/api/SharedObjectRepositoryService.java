package org.socialhistoryservices.delivery.api;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.*;

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
     * @return Whether the SOR has metadata.
     */
    public boolean hasMetadataForPid(String pid) {
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

            // See if there is an element with the PID and make sure it matches the PID we're requesting
            Node pidNode = document.getElementsByTagName("pid").item(0);
            return pidNode.getTextContent().equals(pid);
        } catch (IOException ioe) {
            LOGGER.debug("hasMetadata(): SOR API connection failed", ioe);
            return false;
        } catch (ParserConfigurationException pce) {
            LOGGER.debug("hasMetadata(): Could not build document builder", pce);
            return false;
        } catch (SAXException saxe) {
            LOGGER.debug("hasMetadata(): Could not parse received metadata", saxe);
            return false;
        }
    }
}
