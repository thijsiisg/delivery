package org.socialhistoryservices.delivery.api;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.socialhistoryservices.delivery.record.entity.Holding;

import java.io.IOException;
import java.net.*;

/**
 * Represents the Shared Object Repository (SOR) service.
 */
public class SharedObjectRepositoryService {
    private static final Log LOGGER = LogFactory.getLog(SharedObjectRepositoryService.class);

    private String url = "";

    public SharedObjectRepositoryService() {
    }

    /**
     * Find out if the given PID has metadata in the SOR.
     *
     * @param pid The pid.
     * @return Whether the SOR has metadata.
     */
    public boolean hasMetadataForPid(String pid) {
        try {
            URL req = new URL(url + "/metadata/" + pid + "?accept=xml");

            LOGGER.debug(String.format("hasMetadata(): Querying SOR API: %s", req.toString()));
            HttpURLConnection conn = (HttpURLConnection) req.openConnection();

            conn.setRequestMethod("HEAD");
            conn.connect();

            return (conn.getResponseCode() == HttpURLConnection.HTTP_OK);
        } catch (IOException ioe) {
            LOGGER.debug("hasMetadata(): SOR API connection failed", ioe);
            return false;
        }
    }
}
